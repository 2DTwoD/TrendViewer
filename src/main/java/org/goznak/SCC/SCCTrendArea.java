package org.goznak.SCC;

import org.goznak.Panels.MainPanel;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class SCCTrendArea extends SimpleColorChooser {

    public SCCTrendArea(MainPanel mainPanel, Point location){
        super(mainPanel, mainPanel.trendPanel.color, location);
        okButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if(selectButtonAction()) {
                    mainPanel.trendPanel.setColor(color);
                }
            }
        });
    }
}
