package org.goznak.Panels;

import org.goznak.Instruments.CommonFunctions;
import org.goznak.Instruments.Printer;
import org.goznak.Containers.Trend;
import org.goznak.Containers.Trends;
import org.goznak.Instruments.Dialogs;
import org.goznak.SCC.SCCPanelArea;
import org.goznak.SCC.SCCTrendArea;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.print.PageFormat;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.util.Date;

public class SettingsPanel extends JPanel {

    MainPanel mainPanel;
    JLabel xStartLabel = new JLabel("X нач.:");
    JLabel xFinLabel = new JLabel("X конец:");
    JSpinner xStartDate;
    JSpinner xFinDate;
    String timePattern = "dd.MM.yyyy, HH:mm:ss.SSS";
    JLabel gridXLabel = new JLabel("X сетка:");
    JLabel gridYLabel = new JLabel("Y сетка:");
    JTextField gridXField = new JTextField();
    JTextField gridYField = new JTextField();
    JButton applyXButton = new JButton("Применить");
    JButton resetXButton = new JButton("Сбросить");
    JButton trendAreaColorButton = new JButton("Цвет обл. тренда");
    JButton panelColorButton = new JButton("Цвет панели");
    JButton printButton = new JButton("Печать");
    Dimension small = new Dimension(50, 20);
    Dimension middle = new Dimension(100, 20);
    Dimension big = new Dimension(150, 20);
    Dimension huge = new Dimension(250, 20);

    SettingsPanel(MainPanel mainPanel){
        super(new FlowLayout(FlowLayout.LEFT, 0, 0));
        this.mainPanel = mainPanel;

        setPreferredSize(new Dimension(middle.width * 2 + small.width * 2, mainPanel.heightOfLegend));
        xStartDate = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor startTimeEditor = new JSpinner.DateEditor(xStartDate, timePattern);
        xStartDate.setEditor(startTimeEditor);
        xFinDate = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor finTimeEditor = new JSpinner.DateEditor(xFinDate, timePattern);
        xFinDate.setEditor(finTimeEditor);

        xStartLabel.setPreferredSize(small);
        xFinLabel.setPreferredSize(small);
        xStartDate.setPreferredSize(huge);
        xFinDate.setPreferredSize(huge);
        gridXLabel.setPreferredSize(small);
        gridYLabel.setPreferredSize(small);
        gridXField.setPreferredSize(middle);
        gridYField.setPreferredSize(middle);
        applyXButton.setPreferredSize(middle);
        resetXButton.setPreferredSize(middle);
        printButton.setPreferredSize(middle);
        trendAreaColorButton.setPreferredSize(big);
        panelColorButton.setPreferredSize(big);
        applyXButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if(Dialogs.confirmDialog("Применить пределы времени?") != 0){
                    return;
                }
                fillDateFields();
                CommonFunctions.fillLimitField(gridXField, gridYField, mainPanel, -1, CommonFunctions.GRID);
                mainPanel.updateAllPanels();
                updateTexts();
            }
        });
        resetXButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if(Dialogs.confirmDialog("Сбросить пределы времени?") != 0){
                    return;
                }
                mainPanel.trends.applyZeroTrendTime(true);
                resetTexts();
                mainPanel.updateAllPanels();
            }
        });
        trendAreaColorButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (mainPanel.SCCMain != null) {
                    mainPanel.SCCMain.close();
                }
                mainPanel.SCCMain = new SCCTrendArea(mainPanel, e.getLocationOnScreen());
            }
        });
        panelColorButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (mainPanel.SCCMain != null) {
                    mainPanel.SCCMain.close();
                }
                mainPanel.SCCMain = new SCCPanelArea(mainPanel, e.getLocationOnScreen());
            }
        });
        printButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                PrinterJob pjob = PrinterJob.getPrinterJob();
                PageFormat preformat = pjob.defaultPage();
                preformat.setOrientation(PageFormat.LANDSCAPE);
                PageFormat postformat = pjob.pageDialog(preformat);
                //If user does not hit cancel then print.
                if (preformat != postformat) {
                    //Set print component
                    pjob.setPrintable(new Printer(mainPanel), postformat);
                    if (pjob.printDialog()) {
                        try {
                            pjob.print();
                        } catch (PrinterException ex) {
                            Dialogs.errorDialog("Ошибка печати:" + ex.getMessage());
                        }
                    }
                }
            }
        });
        updateTexts();
        updateColor();
        add(xStartLabel);
        add(xStartDate);
        add(xFinLabel);
        add(xFinDate);
        add(gridXLabel);
        add(gridXField);
        add(gridYLabel);
        add(gridYField);
        add(applyXButton);
        add(resetXButton);
        add(printButton);
        add(panelColorButton);
        add(trendAreaColorButton);
        revalidate();
        repaint();
    }

    public void updateColor(){
        trendAreaColorButton.setBackground(mainPanel.trendPanel.color);
        trendAreaColorButton.setForeground(mainPanel.trendPanel.inverseColor);
        panelColorButton.setBackground(mainPanel.color);
        panelColorButton.setForeground(mainPanel.inverseColor);
        xStartLabel.setForeground(mainPanel.inverseColor);
        xFinLabel.setForeground(mainPanel.inverseColor);
        gridXLabel.setForeground(mainPanel.inverseColor);
        gridYLabel.setForeground(mainPanel.inverseColor);
        setBackground(mainPanel.color);
    }

    public void resetTexts(){
        xStartDate.setValue(new Date(mainPanel.trends.zeroTrend.getStartTime()));
        xFinDate.setValue(new Date(mainPanel.trends.zeroTrend.getStartTime() + mainPanel.trends.zeroTrend.getDeltaTime()));
    }

    public void updateTexts(){
        xStartDate.setValue(new Date((long) mainPanel.trends.getX(0)));
        xFinDate.setValue(new Date((long)(mainPanel.trends.getX(0) + mainPanel.trends.getWidth(0))));
        gridXField.setText(String.valueOf((int) mainPanel.numXGrid));
        gridYField.setText(String.valueOf((int) mainPanel.numYGrid));
    }

    private void fillDateFields(){
        Date date1 = (Date) xStartDate.getModel().getValue();
        Date date2 = (Date) xFinDate.getModel().getValue();
        long time1 = date1.getTime();
        long time2 = date2.getTime();
        xStartDate.revalidate();
        xFinDate.revalidate();
        if(time1 >= time2){
            Dialogs.messageDialog("Нижний предел больше или равен верхнему, исправьте");
            return;
        }
        Trend trend = mainPanel.trends.getTrend(0);
        if(time1 < trend.startTime){
            time1 = trend.startTime;
        }
        if(time2 > trend.getDeltaTime() + trend.startTime){
            time2 = trend.getDeltaTime();
        }
        long difference = time2 - time1;
        if (difference > trend.areaWidthHighLim){
            difference = (long) trend.areaWidthHighLim;
            time1 = (long)(trend.startTime - difference / 2.0);
        } else if (difference < trend.areaWidthLowLim){
            difference = (long) trend.areaWidthLowLim;
            time1 = time2 - difference;
        }
        mainPanel.trends.setTrendAreaX(Trends.ALL, time1);
        mainPanel.trends.setTrendAreaWidth(Trends.ALL, difference);
    }
}
