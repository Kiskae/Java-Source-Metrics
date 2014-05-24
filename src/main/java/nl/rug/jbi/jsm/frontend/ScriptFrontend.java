package nl.rug.jbi.jsm.frontend;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.*;
import nl.rug.jbi.jsm.core.JSMCore;
import nl.rug.jbi.jsm.core.calculator.MetricResult;
import nl.rug.jbi.jsm.util.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Set;

public class ScriptFrontend implements Frontend {
    private final static Logger logger = LogManager.getLogger(ScriptFrontend.class);

    private final JSMCore core;

    private final Set<String> input;
    private final Set<String> libraries;
    private final Set<String> output;

    private final Table<String, Class, Object> resultsClass = HashBasedTable.create();
    private final Table<String, Class, Object> resultsPackage = HashBasedTable.create();
    private final Table<String, Class, Object> resultsCollection = HashBasedTable.create();

    public ScriptFrontend(
            final JSMCore core,
            final Set<String> input,
            final Set<String> libraries,
            final Set<String> output
    ) {
        this.core = core;
        this.input = input;
        this.libraries = libraries;
        this.output = output;
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
        logger.info("Processing finished:");
        logger.info("Class results: {}", this.resultsClass);
        logger.info("Package results: {}", this.resultsPackage);
        logger.info("Collection results: {}", this.resultsCollection);
    }
}
