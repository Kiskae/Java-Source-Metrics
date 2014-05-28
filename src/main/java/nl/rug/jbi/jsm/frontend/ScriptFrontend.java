package nl.rug.jbi.jsm.frontend;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.*;
import nl.rug.jbi.jsm.core.JSMCore;
import nl.rug.jbi.jsm.core.calculator.MetricResult;
import nl.rug.jbi.jsm.core.calculator.MetricScope;
import nl.rug.jbi.jsm.util.FileUtils;
import nl.rug.jbi.jsm.util.ResultsExporter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Frontend for use of JSM in scripts, it calculates the metrics for the given targets and exports the results
 * to the given output.
 *
 * @author David van Leusen
 * @since 2014-05-28
 */
public class ScriptFrontend implements Frontend {
    private final static Logger logger = LogManager.getLogger(ScriptFrontend.class);

    private final JSMCore core;

    private final Set<String> input;
    private final Set<String> libraries;

    private final Table<String, Class, Object> resultsClass = HashBasedTable.create();
    private final Table<String, Class, Object> resultsPackage = HashBasedTable.create();
    private final Table<String, Class, Object> resultsCollection = HashBasedTable.create();

    private final ResultsExporter exporter;

    /**
     * Create a new ScriptFrontend
     *
     * @param core JSMCore containing the metrics it needs to calculate.
     * @param input List of targets for calculation.
     * @param libraries List of additional targets for use as library.
     * @param output Possible outputs, will be linearly searched for the first non-used target.
     * @throws IOException If all of the outputs are already in use.
     */
    public ScriptFrontend(
            final JSMCore core,
            final Set<String> input,
            final Set<String> libraries,
            final Set<String> output
    ) throws IOException {
        this.core = core;
        this.input = input;
        this.libraries = libraries;
        this.exporter = getOutputWriter(output);

        logger.info("Exporting to {}", this.exporter);
    }

    private static void exportData(
            final ResultsExporter exporter,
            final Table<String, Class, Object> results,
            final MetricScope scope
    ) throws IOException {
        for (final Map.Entry<Class, Map<String, Object>> entry : results.columnMap().entrySet()) {
            exporter.exportData(entry.getKey(), scope, entry.getValue());
        }
    }

    @Override
    public void init() {
        final Set<String> classNames = FluentIterable.from(input)
                .transform(new Function<String, File>() {
                    @Override
                    public File apply(String fileName) {
                        return new File(fileName);
                    }
                })
                .filter(new Predicate<File>() {
                    @Override
                    public boolean apply(File file) {
                        return file.exists();
                    }
                })
                .transformAndConcat(new Function<File, Iterable<String>>() {
                    @Override
                    public Iterable<String> apply(File file) {
                        try {
                            return FileUtils.findClassNames(file);
                        } catch (IOException e) {
                            return ImmutableList.of();
                        }
                    }
                }).toSet();

        final URL[] dataSources = FluentIterable.from(Sets.union(input, libraries))
                .transform(new Function<String, File>() {
                    @Override
                    public File apply(String fileName) {
                        return new File(fileName);
                    }
                })
                .filter(new Predicate<File>() {
                    @Override
                    public boolean apply(File file) {
                        return file.exists();
                    }
                })
                .transform(new Function<File, URL>() {
                    @Override
                    public URL apply(File file) {
                        try {
                            return file.toURI().toURL();
                        } catch (MalformedURLException e) {
                            return null;
                        }
                    }
                })
                .filter(new Predicate<URL>() {
                    @Override
                    public boolean apply(URL url) {
                        return url != null;
                    }
                }).toArray(URL.class);

        core.process(this, classNames, dataSources);
    }

    @Override
    public synchronized void processResult(List<MetricResult> resultList) {
        for (final MetricResult result : resultList) {
            final Table<String, Class, Object> targetTable;
            switch (result.getScope()) {
                case CLASS:
                    targetTable = resultsClass;
                    break;
                case PACKAGE:
                    targetTable = resultsPackage;
                    break;
                case COLLECTION:
                    targetTable = resultsCollection;
                    break;
                default:
                    throw new IllegalStateException("Unknown scope: " + result.getScope());
            }
            targetTable.put(result.getIdentifier(), result.getMetricClass(), result.getValue());
        }
    }

    @Override
    public void signalDone() {
        logger.info("Processing finished, exporting results");
        try {
            exportData(this.exporter, this.resultsClass, MetricScope.CLASS);
            exportData(this.exporter, this.resultsPackage, MetricScope.PACKAGE);
            exportData(this.exporter, this.resultsCollection, MetricScope.COLLECTION);

            this.exporter.close();
        } catch (IOException e) {
            logger.error("Error exporting data", e);
        }

        logger.info("Results exported.");
    }

    private ResultsExporter getOutputWriter(final Set<String> output) throws IOException {
        final List<Exception> exceptions = Lists.newLinkedList();
        for (final String out : output) {
            try {
                return new ResultsExporter(out);
            } catch (Exception ex) {
                //Try other outputs
                exceptions.add(ex);
            }
        }

        throw new IOException("Failed to find a suitable output: " + exceptions);
    }
}
