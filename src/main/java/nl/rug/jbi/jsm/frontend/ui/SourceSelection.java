package nl.rug.jbi.jsm.frontend.ui;

import com.google.common.collect.Sets;
import nl.rug.jbi.jsm.frontend.ui.element.SelectableList;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.Set;

public class SourceSelection extends JPanel {
    private final SelectableList sourceList = new SelectableList();
    private final JButton addButton = new JButton();
    private final JButton removeButton = new JButton();

    public SourceSelection(final String panelName) {
        final GridBagLayout layout = new GridBagLayout();
        final GridBagConstraints c = new GridBagConstraints();
        this.setLayout(layout);
        this.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(panelName),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));

        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 2;
        final JScrollPane sPane = new JScrollPane(sourceList);
        sPane.setPreferredSize(new Dimension(250, 80));
        this.add(sPane, c);

        c.gridy = 1;
        c.gridwidth = 1;
        this.addButton.setAction(new AbstractAction("Add Directory/JAR") {
            @Override
            public void actionPerformed(ActionEvent e) {
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

                int ret = fc.showDialog(SourceSelection.this, "Select");

                if (ret == JFileChooser.APPROVE_OPTION) {
                    for (final File selectedFile : fc.getSelectedFiles()) {
                        final String relativePath = workingDir
                                .toURI()
                                .relativize(
                                        selectedFile.toURI()
                                ).getPath();

                        sourceList.addOption(relativePath);
                        removeButton.setEnabled(true);
                    }
                }
            }
        });
        this.add(this.addButton, c);

        c.gridx = 1;
        this.removeButton.setAction(new AbstractAction("Remove Selection") {
            @Override
            public void actionPerformed(final ActionEvent e) {
                final int left = sourceList.removeSelection();
                if (left == 0) {
                    removeButton.setEnabled(false);
                }
            }
        });
        removeButton.setEnabled(false);
        this.add(this.removeButton, c);

        this.setMaximumSize(this.getPreferredSize());
    }

    public Set<String> getSources() {
        return Sets.newHashSet(this.sourceList.getOptions());
    }

    public void setEnabled(final boolean enabled) {
        this.addButton.setEnabled(enabled);
        this.removeButton.setEnabled(enabled && !this.sourceList.isEmpty());
    }
}
