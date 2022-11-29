package org.goznak.Panels;

import org.goznak.Containers.Trend;
import org.goznak.Containers.Trends;
import org.goznak.Instruments.MouseAction;

import java.awt.*;
import java.text.SimpleDateFormat;

public class XAxisPanel extends CommonPanel {

    Dimension size;
    Trend zeroTrend;
    String datePattern = "dd.MM.yyyy";
    SimpleDateFormat date = new SimpleDateFormat(datePattern);
    String timePattern = "HH:mm:ss.SSS";
    SimpleDateFormat time = new SimpleDateFormat(timePattern);
    String dateTimePattern = "dd.MM.yyyy HH:mm:ss.SSS";
    SimpleDateFormat dateTime = new SimpleDateFormat(dateTimePattern);

    public XAxisPanel(MainPanel mainPanel, int id){
        super(MainPanel.X_AXIS, mainPanel, id);
        zeroTrend = mainPanel.trends.getTrend(0);
        addMouseListener(new MouseAction(mainPanel, this, id));
        addMouseMotionListener(new MouseAction(mainPanel, this, id));
        update();
    }

    public void update(){
        size = mainPanel.calculateComponentSize(MainPanel.X_AXIS, Trends.ALL);
        setPreferredSize(size);
        repaint();
        revalidate();
    }

    @Override
    public void paint (Graphics g) {
        Graphics2D graph = (Graphics2D) g;
        graph.setPaint(mainPanel.color);
        graph.fillRect(0, 0, size.width, size.height);
        drawBars(graph);
    }

    private void drawBars(Graphics2D graph){
        graph.setPaint(mainPanel.inverseColor);
        int coordinate;
        double value = zeroTrend.getX();
        int trendPanelWidth = mainPanel.trendPanel.getPreferredSize().width;
        String prevDate = date.format(value);
        String curDate;
        //Прорисовка цифр на осях
        if(mainPanel.numXGrid > 0) {
            int PXGridWidth = mainPanel.PXgetGridWidth();
            double gridWidth = zeroTrend.getGridWidth();
            for (int i = 0; i <= mainPanel.numXGrid + 1; i++) {
                coordinate = i * PXGridWidth - mainPanel.PXshiftXGrid;
                value = zeroTrend.getX() - zeroTrend.shiftXGrid + i * gridWidth;
                graph.drawLine(coordinate, 0, coordinate, 5);
                graph.drawString(time.format(value), coordinate, 17);
                curDate = date.format(value);
                if(valueInLimits(coordinate, 147, trendPanelWidth - 217) &&
                        !prevDate.equals(curDate)) {
                    prevDate = curDate;
                    graph.drawString(prevDate, coordinate, 33);
                }
            }
        }
        value = zeroTrend.getX();
        graph.drawString(dateTime.format(value), 0, 33);
        value = zeroTrend.getX() + zeroTrend.getWidth();
        graph.drawString(dateTime.format(value), trendPanelWidth - 140, 33);
    }

    boolean valueInLimits(int value, int lowLim, int highLim){
        return value > lowLim && value < highLim;
    }
}
