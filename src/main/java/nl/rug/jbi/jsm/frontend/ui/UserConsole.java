package nl.rug.jbi.jsm.frontend.ui;

import nl.rug.jbi.jsm.frontend.GUIFrontend;
import nl.rug.jbi.jsm.util.QueueLogAppender;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import java.awt.*;

public class UserConsole extends JScrollPane {
    private static final Font MONOSPACED = new Font("Monospaced", 0, 12);
    private final JTextArea console = new JTextArea();
    private final GUIFrontend frontend;

    public UserConsole(final GUIFrontend frontend) {
        this.frontend = frontend;

        this.console.setFont(MONOSPACED);
        this.console.setEditable(false);
        this.console.setMargin(null);

        this.setPreferredSize(new Dimension(
                this.getMinimumSize().width,
                MONOSPACED.getSize() * 10 //Console shows 10 lines
        ));

        setViewportView(this.console);

        //Run a daemon thread to retrieve entries for the console
        final Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                String line;
                while ((line = QueueLogAppender.getNextLogEvent("UserConsole")) != null) {
                    UserConsole.this.print(line);
                }
            }
        });
        thread.setDaemon(true);
        thread.start();
    }

    public GUIFrontend getFrontend() {
        return this.frontend;
    }

    public void print(final String log) {
        if (!SwingUtilities.isEventDispatchThread()) {
            //Defer processing to the EventDispatchThread
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    UserConsole.this.print(log);
                }
            });
            return;
        }

        final Document document = this.console.getDocument();
        final JScrollBar scrollBar = getVerticalScrollBar();

        //This code determines if the view should follow the entries
        final boolean shouldScroll;
        if (getViewport().getView() == this.console) {
            shouldScroll = (scrollBar.getValue()
                    + scrollBar.getSize().getHeight()
                    + MONOSPACED.getSize() * 4
            ) > scrollBar.getMaximum();
        } else {
            shouldScroll = false;
        }

        try {
            document.insertString(document.getLength(), log, null);
        } catch (BadLocationException ignored) {
        }

        if (shouldScroll) {
            scrollBar.setValue(Integer.MAX_VALUE);
        }
    }
}
