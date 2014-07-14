package nl.rug.jbi.jsm.util;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.util.ClassLoaderRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Extended version of the default BCEL {@link org.apache.bcel.util.ClassLoaderRepository}.
 * <br>
 * The original version uses a hash map to store the previously loaded classes, this creates a java memory leak for
 * larger inspections, since those classes never get released during execution.
 * <br>
 * This implementation uses Guava's {@link com.google.common.cache.Cache} with soft-reference values so the JVM can
 * reclaim memory if it needs to, at the expense of slightly increasing class lookup time through this class.
 *
 * @author David van Leusen
 * @since 2014-07-14
 */
public class SoftValueClassLoaderRepository extends ClassLoaderRepository {
    private final static Logger logger = LogManager.getLogger(SoftValueClassLoaderRepository.class);
    private final Cache<String, JavaClass> classCache = CacheBuilder.newBuilder()
            .softValues()
            .build();

    /**
     * @param loader The class loader from which to load class-data.
     */
    public SoftValueClassLoaderRepository(final ClassLoader loader) {
        super(loader);
        super.clear();
    }

    @Override
    public void storeClass(JavaClass clazz) {
        logger.debug("Loaded '{}'", clazz.getClassName());
        classCache.put(clazz.getClassName(), clazz);
        clazz.setRepository(this);
    }

    @Override
    public void removeClass(JavaClass clazz) {
        classCache.invalidate(clazz.getClassName());
    }

    @Override
    public JavaClass findClass(String className) {
        return classCache.getIfPresent(className);
    }

    @Override
    public void clear() {
        classCache.invalidateAll();
    }
}
