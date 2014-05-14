package nl.rug.jbi.jsm.util;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;
import java.util.Map;

public class CompositeBCELClassLoader extends ClassLoader {
    private final List<ClassLoader> classLoaders = Lists.newLinkedList();
    private final Map<ClassLoader, String> sourceMap = Maps.newHashMap();
    private final Map<String, String> classSourceMap = Maps.newHashMap();

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

    public synchronized String getSource(final String className) {
        return Preconditions.checkNotNull(this.classSourceMap.get(className));
    }

    private void storeMapping(final String classFileName, final String source) {
        final String editedClassName = classFileName.split("\\.")[0].replaceAll("/", ".");
        this.classSourceMap.put(editedClassName, source);
    }

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
