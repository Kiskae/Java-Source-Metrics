package nl.rug.jbi.jsm;

import nl.rug.jbi.jsm.bcel.ClassVisitor;
import nl.rug.jbi.jsm.core.JSMCore;
import nl.rug.jbi.jsm.core.event.EventBus;
import nl.rug.jbi.jsm.core.pipeline.HandlerMap;
import org.apache.bcel.util.ClassLoaderRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

public class Bootstrap {
    private static final Logger logger = LogManager.getLogger(Bootstrap.class);

    public static void main(final String[] args) throws IOException, ClassNotFoundException {
        logger.trace("Beginning startup");

        final JSMCore core = new JSMCore();

        final String className = "nl.rug.jbi.jsm.bcel.ClassVisitor";

        final EventBus eBus = new EventBus(className, new HandlerMap());
        final ClassLoaderRepository repo = new ClassLoaderRepository(Thread.currentThread().getContextClassLoader());
        new ClassVisitor(repo.loadClass(className), eBus).start();

        /*
        //TODO: parse arguments

        //TODO: potentially init core?
        final JSMCore core = new JSMCore();

        core.registerMetric(new WMC());
        core.registerMetric(new NOC());
        core.registerMetric(new PF());
        core.registerMetric(new IIPU());

        final Frontend frontend = new DebugFrontend(core);
        frontend.init();
                    */
        logger.trace("Startup finished");
    }
}
