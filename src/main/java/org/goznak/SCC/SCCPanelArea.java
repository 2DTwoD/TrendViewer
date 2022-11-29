package org.goznak.SCC;

import org.goznak.Panels.MainPanel;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class SCCPanelArea extends SimpleColorChooser {

    public SCCPanelArea(MainPanel mainPanel, Point location){
        super(mainPanel, mainPanel.color, location);
        okButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if(selectButtonAction()) {
                    mainPanel.setColor(color);
                }
            }
        });
    }
}
