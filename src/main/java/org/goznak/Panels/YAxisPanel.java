package org.goznak.Panels;


import org.goznak.Containers.Trend;
import org.goznak.Instruments.MouseAction;

import java.awt.*;
import java.text.DecimalFormat;

public class YAxisPanel extends CommonPanel {

    Dimension size;
    Trend trend;

    public YAxisPanel(MainPanel mainPanel, int id){
        super(MainPanel.Y_AXIS, mainPanel, id);
        trend = mainPanel.trends.getTrend(id);
        addMouseListener(new MouseAction(mainPanel, this, id));
        addMouseMotionListener(new MouseAction(mainPanel, this, id));
        update();
    }

    public void update(){
        size = mainPanel.calculateComponentSize(MainPanel.Y_AXIS, id);
        setPreferredSize(size);
        setVisible(!trend.hide);
        if (!trend.hide) {
            repaint();
            revalidate();
        }
    }

    @Override
    public void paint (Graphics g) {
        Graphics2D graph = (Graphics2D) g;
        graph.setPaint(trend.color);
        graph.fillRect(0, 0, size.width, size.height);
        graph.setPaint(Color.GRAY);
        graph.drawLine(size.width - 1, 0, size.width - 1, size.height);
        drawBars(graph);
    }

    private void drawBars(Graphics2D graph){
        graph.setPaint(trend.inverseColor);
        int coordinate;
        double value;
        String label;
        //Прорисовка цифр на осях
        int trendPanelHeigth = mainPanel.trendPanel.getPreferredSize().height;
        if(mainPanel.numYGrid > 0) {
            int PXGridHeight = mainPanel.PXgetGridHeight();
            double gridHeight = trend.getGridHeight();
            for (int i = 0; i <= mainPanel.numYGrid + 1; i++) {
                coordinate = i * PXGridHeight + mainPanel.PXshiftYGrid;
                value = trend.getY() + trend.getHeight() - trend.shiftYGrid - i * gridHeight;
                label = new DecimalFormat(trend.numTemplate).format(value);
                graph.drawLine(size.width - 1, coordinate, size.width - 5, coordinate);
                if (valueInLimits(coordinate, 25, trendPanelHeigth - 15)) {
                    graph.drawString(label, 5, coordinate);
                }
            }
        }
        value = trend.getY();
        label = new DecimalFormat(trend.numTemplate).format(value);
        graph.drawString(label, 5, trendPanelHeigth - 5);
        value = trend.getY() + trend.getHeight();
        label = new DecimalFormat(trend.numTemplate).format(value);
        graph.drawString(label, 5, 15);
    }

    boolean valueInLimits(int value, int lowLim, int highLim){
        return value > lowLim && value < highLim;
    }
}
