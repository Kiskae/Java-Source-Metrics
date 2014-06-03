package nl.rug.jbi.jsm.frontend.ui;

import com.google.common.collect.Table;
import nl.rug.jbi.jsm.core.JSMCore;
import nl.rug.jbi.jsm.core.calculator.MetricResult;
import nl.rug.jbi.jsm.core.calculator.MetricScope;
import nl.rug.jbi.jsm.frontend.ui.element.MetricDataTable;
import nl.rug.jbi.jsm.frontend.ui.element.RoundingDoubleCellRenderer;
import nl.rug.jbi.jsm.frontend.ui.element.SortableResult;
import nl.rug.jbi.jsm.util.ResultsExporter;

import javax.swing.*;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class TabbedResultScreen extends JTabbedPane {
    private final static int REPORTED_DOUBLE_PRECISION = 4;
    private final MetricDataTable classData = new MetricDataTable();
    private final MetricDataTable packageData = new MetricDataTable();
    private final MetricDataTable collectionData = new MetricDataTable();

    public TabbedResultScreen(final JSMCore core) {
        this.classData.setMetricClasses(core.getMetricsForScope(MetricScope.CLASS));
        this.packageData.setMetricClasses(core.getMetricsForScope(MetricScope.PACKAGE));
        this.collectionData.setMetricClasses(core.getMetricsForScope(MetricScope.COLLECTION));

        final JComponent classResults = createJTable(this.classData);
        this.addTab("Class", classResults);

        final JComponent packageResults = createJTable(this.packageData);
        this.addTab("Package", packageResults);

        final JComponent collectionResults = createJTable(this.collectionData);
        this.addTab("Collection", collectionResults);
    }

    private static JComponent createJTable(final MetricDataTable resultsData) {
        final JTable table = new JTable(resultsData);
        table.getTableHeader().setReorderingAllowed(false);
        table.setDefaultRenderer(SortableResult.class, new RoundingDoubleCellRenderer(REPORTED_DOUBLE_PRECISION));
        table.setAutoCreateRowSorter(true);
        return new JScrollPane(table);
    }

    private static void exportResultsForTable(
            final ResultsExporter exporter,
            final MetricScope scope,
            final Table<String, Class, Object> resultsMap
    ) throws IOException {
        for (final Map.Entry<Class, Map<String, Object>> entry : resultsMap.columnMap().entrySet()) {
            exporter.exportData(entry.getKey(), scope, entry.getValue());
        }
    }

    public void processResults(final List<MetricResult> results) {
        for (final MetricResult result : results) {
            switch (result.getScope()) {
                case CLASS:
                    classData.processResult(result);
                    break;
                case PACKAGE:
                    packageData.processResult(result);
                    break;
                case COLLECTION:
                    collectionData.processResult(result);
                    break;
                default:
                    throw new IllegalStateException("Unknown scope: " + result.getScope());
            }
        }
    }

    public void exportResults(final ResultsExporter exporter, final boolean groupScopes) throws IOException {
        if (groupScopes) {
            exporter.exportDataCollection(MetricScope.CLASS, classData.getResultsMap());
            exporter.exportDataCollection(MetricScope.PACKAGE, packageData.getResultsMap());
            exporter.exportDataCollection(MetricScope.COLLECTION, collectionData.getResultsMap());
        } else {
            exportResultsForTable(exporter, MetricScope.CLASS, classData.getResultsMap());
            exportResultsForTable(exporter, MetricScope.PACKAGE, packageData.getResultsMap());
            exportResultsForTable(exporter, MetricScope.COLLECTION, collectionData.getResultsMap());
        }
    }

    public void clearResults() {
        this.classData.clear();
        this.packageData.clear();
        this.collectionData.clear();
    }
}
