package org.goznak.Panels;

import org.goznak.Containers.Palette;
import org.goznak.Containers.Trends;
import org.goznak.SCC.SimpleColorChooser;
import org.goznak.Instruments.VerticalLayout;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class MainPanel extends JPanel {

    JFrame mainFrame;
    JPanel row1 = new JPanel(new FlowLayout(FlowLayout.LEFT,0,0));
    JPanel row2 = new JPanel(new FlowLayout(FlowLayout.LEFT,0,0));
    public JPanel rootYAxis = new JPanel(new FlowLayout(FlowLayout.LEFT,0,0));
    public TrendPanel trendPanel;
    public XAxisPanel xAxis;
    public LegendPanel legendPanel;
    public ZeroPanel zeroPanel;
    public Color color;
    public Color inverseColor;
    public SimpleColorChooser SCCMain;
    public final int heightOfXAxis = 35;
    public int widthOfYAxis = 0;
    public final int heightOfLegendDef = 100;
    public int heightOfLegend = heightOfLegendDef;
    public double numXGrid = 10.0;
    public double numYGrid = 10.0;
    public final double maxNumXGrid = 20.0;
    public final double maxNumYGrid = 20.0;
    public int PXshiftXGrid;
    public int PXshiftYGrid;
    public int mouseX = 0;
    public int mouseY = 0;
    public int zoomMouseXStart = 0;
    public int zoomMouseYStart = 0;
    public Trends trends;
    public static final int TREND_AREA = 0;
    public static final int X_AXIS = 1;
    public static final int Y_AXIS = 2;
    public static final int LEGEND = 3;
    public static final int ZERO = 4;

    public MainPanel(JFrame mainFrame){
        super();
        this.mainFrame = mainFrame;
        setLayout(new VerticalLayout(new JPanel(), 0, 0, VerticalLayout.LEFT));
        setColor(Palette.getSpecificColor(46));
        trends = new Trends(this);
        trendPanel = new TrendPanel(this);
        xAxis = new XAxisPanel(this, Trends.ALL);
        legendPanel = new LegendPanel(this);
        zeroPanel = new ZeroPanel(this);
        row1.add(rootYAxis);
        row1.add(trendPanel);
        row2.add(zeroPanel);
        row2.add(xAxis);
        add(legendPanel);
        add(row1);
        add(row2);
        revalidate();
        mainFrame.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                updateAllPanels();
                updatePXSHiftGrid();
            }
        });
    }

    public void updateAllPanels(){
        SwingUtilities.invokeLater(() -> xAxis.update());
        SwingUtilities.invokeLater(() -> trends.update());
        SwingUtilities.invokeLater(() -> trendPanel.update());
        SwingUtilities.invokeLater(() -> legendPanel.update());
        SwingUtilities.invokeLater(() -> zeroPanel.update());
    }

    public void updateMouseCoord(int xAct, int yAct){
        mouseX = xAct;
        mouseY = yAct;
    }

    public void updatePXSHiftGrid(){
        try {
            PXshiftXGrid %= PXgetGridWidth();
        }
        catch (ArithmeticException e){
            PXshiftXGrid = 0;
        }
        try {
            PXshiftYGrid %= PXgetGridHeight();
        }
        catch (ArithmeticException e){
            PXshiftYGrid = 0;
        }
    }

    public int invertY(int value){
        return trendPanel.getPreferredSize().height - value;
    }

    public double invertY(double value){
        return trendPanel.getPreferredSize().height - value;
    }

    public Dimension calculateComponentSize(int component, int id){
        Dimension result = mainFrame.getContentPane().getSize();
        trends.calculateYAxisWidth(id);
        if(trends.getTrendMapSize() > 3) {
            int shift = trends.getTrendMapSize() < trends.maxNumTrends? 1: 0;
            heightOfLegend = (trends.getTrendMapSize() + shift) * 20;
        } else {
            heightOfLegend = heightOfLegendDef;
        }
        switch(component){
            case TREND_AREA -> {
                result.width -= widthOfYAxis;
                result.height -= heightOfXAxis + heightOfLegend;
            }
            case X_AXIS -> {
                result.width -= widthOfYAxis;
                result.height = heightOfXAxis;
            }
            case Y_AXIS -> {
                result.width = widthOfYAxis;
                result.height -= heightOfXAxis + heightOfLegend;
            }
            case LEGEND -> result.height = heightOfLegend;
            case ZERO -> {
                result.width = widthOfYAxis;
                result.height = heightOfXAxis;
            }
        }
        return result;
    }

    public int PXgetGridWidth(){
        return (int) Math.round(trendPanel.getPreferredSize().width / numXGrid);
    }

    public int PXgetGridHeight(){
        return (int) Math.round(trendPanel.getPreferredSize().height / numYGrid);
    }

    public void dragAreaXY(int xAct, int yAct, int axis, int id, boolean onlyGrid){
        if (axis == TREND_AREA || axis == X_AXIS) {
            int subX = xAct - mouseX;
            if(!onlyGrid) {
                trends.incrementTrendAreaX(id, -subX);
            }
            int PXgridWidth = PXgetGridWidth();
            PXshiftXGrid -= subX;
            PXshiftXGrid = PXshiftXGrid < 0? PXgridWidth + PXshiftXGrid: PXshiftXGrid;
            PXshiftXGrid %= PXgridWidth;
        }
        if (axis == MainPanel.TREND_AREA || axis == MainPanel.Y_AXIS) {
            int subY = yAct - mouseY;
            if(!onlyGrid) {
                trends.incrementTrendAreaY(id, subY);
            }
            int PXgridHeight = PXgetGridHeight();
            PXshiftYGrid += subY;
            PXshiftYGrid = PXshiftYGrid < 0? PXgridHeight + PXshiftYGrid: PXshiftYGrid;
            PXshiftYGrid %= PXgridHeight;
        }
        trends.updateShiftGrid(Trends.ALL);
        updateMouseCoord(xAct, yAct);
    }

    public void scaleArea(int axis, int id){
        boolean zoomCondition = Math.abs(zoomMouseXStart - mouseX) > 3 && Math.abs(zoomMouseYStart - mouseY) > 3;
        if(axis == MainPanel.TREND_AREA && zoomCondition) {
            Dimension size = trendPanel.getPreferredSize();
            trends.incrementTrendAreaX(id, Math.min(zoomMouseXStart, mouseX));
            trends.incrementTrendAreaY(id, invertY(Math.max(zoomMouseYStart, mouseY)));
            trends.incrementTrendAreaWidth(id, Math.abs(zoomMouseXStart - mouseX) - size.width);
            trends.incrementTrendAreaHeight(id,Math.abs(zoomMouseYStart - mouseY) - size.height);
            trends.updateShiftGrid(id);
        }
    }

    public void stepScale(double actX, double actY, double direction, int id){
        Dimension size = trendPanel.getPreferredSize();
        double shift;
        double speed = 80 * direction * Math.cos(1 - trends.getWidth(id) / (trends.getDeltaTime(id) * 10));
        double sideRatio = (double) size.height / size.width;
        shift = actX / size.width;
        if (trends.incrementTrendAreaWidth(id, speed)) {
            trends.incrementTrendAreaX(id, -shift * speed);
        }
        shift = invertY(actY) / size.height;
        if (trends.incrementTrendAreaHeight(id, speed * sideRatio)) {
            trends.incrementTrendAreaY(id, -shift * speed * sideRatio);
        }
        trends.updateShiftGrid(Trends.ALL);
    }

    public void dragScale(double actX, double actY, int axis, int id){
        double speed;
        if (axis == X_AXIS) {
            double dir = actX > mouseX? 30.0: -30.0;
            speed = dir * Math.cos(1 - trends.getWidth(id) / (trends.getDeltaTime(id) * 10));
            if(trends.incrementTrendAreaWidth(id, speed)) {
                trends.incrementTrendAreaX(id, -speed);
            }
        }
        if (axis == Y_AXIS) {
            double dir = actY > mouseY? -30.0: 30.0;
            speed = dir * Math.cos(1 - trends.getHeight(id) / (trends.getDeltaValue(id) * 500));
            double shift = 0.5;//(double) invertY(zoomMouseYStart) / trendPanel.getPreferredSize().height;
            if (trends.incrementTrendAreaHeight(id, -speed)) {
                trends.incrementTrendAreaY(id, speed * shift);
            }
        }
        trends.updateShiftGrid(Trends.ALL);
    }

    public void setColor(Color color){
        this.color = color;
        inverseColor = Palette.getInverseColor(color);
    }
}
