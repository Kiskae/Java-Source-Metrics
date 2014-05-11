package nl.rug.jbi.jsm;

import nl.rug.jbi.jsm.frontend.Frontend;
import nl.rug.jbi.jsm.frontend.GUIFrontend;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

public class Bootstrap {
    private static final Logger logger = LogManager.getLogger(Bootstrap.class);

    public static void main(final String[] args) throws IOException, ClassNotFoundException {
        logger.trace("Beginning startup");

        /*
        final JSMCore core = new JSMCore();

        final String className = "org.apache.bcel.util.ClassLoaderRepository";

        final HandlerMap hMap = new HandlerMap.Builder().addIsolatedMetric(new WMC()).build();
        logger.debug(hMap);

        final EventBus eBus = new EventBus(className, hMap);
        final ClassLoaderRepository repo = new ClassLoaderRepository(Thread.currentThread().getContextClassLoader());
        new ClassVisitor(repo.loadClass(className), eBus).start();
        */

        //TODO: parse arguments

        //TODO: potentially init core?
        final Frontend frontend = new GUIFrontend();
        frontend.init();

        logger.trace("Startup finished");
    }
}
