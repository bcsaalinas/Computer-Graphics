package edu.up.cg;

import edu.up.cg.gui.GuiApp;

import javax.swing.SwingUtilities;

public class Main {

    public static void main(String[] args) {
        // Swing requires all UI construction to happen on the event dispatch thread
        SwingUtilities.invokeLater(() -> new GuiApp().show());
    }

}
