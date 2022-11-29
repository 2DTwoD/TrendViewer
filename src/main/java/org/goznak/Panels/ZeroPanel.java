package org.goznak.Panels;

import org.goznak.Containers.Trends;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ZeroPanel extends JPanel{

    MainPanel mainPanel;

    public ZeroPanel(MainPanel mainPanel){
        this.mainPanel = mainPanel;
        JButton fullScreenButton = new JButton("-");
        fullScreenButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                mainPanel.trends.setDefaultAreaValue();
                SwingUtilities.invokeLater(mainPanel::updateAllPanels);
            }
        });

        JButton stackButton = new JButton("≡");
        stackButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                mainPanel.trends.setStack();
                SwingUtilities.invokeLater(mainPanel::updateAllPanels);
            }
        });
        add(fullScreenButton);
        add(stackButton);
        update();
    }

    public void update(){
        setPreferredSize(mainPanel.calculateComponentSize(MainPanel.ZERO, Trends.ALL));
        setBackground(mainPanel.color);
        revalidate();
    }
}
