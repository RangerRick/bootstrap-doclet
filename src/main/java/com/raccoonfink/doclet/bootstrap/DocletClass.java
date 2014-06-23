package com.raccoonfink.doclet.bootstrap;

import java.util.LinkedList;
import java.util.List;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.javadoc.ClassDoc;

public class DocletClass {
    private static final Logger LOG = LoggerFactory.getLogger(DocletClass.class);
    private final ClassDoc m_classDoc;

    public DocletClass(final ClassDoc classDoc) {
        m_classDoc = classDoc;
        LOG.debug("DocletClass: {}", getName());
    }

    private ClassDoc getClassDoc() {
        return m_classDoc;
    }
    
    final JSONObject toJSON() {
        final JSONObject o = toPackageJSON();

        for (final DocletClass dc : getAncestors()) {
            o.append("ancestors", dc.toPackageJSON());
        }

        for (final DocletClass dc : getInterfaces()) {
            o.append("interfaces", dc.toPackageJSON());
        }
        return o;
    }

    final JSONObject toPackageJSON() {
        final JSONObject o = new JSONObject();
        o.put("package", getPackageName());
        o.put("name", getClassName());
        return o;
    }
    public String getClassName() {
        ClassDoc cd = m_classDoc;
        
        if (cd == null) return null;
        String name = cd.name();
        while (cd.containingClass() != null) {
            cd = cd.containingClass();
            name = cd.name() + "." + name;
        }
        return name;
    }

    public String getPackageName() {
        if (m_classDoc == null) return null;
        if (m_classDoc.containingPackage() == null) return null;
        return m_classDoc.containingPackage().name();
    }

    public String getName() {
        return getPackageName() + "." + getClassName();
    }

    /**
     * Get the list of ancestors to this class, in order of farthest to closest
     * @return a list of {@link DocletClass} objects
     */
    public List<DocletClass> getAncestors() {
        final LinkedList<DocletClass> classes = new LinkedList<DocletClass>();

        if (m_classDoc == null || m_classDoc.superclass() == null) {
            return classes;
        }

        DocletClass dc = new DocletClass(m_classDoc.superclass());
        while (dc != null && dc.getName() != null) {
            classes.push(dc);

            final ClassDoc classDoc = dc.getClassDoc();
            final ClassDoc superclass = classDoc == null? null : classDoc.superclass();
            if (superclass == null) {
                dc = null;
            } else {
                dc = new DocletClass(superclass);
            }
        }

        return classes;
    }
    
    public List<DocletClass> getInterfaces() {
        final LinkedList<DocletClass> classes = new LinkedList<DocletClass>();

        if (m_classDoc == null || m_classDoc.interfaces() == null) {
            return classes;
        }

        for (final ClassDoc doc : m_classDoc.interfaces()) {
            classes.add(new DocletClass(doc));
        }

        return classes;
    }
}
