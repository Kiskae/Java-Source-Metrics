package nl.rug.jbi.jsm.frontend;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;
import nl.rug.jbi.jsm.core.JSMCore;
import nl.rug.jbi.jsm.core.calculator.MetricResult;
import nl.rug.jbi.jsm.frontend.ui.ControlsPanel;
import nl.rug.jbi.jsm.frontend.ui.SourceSelection;
import nl.rug.jbi.jsm.frontend.ui.TabbedResultScreen;
import nl.rug.jbi.jsm.frontend.ui.UserConsole;
import nl.rug.jbi.jsm.util.ClassDiscoverer;
import nl.rug.jbi.jsm.util.ResultsExporter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Set;

public class GUIFrontend extends JFrame implements Frontend, ActionListener {
    private final static Logger logger = LogManager.getLogger(GUIFrontend.class);
    private final UserConsole console = new UserConsole(this);
    private final SourceSelection inputSources = new SourceSelection("Input Sources");
    private final SourceSelection librarySources = new SourceSelection("Library Sources");
    private final ControlsPanel controls = new ControlsPanel(this);
    private final TabbedResultScreen tabbedResults;
    private final JSMCore core;

    public GUIFrontend(final JSMCore core) {
        this.core = Preconditions.checkNotNull(core);
        this.tabbedResults = new TabbedResultScreen(this.core);
    }

    public JSMCore getCore() {
        return this.core;
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
        //Forward to EDT Thread
        if (!SwingUtilities.isEventDispatchThread()) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    GUIFrontend.this.processResult(resultList);
                }
            });
            return;
        }
        tabbedResults.processResults(resultList);
    }

    @Override
    public void signalDone() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                endExecution();
            }
        });
    }

    private void beginExecution() {
        tabbedResults.clearResults();

        final Set<String> targetClasses = FluentIterable.from(inputSources.getSources())
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
                        inputSources.getSources(),
                        librarySources.getSources()
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

        this.inputSources.setEnabled(false);
        this.librarySources.setEnabled(false);
        this.controls.setExecuting(true);
        logger.info("Beginning execution.");
        core.process(
                GUIFrontend.this,
                targetClasses,
                dataSources
        );
    }

    private void endExecution() {
        this.inputSources.setEnabled(true);
        this.librarySources.setEnabled(true);
        this.controls.setExecuting(false);
        logger.info("Execution ended.");
    }

    private void exportResults() {
        final String exportPath = JOptionPane.showInputDialog(
                this,
                "Please give a path where to export the data.\nThe path has to contain '%s' which will get " +
                        "replaced by an identifier for each metric.",
                "Export Path",
                JOptionPane.PLAIN_MESSAGE
        );

        if (exportPath == null) return;

        ResultsExporter exporter = null;
        try {
            exporter = new ResultsExporter(exportPath);
            this.tabbedResults.exportResults(exporter);
            logger.info("Results exported, mapping file located at: '{}'", exporter.getMappingFileName());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            if (exporter != null) {
                try {
                    exporter.close();
                } catch (IOException ignored) {
                }
            }
        }
    }

    private void initWindow() {
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        final Container container = this.getContentPane();
        container.add(this.tabbedResults, BorderLayout.CENTER);
        container.add(createControlPanel(), BorderLayout.EAST);
        container.add(this.console, BorderLayout.SOUTH);

        SwingUtilities.updateComponentTreeUI(this);
        this.pack();
        this.setVisible(true);

        logger.info("Ready");
    }

    private JComponent createControlPanel() {
        final JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));

        panel.add(this.inputSources);
        panel.add(this.librarySources);
        panel.add(this.controls);

        return panel;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if ("execute".equals(e.getActionCommand())) {
            this.beginExecution();
        } else if ("export".equals(e.getActionCommand())) {
            this.exportResults();
        }
    }
}
