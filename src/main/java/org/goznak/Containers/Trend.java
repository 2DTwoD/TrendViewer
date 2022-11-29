package org.goznak.Containers;

import org.goznak.Panels.MainPanel;

import java.awt.*;
import java.text.DecimalFormat;
import java.util.*;
import java.util.List;

public class Trend {

    public int id;
    List<Double> valueList;
    List<Long> timeList;
    ArrayList<Integer> valueCoord;
    ArrayList<Integer> timeCoord;
    MonitoringArea area = new MonitoringArea(0.0, 0.0, 1.0, 1.0);
    MainPanel mainPanel;
    public Color color;
    public Color inverseColor;
    public String name;
    public Stroke dash = new BasicStroke();
    public int lineType = LINE_TYPE0;
    public double shiftXGrid = 0;
    public double shiftYGrid = 0;
    final int charWidth = 8;
    public boolean hide = false;
    long timeStep = 0;
    public long startTime = 0;
    long finTime = 1;
    long deltaTime = 0;
    public double maxValue = 1.0;
    public double minValue = 0.0;
    public double areaWidthLowLim = 0.0;
    public double areaWidthHighLim = 0.0;
    public double areaHeightLowLim = 0.0;
    public double areaHeightHighLim = 0.0;
    double deltaValue = 0.0;
    public String numTemplate;
    Trend zeroTrend;
    public static final int LINE_TYPE0 = 0;
    public static final int LINE_TYPE1 = 1;
    public static final int LINE_TYPE2 = 2;

    public Trend(int id, String name, List<Double> valueList, List<Long> timeList, MainPanel mainPanel, Color color){
        this.id = id;
        this.valueList = valueList;
        this.timeList = timeList;
        this.mainPanel = mainPanel;
        this.name = name;
        if(id != 0) {
            zeroTrend = mainPanel.trends.zeroTrend;
        }
        setColor(color);
        valueCoord = new ArrayList<>();
        timeCoord = new ArrayList<>();
        if(timeList.size() >=2) {
            maxValue = valueList.stream().max(Double::compare).orElse(maxValue);
            minValue = valueList.stream().min(Double::compare).orElse(minValue);
            deltaValue = maxValue - minValue;
            startTime = timeList.get(0);
            finTime = timeList.get(timeList.size() - 1);
            deltaTime = finTime - startTime;
            timeStep = 10 * (timeList.get(1) - timeList.get(0));
            areaHeightLowLim = deltaValue / 10000.0;
            areaHeightHighLim = deltaValue * 1000.0;
            areaWidthLowLim = 1.0;
            areaWidthHighLim = deltaTime * 100.0;
            setDefaultAreaY();
            modifyNumTemplate();
        }
    }

    private void modifyNumTemplate(){
        String num = String.valueOf(deltaValue);
        int numOfDig = 0;
        for (int i = 0; i < num.length(); i++){
            if(num.charAt(i) == '.' && i != 1){
                numOfDig = i;
            }
            if(num.charAt(i) == 'E'){
                numOfDig = 7;
                break;
            }
        }
        if(numOfDig > 6){
            numTemplate = "#0";
        } else if (numOfDig > 0){
            numTemplate = "#0." + "0".repeat(6 - numOfDig);
        } else {
            int numOfZeroAfterPoint = 0;
            for (int i = numOfDig; i < num.length(); i++){
                if(num.charAt(i) != '0'){
                    numOfZeroAfterPoint = i - numOfDig;
                    break;
                }
            }
            numTemplate = "#" + "0." + "0".repeat(numOfZeroAfterPoint) + "0".repeat(6);
        }
    }

    public void setDefaultAreaY(){
        setY(minValue);
        setHeight(deltaValue);
    }

    public void setTimeAreaFromZeroTrend(){
        if (id == 0) {
            return;
        }
        areaWidthHighLim = zeroTrend.areaWidthHighLim;
        setX(zeroTrend.area.x);
        setWidth(zeroTrend.area.width);
        //setDefaultAreaValue();
    }

    public double getXRatio(){
        return mainPanel.trendPanel.getPreferredSize().width / area.width;
    }

    public double getYRatio(){
        return mainPanel.trendPanel.getPreferredSize().height / area.height;
    }

    public double getGridWidth(){
        return area.width / mainPanel.numXGrid;
    }

    public double getGridHeight(){
        return area.height / mainPanel.numYGrid;
    }

    public ArrayList<Integer> getValueCoord(){
        return valueCoord;
    }

    public ArrayList<Integer> getTimeCoord(){
        return timeCoord;
    }

    public long getStartTime() {
        return startTime;
    }

    public long getDeltaTime(){
        return deltaTime;
    }

    public double[] getCurrentExtremums() {
        long time;
        ArrayList<Double> valueListTmp = new ArrayList<>();
        for (int i = 0; i < valueList.size(); i++){
            time = timeList.get(i);
            if(time >= area.x && time <= (area.x + area.width)){
                valueListTmp.add(valueList.get(i));
            }
        }
        if (valueListTmp.size() == 0){
            return null;
        }
        double min = valueListTmp.stream().min(Double::compare).orElse(minValue);
        double max = valueListTmp.stream().max(Double::compare).orElse(maxValue);
        return new double[]{min, max};
    }

    public double getX(){
        return area.x;
    }

    public double getY(){
        return area.y;
    }

    public double getWidth(){
        return area.width;
    }

    public double getHeight(){
        return area.height;
    }

    public void setX(double value){
        area.x = value;
    }

    public void setY(double value){
        area.y = value;
    }

    public boolean setWidth(double value){
        area.width = value;
        if (id ==0){
            return true;
        }
        if(value < areaWidthLowLim){
            area.width = areaWidthLowLim;
            return false;
        } else if(value > areaWidthHighLim){
            area.width = areaWidthHighLim;
            return false;
        }
        return true;
    }

    public boolean setHeight(double value){
        area.height = value;
        if(value < areaHeightLowLim){
            area.height = areaHeightLowLim;
            return false;
        } else if(value > areaHeightHighLim){
            area.height = areaHeightHighLim;
            return false;
        }
        return true;
    }

    public void setDefaultAreaValue(){
        double[] extremums = getCurrentExtremums();
        if(extremums == null || hide){
            return;
        }
        setY(extremums[0]);
        setHeight(extremums[1] - extremums[0]);
    }

    public void setColor(Color color){
        this.color = color;
        inverseColor = Palette.getInverseColor(color);
    }

    public void setLineType(int type){
        switch(type){
            case LINE_TYPE0 -> dash = new BasicStroke();
            case LINE_TYPE1 -> dash = new BasicStroke(2);
            case LINE_TYPE2 -> dash = new BasicStroke(4);
        }
    }

    public void incrementX(double value){
        setX(area.x + value);
    }

    public void incrementY(double value){
        setY(area.y + value);
    }

    public boolean incrementWidth(double value){
        return setWidth(area.width + value);
    }

    public boolean incrementHeight(double value){
        return setHeight(area.height + value);
    }

    public void updateShiftGrid(){
        shiftXGrid = mainPanel.PXshiftXGrid / getXRatio();
        shiftYGrid = mainPanel.PXshiftYGrid / getYRatio();
    }

    public int calculateYAxisWidth(){
        String valueLow = new DecimalFormat(numTemplate).format(area.y);
        String valueHigh = new DecimalFormat(numTemplate).format(area.y + area.height);
        if(valueHigh.length() > valueLow.length()){
            return charWidth * valueHigh.length();
        }else {
            return charWidth * valueLow.length();
        }
    }

    public void calculateAreaCoord(){
        valueCoord.clear();
        timeCoord.clear();
        long time;
        for (int i = 0; i < valueList.size(); i++){
            time = timeList.get(i);
            if(time >= (area.x - timeStep) && time <= (area.x + area.width + timeStep)){
                valueCoord.add((int) ((valueList.get(i) - area.y) * getYRatio()));
                timeCoord.add((int) ((time - area.x) * getXRatio()));
            }
        }
        int allPX = mainPanel.trendPanel.getPreferredSize().width;
        if(valueCoord.size() <= allPX){
            return;
        }
        Integer[] tmpTimeCoord = new Integer[allPX];
        Integer[] tmpValueCoord = new Integer[allPX];
        int shrinking = valueCoord.size() / allPX;
        int remainder = valueCoord.size() % allPX;
        int upKoef = remainder > 0? 1: 0;
        int realShrinking = shrinking + upKoef;
        int[] calcExtremumValue = new int[realShrinking];
        int[] calcExtremumValueAppend;
        int index;
        int tmpCoordIndex = 0;
        int extremumIndex = 0;
        int min;
        int max;
        int extremum;
        double avg;
        for (int i = 0; i < valueCoord.size(); i++) {
            calcExtremumValue[extremumIndex] = valueCoord.get(i);
            if (extremumIndex >= realShrinking - 1) {
                min = Arrays.stream(calcExtremumValue).min().getAsInt();
                max = Arrays.stream(calcExtremumValue).max().getAsInt();
                if(calcExtremumValue.length > 2 || calcExtremumValue.length == 1) {
                    avg = Arrays.stream(calcExtremumValue).average().orElse(calcExtremumValue[0]);
                } else {
                    calcExtremumValueAppend = Arrays.copyOf(calcExtremumValue, 3);
                    index = i == (valueCoord.size() - 1)? i: i + 1;
                    calcExtremumValueAppend[2] = valueCoord.get(index);
                    avg = Arrays.stream(calcExtremumValueAppend).average().orElse(calcExtremumValue[0]);
                }
                extremum = max - avg > avg - min ? max : min;
                tmpValueCoord[tmpCoordIndex] = extremum;
                tmpTimeCoord[tmpCoordIndex] = timeCoord.get(i);
                remainder--;
                upKoef = remainder > 0 ? 1 : 0;
                realShrinking = shrinking + upKoef;
                calcExtremumValue = new int[realShrinking];
                extremumIndex = 0;
                tmpCoordIndex++;
            } else {
                extremumIndex++;
            }
        }
        valueCoord.clear();
        timeCoord.clear();
        Collections.addAll(valueCoord, tmpValueCoord);
        Collections.addAll(timeCoord, tmpTimeCoord);
    }
}