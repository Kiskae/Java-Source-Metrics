package nl.rug.jbi.jsm.util;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 *
 */
public class FileUtils {

    /**
     *
     * @param file
     * @return
     * @throws IOException
     */
    public static List<String> findClassNames(final File file) throws IOException {
        if (file.getName().endsWith(".jar")) {
            return findClassNamesJar(file);
        } else {
            return findClassNamesDir(file);
        }
    }

    private static List<String> findClassNamesDir(final File file) {
        if (file.exists() && file.isDirectory()) {
            return recursiveFindClassFiles(file, "");
        } else {
            if (file.getName().endsWith(".class")) {
                return ImmutableList.of(file2className(file.getName()));
            } else {
                return ImmutableList.of();
            }
        }
    }

    private static List<String> recursiveFindClassFiles(final File directory, final String prefix) {
        List<String> result = Lists.newLinkedList();
        for (final File f : directory.listFiles()) {
            if (f.isDirectory()) {
                result.addAll(recursiveFindClassFiles(f, prefix + f.getName() + "/"));
            } else if (f.getName().endsWith(".class")) {
                result.add(file2className(prefix + f.getName()));
            }
        }
        return result;
    }

    private static List<String> findClassNamesJar(final File file) throws IOException {
        final ZipFile zipFile = new ZipFile(file);
        final Enumeration<? extends ZipEntry> enumeration = zipFile.entries();
        final List<String> classNames = Lists.newLinkedList();
        while (enumeration.hasMoreElements()) {
            final ZipEntry entry = enumeration.nextElement();
            final String name = entry.getName();
            if (!name.endsWith(".class")) continue;
            classNames.add(file2className(name));
        }
        return classNames;
    }

    private static String file2className(final String fileName) {
        return fileName.substring(0, fileName.length() - 6).replaceAll("/", ".");
    }
}
