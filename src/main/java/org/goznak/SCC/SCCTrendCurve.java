package org.goznak.SCC;

import org.goznak.Containers.Trend;
import org.goznak.Panels.MainPanel;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class SCCTrendCurve extends SimpleColorChooser {

    public SCCTrendCurve(MainPanel mainPanel, Color currentColor, Point location, Trend trend){
        super(mainPanel, currentColor, location);
        okButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if(selectButtonAction()) {
                    trend.setColor(color);
                }
            }
        });
    }
}
