package nl.rug.jbi.jsm;

import nl.rug.jbi.jsm.core.JSMCore;
import nl.rug.jbi.jsm.metrics.ckjm.WMC;
import nl.rug.jbi.jsm.metrics.packagemetrics.PF;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Bootstrap {
    private static final Logger logger = LogManager.getLogger(Bootstrap.class);

    public static void main(final String[] args) {
        logger.trace("Starting Application");

        final JSMCore core = new JSMCore();
        core.registerMetric(new WMC());
        core.registerMetric(new PF());

        logger.trace("Exiting Application");
    }
}
