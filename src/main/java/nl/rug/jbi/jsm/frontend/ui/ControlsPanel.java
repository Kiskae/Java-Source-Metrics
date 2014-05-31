package nl.rug.jbi.jsm.frontend.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class ControlsPanel extends JPanel {
    private final JButton executeButton = new JButton("Start Calculation");
    private final JButton exportButton = new JButton("Export");
    private final JProgressBar progressBar = new JProgressBar();

    public ControlsPanel(final ActionListener listener) {
        final GridBagLayout layout = new GridBagLayout();
        final GridBagConstraints c = new GridBagConstraints();
        this.setLayout(layout);

        this.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Controls"),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));

        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 1;
        this.executeButton.setActionCommand("execute");
        this.executeButton.addActionListener(listener);
        this.add(this.executeButton, c);

        c.gridx = 1;
        this.exportButton.setActionCommand("export");
        this.exportButton.setEnabled(false);
        this.exportButton.addActionListener(listener);
        this.add(this.exportButton, c);

        c.gridx = 0;
        c.gridy = 1;
        c.gridwidth = 2;
        this.add(this.progressBar, c);

        this.setMaximumSize(this.getPreferredSize());
    }

    public void setExecuting(final boolean executing) {
        this.executeButton.setEnabled(!executing);
        this.exportButton.setEnabled(!executing);
        this.progressBar.setIndeterminate(executing);
    }
}
