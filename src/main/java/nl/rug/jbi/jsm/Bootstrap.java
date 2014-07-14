package nl.rug.jbi.jsm;

import com.google.common.collect.Sets;
import nl.rug.jbi.jsm.core.JSMCore;
import nl.rug.jbi.jsm.core.pipeline.MetricPreparationException;
import nl.rug.jbi.jsm.frontend.Frontend;
import nl.rug.jbi.jsm.frontend.GUIFrontend;
import nl.rug.jbi.jsm.frontend.ScriptFrontend;
import nl.rug.jbi.jsm.metrics.ckjm.CKJM;
import nl.rug.jbi.jsm.metrics.packagemetrics.PackageMetrics;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.Set;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * Application bootstrapper for JSM, will start either {@link nl.rug.jbi.jsm.frontend.GUIFrontend} or
 * {@link nl.rug.jbi.jsm.frontend.ScriptFrontend} based on the arguments given to the program.
 *
 * @author David van Leusen
 * @since 2014-06-01
 */
public class Bootstrap {
    private static final Logger logger = LogManager.getLogger(Bootstrap.class);

    public static void main(final String[] args) throws IOException {
        logger.trace("Beginning startup");

        final JSMCore core = new JSMCore();

        try {
            //CKJM
            core.registerMetricCollection(new CKJM());
            //Package metrics
            core.registerMetricCollection(new PackageMetrics());
        } catch (MetricPreparationException e) {
            logger.error("Error loading metric definitions", e);
            if (!GraphicsEnvironment.isHeadless()) {
                JOptionPane.showMessageDialog(
                        null,
                        e.getMessage(),
                        "Error loading metrics, see logs for more information",
                        JOptionPane.ERROR_MESSAGE
                );
            }
            System.exit(-1);
        }

        Bootstrap.bootstrapJSM(core, args);
    }

    /**
     * Parses the input arguments and starts the appropriate frontend.
     * The core should not be modified after being passed to this method.
     *
     * @param core      Execution core with the required metrics already registered.
     * @param arguments Commandline arguments
     * @throws IOException If the underlying frontends throw an IOException
     */
    public static void bootstrapJSM(final JSMCore core, final String[] arguments) throws IOException {
        final Frontend frontend;
        if (arguments.length > 0) {
            //Script Mode
            final Set<String> input = Sets.newHashSet();
            final Set<String> libraries = Sets.newHashSet();
            final Set<String> output = Sets.newHashSet();
            boolean groupExportScopes = false;

            Set<String> selectedSet = Sets.newHashSet();

            for (final String arg : arguments) {
                if ("--in".equalsIgnoreCase(arg)) {
                    selectedSet = input;
                } else if ("--lib".equalsIgnoreCase(arg)) {
                    selectedSet = libraries;
                } else if ("--out".equalsIgnoreCase(arg)) {
                    selectedSet = output;
                } else if ("--group-scopes".equalsIgnoreCase(arg)) {
                    groupExportScopes = true;
                } else {
                    selectedSet.add(arg);
                }
            }

            checkArgument(!input.isEmpty(), "Scriptable mode requires a specified input. (--in)");
            checkArgument(!output.isEmpty(), "Scriptable mode requires a specified output. (--out)");

            frontend = new ScriptFrontend(core, input, libraries, output, groupExportScopes);
        } else {
            //GUI mode
            frontend = new GUIFrontend(core);
        }

        frontend.init();
        logger.trace("Startup finished");
    }
}
