package nl.rug.jbi.jsm;

import nl.rug.jbi.jsm.core.JSMCore;
import nl.rug.jbi.jsm.core.pipeline.MetricPreparationException;
import nl.rug.jbi.jsm.frontend.DebugFrontend;
import nl.rug.jbi.jsm.frontend.Frontend;
import nl.rug.jbi.jsm.frontend.GUIFrontend;
import nl.rug.jbi.jsm.metrics.ckjm.*;
import nl.rug.jbi.jsm.metrics.packagemetrics.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;

public class Bootstrap {
    private static final Logger logger = LogManager.getLogger(Bootstrap.class);

    public static void main(final String[] args) throws MetricPreparationException {
        logger.trace("Beginning startup");

        //TODO: parse arguments

        final JSMCore core = new JSMCore();

        //CKJM
        core.registerMetric(new WMC());
        core.registerMetric(new NOC());
        core.registerMetric(new DIT());
        core.registerMetric(new NPM());
        core.registerMetric(new RFC());
        core.registerMetric(new LCOM());
        core.registerMetric(new CBO());
        core.registerMetric(new CA());

        //Package metrics
        core.registerMetric(new PF());
        core.registerMetric(new IIPU());
        core.registerMetric(new IIPE());
        core.registerMetric(new IPCI());
        core.registerMetric(new IIPUD());
        core.registerMetric(new IIPED());
        core.registerMetric(new IPSC());

        final Frontend frontend;
        if (Arrays.asList(args).contains("--nogui") && args.length > 1)
            frontend = new DebugFrontend(core, args[1]);
        else
            frontend = new GUIFrontend(core);
        frontend.init();

        logger.trace("Startup finished");
    }
}
