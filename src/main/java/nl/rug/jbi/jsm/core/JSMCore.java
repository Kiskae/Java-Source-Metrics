package nl.rug.jbi.jsm.core;

import nl.rug.jbi.jsm.core.calculator.BaseMetric;
import nl.rug.jbi.jsm.frontend.Frontend;
import nl.rug.jbi.jsm.util.CompositeBCELClassLoader;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.util.ClassLoaderRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URL;
import java.util.List;

public class JSMCore {
    private final static Logger logger = LogManager.getLogger(JSMCore.class);

    public void registerMetric(final BaseMetric metric) {

    }

    public void process(final Frontend frontend, final List<String> classNames, final URL... sources) {
        final CompositeBCELClassLoader ccl = new CompositeBCELClassLoader(sources);
        final ClassLoaderRepository repo = new ClassLoaderRepository(ccl);
        for (final String className : classNames) {
            try {
                final JavaClass jc = repo.loadClass(className);
                logger.debug("[{}] {}", ccl.getSource(jc.getClassName()), jc);
            } catch (ClassNotFoundException e) {
                logger.warn(e);
            }
        }
    }
}
