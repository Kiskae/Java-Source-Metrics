package nl.rug.jbi.jsm;

import nl.rug.jbi.jsm.bcel.ClassVisitor;
import nl.rug.jbi.jsm.core.EventBus;
import nl.rug.jbi.jsm.core.JSMCore;
import nl.rug.jbi.jsm.metrics.ckjm.WMC;
import nl.rug.jbi.jsm.metrics.packagemetrics.PF;
import org.apache.bcel.util.ClassLoaderRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

public class Bootstrap {
    private static final Logger logger = LogManager.getLogger(Bootstrap.class);

    public static void main(final String[] args) throws IOException, ClassNotFoundException {
        logger.trace("Starting Application");

        final JSMCore core = new JSMCore();
        core.registerMetric(new WMC());
        core.registerMetric(new PF());

        final EventBus eBus = new EventBus();
        final ClassLoaderRepository repo = new ClassLoaderRepository(Thread.currentThread().getContextClassLoader());
        new ClassVisitor(repo.loadClass("nl.rug.jbi.jsm.metrics.packagemetrics.PF"), eBus).start();

        logger.trace("Exiting Application");
    }
}
