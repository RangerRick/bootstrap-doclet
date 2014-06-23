package com.raccoonfink.doclet.bootstrap;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.Doclet;
import com.sun.javadoc.LanguageVersion;
import com.sun.javadoc.RootDoc;

public class BootstrapDoclet extends Doclet {
    private static final Logger LOG = LoggerFactory.getLogger(BootstrapDoclet.class);

    private final RootDoc m_root;

    protected BootstrapDoclet(final RootDoc root) {
        m_root = root;
    }

    public static boolean start(final RootDoc root) {
        try {
            new BootstrapDoclet(root).generateDocumentation();
        } catch (final Exception e) {
            System.err.println("Failed to generate documentation: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static LanguageVersion languageVersion() {
        return LanguageVersion.JAVA_1_5;
    }

    public static int optionLength(final String option) {
        final String o = option.toLowerCase();
        if ("-author".equals(o)) {
            return 1;
        } else if ("-bottom".equals(o)) {
            return 2;
        } else if ("-charset".equals(o)) {
            return 2;
        } else if ("-d".equals(o)) {
            return 2;
        } else if ("-docencoding".equals(o)) {
            return 2;
        } else if ("-doctitle".equals(o)) {
            return 2;
        } else if ("-linkoffline".equals(o)) {
            return 3;
        } else if ("-use".equals(o)) {
            return 1;
        } else if ("-version".equals(o)) {
            return 1;
        } else if ("-windowtitle".equals(o)) {
            return 2;
        }
        return 0;
    }

    private void generateDocumentation() throws IOException {
        final Map<String,List<String>> options = new HashMap<String,List<String>>();
        for (final String[] option : m_root.options()) {
            final List<String> entry = new ArrayList<String>(Arrays.asList(option));
            LOG.debug("Command-line option: {}", entry);
            if (entry.size() > 0) {
                final String key = entry.remove(0);
                options.put(key, entry);
            } else {
                LOG.warn("Options was empty?  Weird.");
            }
        }


        final File outputDirectory = new File(options.get("-d").get(0));
        //LOG.debug("Copying web resources to output directory: {}", outputDirectory);
        final URL webZip = getClass().getResource("/web.zip");
        final File webZipFile = File.createTempFile("web", ".zip");

        //LOG.debug("Temporary output file = {}", webZipFile);
        final FileOutputStream writer = new FileOutputStream(webZipFile);
        IOUtils.copy(webZip.openStream(), writer);
        writer.close();

        final ZipFile zipFile = new ZipFile(webZipFile);
        final Enumeration<? extends ZipEntry> entries = zipFile.entries();
        while (entries.hasMoreElements()) {
            final ZipEntry entry = entries.nextElement();
            if (entry.getName().contains("META-INF")) {
                continue;
            }

            //LOG.debug("entry = {}", entry);
            final File newFile = new File(outputDirectory, entry.getName());
            if (entry.isDirectory()) {
                //LOG.debug("Making directory: {}", newFile);
                newFile.mkdirs();
            } else {
                //LOG.debug("Unpacking: {}", newFile);
                final FileWriter newFileWriter = new FileWriter(newFile);
                IOUtils.copy(zipFile.getInputStream(entry), newFileWriter);
                newFileWriter.close();
            }
        }

        for (final ClassDoc cd : m_root.classes()) {
            LOG.debug("ClassDoc: {}", cd);
            
            final DocletClass c = new DocletClass(cd);

            final File classFile = new File(outputDirectory, "data/" + c.getName() + ".json");
            final FileWriter fw = new FileWriter(classFile);
            fw.write(c.toJSON().toString());
            fw.close();
        }

        throw new UnsupportedOperationException("Not yet implemented!");
    }
}
