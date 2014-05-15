package nl.rug.jbi.jsm.bcel;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;
import java.util.Map;

/**
 * Partial implementation of a composite class loader.
 * The primary purpose of this class is to act as a source for {@link org.apache.bcel.classfile.JavaClass} within the
 * BCEL library.
 * It also records the source of the class files so this information can be used for collection-based metrics.
 * <p></p>
 * Be aware that this class is not a functional class loader, it only implements what is required to provide
 * functionality to BCEL.
 *
 * @author David van Leusen
 * @since 1.0
 */
public class CompositeBCELClassLoader extends ClassLoader {
    private final List<ClassLoader> classLoaders = Lists.newLinkedList();
    private final Map<ClassLoader, String> sourceMap = Maps.newHashMap();
    private final Map<String, String> classSourceMap = Maps.newHashMap();

    /**
     * Construct a composite class loader which draws its classes from the sources provided.
     * <p></p>
     * Internally the provided URLs are converted into class-loaders with reverse-mappings to identifiable names.
     *
     * @param externalSources URL objects referencing outside resources, defined for URLs representing directories
     *                        and JAR archives.
     * @see java.net.URLClassLoader
     */
    public CompositeBCELClassLoader(final URL[] externalSources) {
        for (final URL url : externalSources) {
            this.addInternal(url.toString(), URLClassLoader.newInstance(new URL[]{url}, null));
        }
        this.addInternal("Application", getClass().getClassLoader());
        this.addInternal("Bootstrap", ClassLoader.getSystemClassLoader());
    }

    private void addInternal(final String identifier, final ClassLoader classLoader) {
        this.classLoaders.add(classLoader);
        this.sourceMap.put(classLoader, identifier);
    }

    /**
     * Retrieve a string identifying the location a class was loaded from, requires the class to be requested by BCEL
     * before this call will succeed.
     *
     * @param className Name of the class to look up
     * @return String identifying the resource the requested class was retrieved from.
     * @throws java.lang.NullPointerException If the class was not previously loaded through this class loader.
     */
    public synchronized String getSource(final String className) {
        return Preconditions.checkNotNull(this.classSourceMap.get(className), "Class unknown");
    }

    private void storeMapping(final String classFileName, final String source) {
        final String editedClassName = classFileName.split("\\.")[0].replaceAll("/", ".");
        this.classSourceMap.put(editedClassName, source);
    }

    /**
     * Looks for class files by linear search though the provided resources, if the class cannot be found in the
     * provided locations, the {@link ClassLoader#getSystemClassLoader()} will be queried.
     * <p></p>
     * {@inheritDoc}
     */
    @Override
    public synchronized InputStream getResourceAsStream(final String name) {
        for (final ClassLoader cl : this.classLoaders) {
            final InputStream resource = cl.getResourceAsStream(name);
            if (resource != null) {
                this.storeMapping(name, this.sourceMap.get(cl));
                return resource; //Return first resource
            }
        }

        //Final attempt to retrieve a class
        final InputStream resource = Thread.currentThread().getContextClassLoader().getResourceAsStream(name);
        if (resource != null) {
            this.storeMapping(name, "ContextClassLoader");
            return resource;
        } else {
            return null;
        }
    }
}
