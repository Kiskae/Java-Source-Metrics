package nl.rug.jbi.jsm.frontend;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;
import nl.rug.jbi.jsm.core.JSMCore;
import nl.rug.jbi.jsm.core.calculator.MetricResult;
import nl.rug.jbi.jsm.core.calculator.MetricScope;
import nl.rug.jbi.jsm.frontend.ui.MetricDataTable;
import nl.rug.jbi.jsm.frontend.ui.element.SelectableList;
import nl.rug.jbi.jsm.frontend.ui.UserConsole;
import nl.rug.jbi.jsm.util.ClassDiscoverer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Set;

public class GUIFrontend implements Frontend {
    private final static Logger logger = LogManager.getLogger(GUIFrontend.class);
    private final UserConsole console = new UserConsole(this);
    private final JSMCore core;
    private final MetricDataTable classData = new MetricDataTable("Class Name");
    private final MetricDataTable packageData = new MetricDataTable("Package Name");
    private final MetricDataTable collectionData = new MetricDataTable("Collection Name");
    private final SelectableList inputSources = new SelectableList();
    private final SelectableList librarySources = new SelectableList();

    public GUIFrontend(final JSMCore core) {
        this.core = Preconditions.checkNotNull(core);

        this.classData.setMetricClasses(this.core.getMetricsForScope(MetricScope.CLASS));
        this.packageData.setMetricClasses(this.core.getMetricsForScope(MetricScope.PACKAGE));
        this.collectionData.setMetricClasses(this.core.getMetricsForScope(MetricScope.COLLECTION));
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
    public void processResult(final List<MetricResult> resultList) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                for (final MetricResult result : resultList) {
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
        });
    }

    @Override
    public void signalDone() {
        //TODO: buttonz
        logger.debug("Done.");
    }

    private void initWindow() {
        final JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        final JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        panel.add(createTabbedDataTable(), BorderLayout.CENTER);
        panel.add(createControlPanel(), BorderLayout.EAST);
        panel.add(this.console, BorderLayout.SOUTH);

        frame.setContentPane(panel);
        frame.pack();
        frame.setVisible(true);
    }

    private JComponent createControlPanel() {
        final JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));

        panel.add(createSourceSelectionPanel("Input Sources", this.inputSources));
        panel.add(createSourceSelectionPanel("Library Sources", this.librarySources));
        panel.add(createCoreControls());
        panel.add(Box.createGlue());

        return panel;
    }

    private JComponent createSourceSelectionPanel(String panelName, final SelectableList target) {
        final JPanel root = new JPanel();
        final GridBagLayout layout = new GridBagLayout();
        final GridBagConstraints c = new GridBagConstraints();
        root.setLayout(layout);
        root.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(panelName),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));

        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 2;
        final JScrollPane sPane = new JScrollPane(target);
        sPane.setPreferredSize(new Dimension(250, 80));
        root.add(sPane, c);

        //Define Remove button so the adding button can enable it!
        final JButton removeButton = new JButton();

        c.gridy = 1;
        c.gridwidth = 1;
        root.add(new JButton(new AbstractAction("Add Directory/JAR") {
            @Override
            public void actionPerformed(final ActionEvent e) {
                final File workingDir = new File(".");
                final JFileChooser fc = new JFileChooser();
                fc.setFileFilter(new FileFilter() {
                    @Override
                    public boolean accept(File f) {
                        if (f.isDirectory()) {
                            return true;
                        } else {
                            final String name = f.getName();
                            return name.endsWith(".jar");
                        }
                    }

                    @Override
                    public String getDescription() {
                        return "Directories or JAR archives.";
                    }
                });
                fc.setAcceptAllFileFilterUsed(false);
                fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
                fc.setMultiSelectionEnabled(true);
                fc.setCurrentDirectory(workingDir);

                int ret = fc.showDialog(root, "Select");

                if (ret == JFileChooser.APPROVE_OPTION) {
                    for (final File selectedFile : fc.getSelectedFiles()) {
                        final String relativePath = workingDir
                                .toURI()
                                .relativize(
                                        selectedFile.toURI()
                                ).getPath();

                        target.addOption(relativePath);

                        removeButton.setEnabled(true);
                    }
                }
            }
        }), c);

        c.gridx = 1;
        removeButton.setAction(new AbstractAction("Remove Selection") {
            @Override
            public void actionPerformed(final ActionEvent e) {
                final int left = target.removeSelection();
                if (left == 0) {
                    removeButton.setEnabled(false);
                }
            }
        });
        removeButton.setEnabled(false);
        root.add(removeButton, c);

        root.setMaximumSize(root.getPreferredSize());

        return root;
    }

    private JComponent createCoreControls() {
        final JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        final JButton start = new JButton();
        panel.add(start);

        final JButton export = new JButton();
        export.setAction(new AbstractAction("Export data") {
            @Override
            public void actionPerformed(ActionEvent e) {
                throw new UnsupportedOperationException();
            }
        });
        export.setEnabled(false);
        panel.add(export);

        start.setAction(new AbstractAction("Start Calculation") {
            @Override
            public void actionPerformed(ActionEvent e) {
                classData.clear();
                packageData.clear();
                collectionData.clear();

                final Set<String> targetClasses = FluentIterable.from(inputSources.getOptions())
                        .transform(new Function<String, File>() {
                            @Override
                            public File apply(String s) {
                                return new File(s);
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
                                    return ClassDiscoverer.findClassNames(file);
                                } catch (IOException e1) {
                                    return ImmutableList.of();
                                }
                            }
                        }).toSet();

                final URL[] dataSources = FluentIterable
                        .from(Sets.union(
                                Sets.newHashSet(inputSources.getOptions()),
                                Sets.newHashSet(librarySources.getOptions())
                        ))
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
                                } catch (MalformedURLException e1) {
                                    return null;
                                }
                            }
                        }).filter(new Predicate<URL>() {
                            @Override
                            public boolean apply(URL url) {
                                return url != null;
                            }
                        }).toArray(URL.class);

                core.process(
                        GUIFrontend.this,
                        targetClasses,
                        dataSources
                );
                export.setEnabled(true);
            }
        });

        final JProgressBar progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);
        panel.add(progressBar);

        return panel;
    }

    private JComponent createTabbedDataTable() {
        final JTabbedPane tabContainer = new JTabbedPane();

        final JComponent classResults = createResultTable(this.classData);
        tabContainer.addTab("Class", classResults);

        final JComponent packageResults = createResultTable(this.packageData);
        tabContainer.addTab("Package", packageResults);

        final JComponent collectionResults = createResultTable(this.collectionData);
        tabContainer.addTab("Collection", collectionResults);

        return tabContainer;
    }

    private JComponent createResultTable(final AbstractTableModel data) {
        final JTable table = new JTable(data);
        table.getTableHeader().setReorderingAllowed(false);
        table.setAutoCreateRowSorter(true);
        return new JScrollPane(table);
    }
}
