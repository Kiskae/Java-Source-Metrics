package nl.rug.jbi.jsm.frontend;

import com.google.common.base.Preconditions;
import nl.rug.jbi.jsm.core.JSMCore;
import nl.rug.jbi.jsm.core.calculator.MetricResult;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.event.ActionEvent;

public class GUIFrontend implements Frontend {
    private final static Logger logger = LogManager.getLogger(GUIFrontend.class);
    private final JSMCore core;

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
            /* Will just default to the x-platform L&F */
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
        start.setAction(new AbstractAction("Start Calculation") {
            @Override
            public void actionPerformed(ActionEvent e) {
                logger.debug("Derp");
            }
        });
        panel.add(start);

        final JButton export = new JButton();
        export.setAction(new AbstractAction("Export data") {
            @Override
            public void actionPerformed(ActionEvent e) {
                logger.debug("Exporting to CSV");
            }
        });
        export.setEnabled(false);
        panel.add(export);

        final JProgressBar progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);
        progressBar.setString("Derp");
        panel.add(progressBar);

        return panel;
    }

    private JComponent createTabbedDataTable() {
        final JTabbedPane tabContainer = new JTabbedPane();

        final JComponent classResults = createResultTable(null);
        tabContainer.addTab("Class", classResults);

        final JComponent packageResults = createResultTable(null);
        tabContainer.addTab("Package", packageResults);

        return tabContainer;
    }

    private JComponent createResultTable(final AbstractTableModel data) {
        final JTable table = new JTable(data);
        table.getTableHeader().setReorderingAllowed(false);
        return new JScrollPane(table);
    }
}
