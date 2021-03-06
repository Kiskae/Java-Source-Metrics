package nl.rug.jbi.jsm.util;

import au.com.bytecode.opencsv.CSVWriter;
import com.google.common.base.Function;
import com.google.common.base.Objects;
import com.google.common.collect.*;
import nl.rug.jbi.jsm.core.calculator.MetricScope;

import java.io.Closeable;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * Multi-file exporter for the metric results. Using the CSV-writer from the OpenCSV project, it will create a CSV file
 * for each Metric-&gt;ResultList association. Mappings between the various Metrics, Scopes and their result files will be
 * put in the unique file with identifier "Mapping".
 *
 * @author David van Leusen
 * @see <a href="https://code.google.com/p/opencsv/">OpenCSV Project</a>
 * @since 2014-05-28
 */
public class ResultsExporter implements Closeable {
    private final static int DOUBLE_PRINT_PRECISION = 4;
    private final CSVWriter mappingWriter;
    private final String mappingFileName;
    private final File container;
    private final String namePattern;
    private final NumberFormat nf;

    /**
     * Creates a new ResultsExporter that creates files using the given fileNamePattern. This pattern should include
     * the replacement token '%s' so the unique identifiers can be inserted. This token will be replaced by 'mapping' to
     * create the file containing the metrics to file mappings.
     *
     * @param fileNamePattern String pattern indicating the location results should be output to.
     * @throws IOException                        If the underlying CSVWriter throws an IOException or the mappings file
     *                                            already exists.
     * @throws java.lang.IllegalArgumentException If the given fileNamePattern doesn't contain '%s'
     */
    public ResultsExporter(final String fileNamePattern) throws IOException {
        checkArgument(fileNamePattern != null);
        assert fileNamePattern != null;

        final File tmpFile = new File(fileNamePattern);
        this.container = tmpFile.getParentFile();
        this.namePattern = tmpFile.getName();
        checkArgument(this.namePattern.contains("%s"), "Name has to contain '%s' for identifier replacing.");
        this.container.mkdirs();

        final File mappingFile = getFileForName("Mapping", true); //Throw exception if this file already exists.
        this.mappingFileName = mappingFile.getAbsolutePath();
        this.mappingWriter = new CSVWriter(new FileWriter(mappingFile));
        this.mappingWriter.writeNext(new String[]{"Metric Identifier", "Scope", "Output File"});

        this.nf = NumberFormat.getNumberInstance(Locale.US);
        //Set double precision
        this.nf.setMinimumFractionDigits(0);
        this.nf.setMaximumFractionDigits(DOUBLE_PRINT_PRECISION);
    }

    private static String printObject(final Object o, final NumberFormat nf) {
        if (o instanceof Number) {
            return nf.format(((Number) o).doubleValue());
        } else {
            return o != null ? o.toString() : "null";
        }
    }

    private File getFileForName(final String identifier, final boolean strict) throws IOException {
        final String fileName = String.format(namePattern, identifier.toLowerCase());

        File selectedFile = new File(container, fileName);
        if (selectedFile.exists() && strict) {
            throw new IOException("File already exists: " + selectedFile);
        } else {
            int counter = 0;
            while (selectedFile.exists()) {
                selectedFile = new File(container, String.format("%s.%d", fileName, ++counter));
            }
        }

        return selectedFile;
    }

    /**
     * Exports a data set, creates a file to contain the results, writes the filename to the mapping file and then
     * write all the data from the given map to that file.
     *
     * @param metricClass Metric Class that is getting exported.
     * @param scope       Scope of the results getting exported.
     * @param results     Map with results for the given metric and scope.
     * @throws IOException If it gets thrown by the underlying CSVWriter
     */
    public void exportData(final Class metricClass, final MetricScope scope, final Map<String, Object> results)
            throws IOException {
        checkArgument(metricClass != null);
        assert metricClass != null;
        checkArgument(scope != null);
        assert scope != null;
        checkArgument(results != null);
        assert results != null;

        final File metricOutput = getFileForName(
                String.format("%s.%s", metricClass.getSimpleName(), scope),
                false
        );

        //Output mapping
        this.mappingWriter.writeNext(new String[]{metricClass.getName(), scope.toString(), metricOutput.toString()});
        this.mappingWriter.flush();

        //Write results to file.
        final CSVWriter writer = new CSVWriter(new FileWriter(metricOutput));
        try {
            writer.writeNext(new String[]{"Identifier", "Result"});
            //Map each identifier->result pair to an entry in the CSV file.
            writer.writeAll(
                    FluentIterable.from(results.entrySet())
                            .transform(new Function<Map.Entry<String, Object>, String[]>() {
                                @Override
                                public String[] apply(Map.Entry<String, Object> entry) {
                                    return new String[]{
                                            entry.getKey(),
                                            printObject(entry.getValue(), ResultsExporter.this.nf)
                                    };
                                }
                            })
                            .toList()
            );
        } finally {
            try {
                writer.close();
            } catch (IOException ignored) {
                //Prevent finally block from overriding exceptions
            }
        }
    }


    /**
     * Exports a data set collection, creates a file to contain the results, writes the filename to the mapping file
     * and then write all the data from the given table to that file.
     *
     * @param scope   Scope of the results getting exported.
     * @param results Map with results for the given scope.
     * @throws IOException If it gets thrown by the underlying CSVWriter
     */
    public void exportDataCollection(final MetricScope scope, final Table<String, Class, Object> results)
            throws IOException {
        checkArgument(scope != null);
        assert scope != null;
        checkArgument(results != null);
        assert results != null;

        final File metricOutput = getFileForName(scope.toString(), false);

        //Output mappings
        final String outputFileName = metricOutput.toString();
        for (final Class c : results.columnKeySet()) {
            this.mappingWriter.writeNext(new String[]{c.getName(), scope.toString(), outputFileName});
        }
        this.mappingWriter.flush();

        //Write results
        final CSVWriter writer = new CSVWriter(new FileWriter(metricOutput));
        try {
            //Output headers
            final List<String> headers = Lists.newLinkedList();
            headers.add("Identifier");
            headers.addAll(Collections2.transform(results.columnKeySet(), new Function<Class, String>() {
                @Override
                public String apply(Class aClass) {
                    return aClass.getSimpleName();
                }
            }));
            writer.writeNext(headers.toArray(new String[headers.size()]));

            //Output data
            final ImmutableList<String[]> mappedDataSet = FluentIterable.from(results.rowMap().entrySet())
                    .transform(new Function<Map.Entry<String, Map<Class, Object>>, String[]>() {
                        @Override
                        public String[] apply(Map.Entry<String, Map<Class, Object>> entry) {
                            final String[] ret = new String[1 + entry.getValue().size()];
                            ret[0] = entry.getKey();

                            final Iterator<Object> it = entry.getValue().values().iterator();
                            int count = 1;
                            while (it.hasNext()) {
                                ret[count++] = printObject(it.next(), ResultsExporter.this.nf);
                            }

                            return ret;
                        }
                    })
                    .toList();
            writer.writeAll(mappedDataSet);
        } finally {
            try {
                writer.close();
            } catch (IOException ignored) {
                //Prevent finally block from overriding exceptions
            }
        }
    }

    /**
     * @return Relative location of the 'mapping' file
     */
    public String getMappingFileName() {
        return this.mappingFileName;
    }

    @Override
    public void close() throws IOException {
        this.mappingWriter.close();
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("container", container)
                .add("namePattern", namePattern)
                .toString();
    }
}
