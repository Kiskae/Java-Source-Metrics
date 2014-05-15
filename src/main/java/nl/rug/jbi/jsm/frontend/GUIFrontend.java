package nl.rug.jbi.jsm.frontend;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import nl.rug.jbi.jsm.core.JSMCore;
import nl.rug.jbi.jsm.core.calculator.MetricResult;
import nl.rug.jbi.jsm.util.CSVExporter;
import nl.rug.jbi.jsm.util.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class GUIFrontend implements Frontend {
    private final static Logger logger = LogManager.getLogger(GUIFrontend.class);
    private final JSMCore core;
    private final MetricDataTable classData = new MetricDataTable("Class Name");
    private final MetricDataTable packageData = new MetricDataTable("Package Name");
    private final MetricDataTable collectionData = new MetricDataTable("Collection Name");

    public GUIFrontend(final JSMCore core) {
        this.core = Preconditions.checkNotNull(core);
    }

    @Override
    public void init() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (final ReflectiveOperationException e) {
            logger.warn("Exception setting Swing L&F", e);
        } catch (final UnsupportedLookAndFeelException ignored) {
            /* Will just default to the cross-platform L&F */
        }

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                logger.trace("Initializing GUIFrontend");
                initWindow();
            }
        });
    }

    @Override
    public void processResult(final MetricResult result) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                switch (result.getScope()) {
                    case CLASS:
                        classData.processResult(result);
                        return;
                    case PACKAGE:
                        packageData.processResult(result);
                        return;
                    case COLLECTION:
                        collectionData.processResult(result);
                        return;
                    default:
                        throw new IllegalStateException("Unknown scope: " + result.getScope());
                }
            }
        });
    }

    private void initWindow() {
        final JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        final JPanel panel = new JPanel();

        panel.add(createTabbedDataTable());
        panel.add(createControlPanel());

        frame.setContentPane(panel);
        frame.pack();
        frame.setVisible(true);
    }

    private JComponent createControlPanel() {
        final JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        final JButton start = new JButton();
        panel.add(start);

        final JButton export = new JButton();
        export.setAction(new AbstractAction("Export data") {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    final CSVExporter exporter = new CSVExporter(new File("test.csv"));
                    classData.export(exporter);
                    exporter.close();
                } catch (IOException e1) {
                    logger.debug(e1);
                }
            }
        });
        export.setEnabled(false);
        panel.add(export);

        start.setAction(new AbstractAction("Start Calculation") {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    final File file = new File("agon-1.0-SNAPSHOT.jar");
                    core.process(GUIFrontend.this, Sets.newHashSet(FileUtils.findClassNames(file)), file.toURI().toURL());
                    export.setEnabled(true);
                } catch (MalformedURLException ex) {
                    ex.printStackTrace();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });

        final JProgressBar progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);
        progressBar.setString("Derp");
        panel.add(progressBar);

        return panel;
    }

    private JComponent createTabbedDataTable() {
        final JTabbedPane tabContainer = new JTabbedPane();

        final JComponent classResults = createResultTable(this.classData);
        tabContainer.addTab("Class", classResults);

        final JComponent packageResults = createResultTable(this.packageData);
        tabContainer.addTab("Package", packageResults);

        return tabContainer;
    }

    private JComponent createResultTable(final AbstractTableModel data) {
        final JTable table = new JTable(data);
        table.getTableHeader().setReorderingAllowed(false);
        return new JScrollPane(table);
    }

    private static class MetricDataTable extends AbstractTableModel {
        private final String identifierName;
        private final List<Class> metricClasses = Lists.newLinkedList();
        private final List<String> identifierLookup = Lists.newArrayList();
        private final Map<String, ResultSet> results = Maps.newHashMap();

        public MetricDataTable(final String identifierName) {
            this.identifierName = Preconditions.checkNotNull(identifierName);
        }

        @Override
        public int getRowCount() {
            return this.identifierLookup.size();
        }

        @Override
        public int getColumnCount() {
            return this.metricClasses.size() + 1;
        }

        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return false;
        }

        @Override
        public String getColumnName(int column) {
            return column == 0 ? "" : this.metricClasses.get(column - 1).getSimpleName();
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            final String className = this.identifierLookup.get(rowIndex);
            return this.results.get(className).getResult(columnIndex);
        }

        public void processResult(final MetricResult result) {
            final String indentifier = result.getIdentifier();
            final int index = this.identifierLookup.indexOf(indentifier);
            final int columnIndex = this.metricClasses.indexOf(result.getMetricClass());
            if (index == -1) {
                this.identifierLookup.add(indentifier);

                final ResultSet results = new ResultSet(indentifier, this.metricClasses.size());
                results.setResult(columnIndex, result.getValue());
                this.results.put(indentifier, results);

                final int size = this.identifierLookup.size();
                this.fireTableRowsInserted(size - 1, size - 1);
            } else {
                this.results.get(indentifier).setResult(columnIndex, result.getValue());
                this.fireTableCellUpdated(index, columnIndex + 1);
            }
        }

        public void setMetricClasses(final List<Class> metricClasses) {
            this.metricClasses.clear();
            this.metricClasses.addAll(metricClasses);
            this.identifierLookup.clear();
            this.results.clear();
            this.fireTableStructureChanged();
        }

        public void export(final CSVExporter export) {
            final List<String> headers = Lists.newLinkedList();
            headers.add(this.identifierName);
            headers.addAll(Collections2.transform(this.metricClasses, new Function<Class, String>() {
                @Override
                public String apply(Class aClass) {
                    return aClass.getSimpleName();
                }
            }));
            export.writeDataRow(headers);

            for (final Map.Entry<String, ResultSet> entry : this.results.entrySet()) {
                final List<Object> row = Lists.newLinkedList();
                row.add(entry.getKey());
                row.addAll(Arrays.asList(entry.getValue().getObjects()));
                export.writeDataRow(row);
            }
        }
    }

    private static class ResultSet {
        private final Object[] results;
        private final String identifier;

        public ResultSet(final String identifier, final int setSize) {
            this.identifier = identifier;
            this.results = new Object[setSize];
        }

        public Object[] getObjects() {
            return this.results;
        }

        public void setResult(final int index, final Object result) {
            this.results[index] = result;
        }

        public Object getResult(final int index) {
            return index == 0 ? this.identifier : this.results[index - 1];
        }
    }
}
