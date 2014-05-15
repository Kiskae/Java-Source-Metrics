package nl.rug.jbi.jsm.core;

import com.google.common.base.Preconditions;
import nl.rug.jbi.jsm.core.calculator.BaseMetric;
import nl.rug.jbi.jsm.core.pipeline.Pipeline;
import nl.rug.jbi.jsm.frontend.Frontend;
import nl.rug.jbi.jsm.bcel.CompositeBCELClassLoader;
import org.apache.bcel.util.ClassLoaderRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URL;
import java.util.Set;

public class JSMCore {
    private final static Logger logger = LogManager.getLogger(JSMCore.class);
    private final Pipeline pipe = new Pipeline();
    private volatile boolean running = false;

    public void registerMetric(final BaseMetric metric) {
        this.pipe.registerMetric(metric);
    }

    public void process(final Frontend frontend, final Set<String> classNames, final URL... sources) {
        Preconditions.checkState(!this.running, "Processing already in process.");
        this.running = true;

        final CompositeBCELClassLoader ccl = new CompositeBCELClassLoader(sources);
        final ClassLoaderRepository repo = new ClassLoaderRepository(ccl);
    }
}
