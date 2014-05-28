package nl.rug.jbi.jsm;

import com.google.common.collect.Sets;
import nl.rug.jbi.jsm.core.JSMCore;
import nl.rug.jbi.jsm.core.pipeline.MetricPreparationException;
import nl.rug.jbi.jsm.frontend.DebugFrontend;
import nl.rug.jbi.jsm.frontend.Frontend;
import nl.rug.jbi.jsm.frontend.GUIFrontend;
import nl.rug.jbi.jsm.frontend.ScriptFrontend;
import nl.rug.jbi.jsm.metrics.ckjm.CKJM;
import nl.rug.jbi.jsm.metrics.packagemetrics.PackageMetrics;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.Arrays;
import java.util.Set;

public class Bootstrap {
    private static final Logger logger = LogManager.getLogger(Bootstrap.class);

    public static void main(final String[] args) throws MetricPreparationException, IOException {
        logger.trace("Beginning startup");

        final JSMCore core = new JSMCore();

        //CKJM
        core.registerMetricCollection(new CKJM());
        //Package metrics
        core.registerMetricCollection(new PackageMetrics());

        final Frontend frontend;
        if (Arrays.asList(args).contains("--nogui") && args.length > 1) {
            //Debug Mode
            frontend = new DebugFrontend(core, args[1]);
        } else if (args.length > 0) {
            //Script Mode
            final Set<String> input = Sets.newHashSet();
            final Set<String> libraries = Sets.newHashSet();
            final Set<String> output = Sets.newHashSet();

            Set<String> selectedSet = Sets.newHashSet();

            for (final String arg : args) {
                if ("--in".equalsIgnoreCase(arg)) {
                    selectedSet = input;
                } else if ("--lib".equalsIgnoreCase(arg)) {
                    selectedSet = libraries;
                } else if ("--out".equalsIgnoreCase(arg)) {
                    selectedSet = output;
                } else {
                    selectedSet.add(arg);
                }
            }

            frontend = new ScriptFrontend(core, input, libraries, output);
        } else {
            //GUI mode
            frontend = new GUIFrontend(core);
        }

        frontend.init();

        logger.trace("Startup finished");
    }
}
