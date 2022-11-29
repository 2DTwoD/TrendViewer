package org.goznak;

import org.goznak.Panels.MainPanel;

import javax.swing.*;
import java.awt.*;

public class Main extends JFrame {
    MainPanel mainPanel;
    public static void main(String[] args){
        new Main("Trend viewer");
    }
    public Main(String title){
        super(title);
        setPreferredSize(new Dimension(1250, 800));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        SwingUtilities.invokeLater(() -> {
            mainPanel = new MainPanel(this);
            add(mainPanel);
        });
        pack();
        setVisible(true);
    }
}
