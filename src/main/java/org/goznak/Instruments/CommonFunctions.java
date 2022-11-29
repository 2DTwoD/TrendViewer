package org.goznak.Instruments;

import org.goznak.Containers.Trend;
import org.goznak.Panels.MainPanel;

import javax.swing.*;
import java.text.DecimalFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommonFunctions {

    public static final int Y_AXIS = 0;
    public static final int GRID = 1;

    public static void fillLimitField(JTextField lowTxt, JTextField highTxt, MainPanel mainPanel, int id, int entity){
        String lowStr = getMatchDoubleString(lowTxt.getText());
        String highStr = getMatchDoubleString(highTxt.getText());
        if(lowStr == null || highStr == null){
            Dialogs.messageDialog("Некорректное значение верхнего или нижнего предела, исправьте");
            return;
        }
        double lowDbl = Double.parseDouble(lowStr.replace(',', '.'));
        double highDbl = Double.parseDouble(highStr.replace(',', '.'));
        if(entity != GRID && lowDbl >= highDbl){
            Dialogs.messageDialog("Нижний предел больше или равен верхнему, исправьте");
            return;
        }
        double difference = highDbl - lowDbl;
        String numTemplate = "";
        switch (entity){
            case Y_AXIS -> {
                Trend trend = mainPanel.trends.getTrend(id);
                if (difference > trend.areaHeightHighLim){
                    difference = trend.areaHeightHighLim;
                    lowDbl = trend.minValue - difference / 2.0;
                    highDbl = lowDbl + difference;
                } else if (difference < trend.areaHeightLowLim){
                    difference = trend.areaHeightLowLim;
                    lowDbl = highDbl - difference;
                }
                trend.setY(lowDbl);
                trend.setHeight(difference);
                numTemplate = trend.numTemplate;
            }
            case GRID -> {
                if (lowDbl > mainPanel.maxNumXGrid){
                    lowDbl = mainPanel.maxNumXGrid;
                } else if (lowDbl < 0.0){
                    lowDbl = 0.0;
                }
                if (highDbl > mainPanel.maxNumYGrid){
                    highDbl = mainPanel.maxNumYGrid;
                } else if (highDbl < 0.0){
                    highDbl = 0.0;
                }
                mainPanel.numXGrid = lowDbl;
                mainPanel.numYGrid = highDbl;
                numTemplate = "#0";
            }
        }
        lowTxt.setText(new DecimalFormat(numTemplate).format(lowDbl));
        highTxt.setText(new DecimalFormat(numTemplate).format(highDbl));
    }

    private static String getMatchDoubleString(String text){
        Pattern pattern = Pattern.compile("[-+]?(0|[1-9]\\d*)([.,]\\d+)?");
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            return text.substring(matcher.start(), matcher.end());
        }
        return null;
    }
}
