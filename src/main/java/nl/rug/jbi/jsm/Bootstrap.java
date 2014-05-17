package nl.rug.jbi.jsm;

import nl.rug.jbi.jsm.core.JSMCore;
import nl.rug.jbi.jsm.core.pipeline.MetricPreparationException;
import nl.rug.jbi.jsm.frontend.DebugFrontend;
import nl.rug.jbi.jsm.frontend.Frontend;
import nl.rug.jbi.jsm.frontend.GUIFrontend;
import nl.rug.jbi.jsm.metrics.TestMetric;
import nl.rug.jbi.jsm.metrics.ckjm.DIT;
import nl.rug.jbi.jsm.metrics.ckjm.NOC;
import nl.rug.jbi.jsm.metrics.ckjm.WMC;
import nl.rug.jbi.jsm.metrics.packagemetrics.IIPU;
import nl.rug.jbi.jsm.metrics.packagemetrics.PF;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Bootstrap {
    private static final Logger logger = LogManager.getLogger(Bootstrap.class);

    public static void main(final String[] args) throws MetricPreparationException {
        logger.trace("Beginning startup");

        //TODO: parse arguments

        final JSMCore core = new JSMCore();

        core.registerMetric(new TestMetric());
        core.registerMetric(new WMC());
        core.registerMetric(new NOC());
        core.registerMetric(new DIT());
        core.registerMetric(new PF());
        core.registerMetric(new IIPU());

        final Frontend frontend = new GUIFrontend(core);
        frontend.init();

        logger.trace("Startup finished");
    }
}
