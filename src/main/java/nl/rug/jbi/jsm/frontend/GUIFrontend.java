package nl.rug.jbi.jsm.frontend;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;

public class GUIFrontend implements Frontend {
    private final static Logger logger = LogManager.getLogger(GUIFrontend.class);

    @Override
    public void init() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                logger.trace("Initializing GUIFrontend");
                initWindow();
            }
        });
    }

    private void initWindow() {
        final JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        final JTable jTable = new JTable();
        final JScrollPane scrollPane = new JScrollPane(jTable);
        jTable.setFillsViewportHeight(true);
        jTable.setDragEnabled(false);
        frame.add(scrollPane);
        frame.pack();
        frame.setVisible(true);
    }
}
