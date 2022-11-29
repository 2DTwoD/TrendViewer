package org.goznak.Panels;

import org.goznak.Containers.Palette;
import org.goznak.Containers.Trends;
import org.goznak.Instruments.MouseAction;

import java.awt.*;
import java.util.ArrayList;

public class TrendPanel extends CommonPanel {

    Dimension size;
    public Color color;
    public Color inverseColor;
    public Color gridColor;

    public TrendPanel(MainPanel mainPanel){
        super(MainPanel.TREND_AREA, mainPanel, Trends.ALL);
        setColor(Palette.getSpecificColor(47));
        addMouseListener(new MouseAction(mainPanel, this, Trends.ALL));
        addMouseMotionListener(new MouseAction(mainPanel, this, Trends.ALL));
        addMouseWheelListener(new MouseAction(mainPanel, this, Trends.ALL));
        update();
    }

    public void update(){
        size = mainPanel.calculateComponentSize(MainPanel.TREND_AREA, Trends.ALL);
        setPreferredSize(size);
        repaint();
        revalidate();
    }

    @Override
    public void paint (Graphics g) {
        Graphics2D graph = (Graphics2D) g;
        graph.setPaint(color);
        graph.fillRect(0, 0, size.width, size.height);
        drawGrid(graph);
        for (int i: trends.getIdSet()) {
            if (i == 0 || trends.getHide(i)){
                continue;
            }
            trends.getTrend(i).calculateAreaCoord();
            drawTrend(graph, trends.getValueCoord(i), trends.getTimeCoord(i), trends.getColor(i), trends.getDash(i));
        }
        drawZoom(graph);
    }

    //Прорисовка сетки
    private void drawGrid(Graphics2D graph){
        graph.setPaint(gridColor);
        Stroke dash = new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL,
                0, new float[]{3}, 0);
        graph.setStroke(dash);
        int coordinate;
        //Линии перпендикулярно оси X
        if(mainPanel.numXGrid > 0) {
            int PXGridWidth = mainPanel.PXgetGridWidth();
            for (int i = 0; i <= mainPanel.numXGrid + 1; i++) {
                coordinate = i * PXGridWidth - mainPanel.PXshiftXGrid;
                graph.drawLine(coordinate, 0, coordinate, size.height);
            }
        }
        //Линии перпендикулярно оси Y
        if (mainPanel.numYGrid > 0) {
            int PXGridHeight = mainPanel.PXgetGridHeight();
            for (int i = 0; i <= mainPanel.numYGrid + 1; i++) {
                coordinate = i * PXGridHeight + mainPanel.PXshiftYGrid;
                graph.drawLine(0, coordinate, size.width, coordinate);
            }
        }
    }

    //Прорисовка прямоугольника зумирования
    private void drawZoom(Graphics2D graph){
        if (zoomFlag) {
            Stroke dash = new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL,
                    0, new float[]{1}, 0);
            graph.setStroke(dash);
            graph.setPaint(inverseColor);
            graph.drawRect(Math.min(mainPanel.zoomMouseXStart, mainPanel.mouseX), Math.min(mainPanel.zoomMouseYStart, mainPanel.mouseY),
                    Math.abs(mainPanel.zoomMouseXStart - mainPanel.mouseX), Math.abs(mainPanel.zoomMouseYStart - mainPanel.mouseY));
        }
    }

    //Прорисовка тренда
    private void drawTrend(Graphics2D graph, ArrayList<Integer> valueList,
                           ArrayList<Integer> timeList, Color color, Stroke dash){
        graph.setPaint(color);
        graph.setStroke(dash);
        for(int i = 1; i < valueList.size(); i++){
            int x1 = timeList.get(i - 1);
            int x2 = timeList.get(i);
            int y1 = mainPanel.invertY(valueList.get(i - 1));
            int y2 = mainPanel.invertY(valueList.get(i));
            graph.drawLine(x1, y1, x2, y1);
            graph.drawLine(x2, y1, x2, y2);
        }
    }

    public void setColor(Color color){
        this.color = color;
        inverseColor = Palette.getInverseColor(color);
        gridColor = Palette.getGridColor(color);
    }
}
