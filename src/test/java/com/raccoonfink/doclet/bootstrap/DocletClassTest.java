package com.raccoonfink.doclet.bootstrap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.PackageDoc;

public class DocletClassTest {
    private static final Logger LOG = LoggerFactory.getLogger(DocletClassTest.class);

    @Before
    public void setUp() throws Exception {
        LOG.debug("----------");
    }

    @Test
    public void testPackageName() {
        final PackageDoc pd = mock(PackageDoc.class);
        when(pd.name()).thenReturn("org.opennms");

        final ClassDoc cd = mock(ClassDoc.class);
        when(cd.containingPackage()).thenReturn(pd);

        final DocletClass dc = new DocletClass(cd);

        final String json = dc.toJSON().toString();
        assertTrue(json.contains("\"package\":\"org.opennms\""));
    }

    @Test
    public void testClass() {
        final PackageDoc pd = mock(PackageDoc.class);
        when(pd.name()).thenReturn("org.opennms");

        final ClassDoc cd = mock(ClassDoc.class);
        when(cd.containingPackage()).thenReturn(pd);
        when(cd.name()).thenReturn("Monkey");

        final DocletClass dc = new DocletClass(cd);

        final String json = dc.toJSON().toString();
        assertTrue(json.contains("\"name\":\"Monkey\""));
    }

    @Test
    public void testSubClass() {
        final PackageDoc pd = mock(PackageDoc.class);
        when(pd.name()).thenReturn("org.opennms");

        final ClassDoc containingClass = mock(ClassDoc.class);
        when(containingClass.containingPackage()).thenReturn(pd);
        when(containingClass.name()).thenReturn("Shoe");

        final ClassDoc cd = mock(ClassDoc.class);
        when(cd.containingPackage()).thenReturn(pd);
        when(cd.name()).thenReturn("Monkey");
        when(cd.containingClass()).thenReturn(containingClass);

        final DocletClass dc = new DocletClass(cd);

        final String json = dc.toJSON().toString();
        assertTrue(json.contains("\"name\":\"Shoe.Monkey\""));
    }
    
    @Test
    public void testAncestors() {
        final PackageDoc pd = mock(PackageDoc.class);
        when(pd.name()).thenReturn("org.opennms");

        final ClassDoc mammal = mock(ClassDoc.class);
        when(mammal.containingPackage()).thenReturn(pd);
        when(mammal.name()).thenReturn("Mammal");

        final ClassDoc primate = mock(ClassDoc.class);
        when(primate.containingPackage()).thenReturn(pd);
        when(primate.name()).thenReturn("Primate");
        when(primate.superclass()).thenReturn(mammal);

        final ClassDoc cd = mock(ClassDoc.class);
        when(cd.containingPackage()).thenReturn(pd);
        when(cd.name()).thenReturn("Monkey");
        when(cd.superclass()).thenReturn(primate);

        final DocletClass dc = new DocletClass(cd);

        List<DocletClass> ancestors = dc.getAncestors();
        assertEquals(2, ancestors.size());
        assertEquals(ancestors.get(0).getName(), "org.opennms.Mammal");
        assertEquals(ancestors.get(1).getName(), "org.opennms.Primate");

        final String json = dc.toJSON().toString();
        assertTrue(json.contains("\"ancestors\":["));
        // mammal, then primate
        assertTrue(json.matches(".*Mammal.*Primate.*"));
    }

    @Test
    public void testInterfaces() {
        final PackageDoc pd = mock(PackageDoc.class);
        when(pd.name()).thenReturn("org.opennms");

        final ClassDoc iface = mock(ClassDoc.class);
        when(iface.containingPackage()).thenReturn(pd);
        when(iface.name()).thenReturn("Blah");

        final ClassDoc cd = mock(ClassDoc.class);
        when(cd.containingPackage()).thenReturn(pd);
        when(cd.name()).thenReturn("Foo");
        when(cd.interfaces()).thenReturn(new ClassDoc[] { iface });

        final DocletClass dc = new DocletClass(cd);

        final String json = dc.toJSON().toString();
        assertTrue(json.contains("\"interfaces\":["));
        assertTrue(json.contains("\"name\":\"Blah\""));
    }

}
