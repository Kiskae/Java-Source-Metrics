package nl.rug.jbi.jsm.frontend.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ExportDialog extends JDialog implements ActionListener {

    private Result result = null;
    private boolean exportByScope = false;
    private JTextField exportPath = new JTextField();

    private ExportDialog(final Frame frame) {
        super(frame, "Export Results", true);

        final JPanel centerPane = new JPanel();
        centerPane.setLayout(new BoxLayout(centerPane, BoxLayout.Y_AXIS));

        final JLabel l1 = new JLabel("Please give a path where to export the data.");
        l1.setAlignmentX(0.5f);
        centerPane.add(l1);

        final JLabel l2 = new JLabel("The path has to contain '%s' which will get replaced by an identifier for each metric.");
        l2.setAlignmentX(0.5f);
        centerPane.add(l2);

        centerPane.add(createExportPathTextField());
        centerPane.add(createExportTypeSwitch());
        centerPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));

        final Container contentPane = getContentPane();
        contentPane.add(centerPane, BorderLayout.CENTER);
        contentPane.add(createButtonPane(), BorderLayout.PAGE_END);

        pack();
        setLocationRelativeTo(frame);
    }

    public static Result showDialog(final Component frameComp) {
        final Frame frame = JOptionPane.getFrameForComponent(frameComp);
        final ExportDialog ed = new ExportDialog(frame);
        ed.setVisible(true);
        return ed.getResult();
    }

    private JComponent createButtonPane() {
        //Export button
        final JButton exportButton = new JButton("Export");
        exportButton.setActionCommand("export");
        exportButton.addActionListener(this);
        //Cancel button
        final JButton cancelButton = new JButton("Cancel");
        cancelButton.setActionCommand("cancel");
        cancelButton.addActionListener(this);
        getRootPane().setDefaultButton(cancelButton);

        final JPanel buttonPane = new JPanel();
        buttonPane.setLayout(new BoxLayout(buttonPane, BoxLayout.LINE_AXIS));
        buttonPane.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
        buttonPane.add(Box.createHorizontalGlue());
        buttonPane.add(exportButton);
        buttonPane.add(Box.createRigidArea(new Dimension(10, 0)));
        buttonPane.add(cancelButton);

        return buttonPane;
    }

    private JComponent createExportTypeSwitch() {
        final JRadioButton metricExport = new JRadioButton("Export by Metric");
        metricExport.setActionCommand("metric-export");
        metricExport.setSelected(true);
        metricExport.setToolTipText("Each metric will be exported into an individual file.");

        final JRadioButton scopeExport = new JRadioButton("Export by Scope");
        scopeExport.setActionCommand("scope-export");
        scopeExport.setToolTipText("Each scope will be exported into a file containing all metrics.");

        final ButtonGroup group = new ButtonGroup();
        group.add(metricExport);
        group.add(scopeExport);

        metricExport.addActionListener(this);
        scopeExport.addActionListener(this);

        final JPanel choicePanel = new JPanel(new FlowLayout());
        choicePanel.add(metricExport);
        choicePanel.add(scopeExport);

        return choicePanel;
    }

    private JComponent createExportPathTextField() {
        final JPanel exportPathPanel = new JPanel();
        exportPathPanel.setLayout(new BoxLayout(exportPathPanel, BoxLayout.LINE_AXIS));

        exportPathPanel.add(new JLabel("Export Path: "));
        exportPathPanel.add(this.exportPath);

        return exportPathPanel;
    }

    private Result getResult() {
        return this.result;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if ("export".equals(e.getActionCommand())) {
            this.result = new Result(this.exportPath.getText(), this.exportByScope);
            this.setVisible(false);
        } else if ("cancel".equals(e.getActionCommand())) {
            this.setVisible(false);
        } else if ("metric-export".equals(e.getActionCommand())) {
            this.exportByScope = false;
        } else if ("scope-export".equals(e.getActionCommand())) {
            this.exportByScope = true;
        }
    }

    public static class Result {
        public final String exportPath;
        public final boolean groupScopes;

        public Result(final String exportPath, final boolean groupScopes) {
            this.exportPath = exportPath;
            this.groupScopes = groupScopes;
        }
    }
}
