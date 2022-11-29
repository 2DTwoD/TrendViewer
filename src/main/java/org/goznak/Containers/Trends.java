package org.goznak.Containers;

import org.goznak.Instruments.Dialogs;
import org.goznak.Panels.MainPanel;
import org.goznak.Panels.TrendStroke;
import org.goznak.Panels.YAxisPanel;

import java.awt.*;
import java.util.*;
import java.util.List;

public class Trends {

    private int id = 1;
    static public final int ALL = -1;
    public final int maxNumTrends = 10;
    public final long maxTimeWidthForDefault = 86000000;
    HashMap<Integer, Trend> trendMap = new HashMap<>();
    HashMap<Integer, YAxisPanel> yAxisMap = new HashMap<>();
    public HashMap<Integer, TrendStroke> strokeMap = new HashMap<>();
    MainPanel mainPanel;
    public Trend zeroTrend;

    public Trends(MainPanel mainPanel){
        this.mainPanel = mainPanel;
        zeroTrend = new Trend(0, "zero", new ArrayList<>(Arrays.asList(0.0, 1.0)),
                new ArrayList<>(Arrays.asList(0L, 1L)), mainPanel, Color.BLACK);
        trendMap.put(0, zeroTrend);
    }

    public void addTrend(List<Double> valueList, List<Long> timeList, String name){
        if (trendMap.size() > maxNumTrends){
            Dialogs.messageDialog("Слишком много трендов");
            return;
        }
        Color color = Palette.getRandomColor();
        if (valueList.size() != timeList.size()){
            Dialogs.errorDialog("Ошибка! Количество измерений значения не равно количеству измерений времени");
        }
        if (valueList.size() < 2){
            Dialogs.errorDialog("Ошибка! Должно быть минимум 2 измерения");
        }
        Trend trend = new Trend(id, name, valueList, timeList, mainPanel, color);
        trendMap.put(id, trend);
        YAxisPanel yAxisPanel = new YAxisPanel(mainPanel, id);
        yAxisMap.put(id, yAxisPanel);
        TrendStroke trendStroke = new TrendStroke(mainPanel, trend);
        strokeMap.put(id, trendStroke);
        mainPanel.rootYAxis.add(yAxisPanel);
        mainPanel.legendPanel.strokesPanel.add(trendStroke);
        applyZeroTrendTime(false);
        mainPanel.legendPanel.settingsPanel.resetTexts();
        id++;
        mainPanel.updateAllPanels();
    }

    public void removeTrend(int id){
        Palette.changeColor(getColor(id), null);
        mainPanel.rootYAxis.remove(yAxisMap.get(id));
        mainPanel.legendPanel.strokesPanel.remove(strokeMap.get(id));
        trendMap.remove(id);
        yAxisMap.remove(id);
        strokeMap.remove(id);
        applyZeroTrendTime(false);
        mainPanel.legendPanel.settingsPanel.resetTexts();
        mainPanel.updateAllPanels();
    }

    public void update(){
        for (YAxisPanel panel: yAxisMap.values()) {
            panel.update();
        }
        updateShiftGrid(ALL);
    }

    public void applyZeroTrendTime(boolean reset){
        long startTime = -1;
        long finTime = 1;
        for(Trend trend: trendMap.values()){
            if(trend.id == 0){
                continue;
            }
            startTime = startTime != -1? Math.min(startTime, trend.startTime): trend.startTime;
            finTime = Math.max(finTime, trend.finTime);
        }
        long deltaTime = finTime - startTime;
        double areaWidthHighLim = deltaTime * 100.0;
        zeroTrend.startTime = startTime;
        zeroTrend.finTime = finTime;
        zeroTrend.deltaTime = deltaTime;
        zeroTrend.areaWidthHighLim = areaWidthHighLim;
        if(!reset) {
            deltaTime = Math.min(deltaTime, maxTimeWidthForDefault);
        }
        zeroTrend.setX(finTime - deltaTime);
        zeroTrend.setWidth(deltaTime);
        for (Trend trnd: trendMap.values()) {
            trnd.setTimeAreaFromZeroTrend();
        }
    }

    public int getTrendMapSize(){
        return trendMap.size() - 1;
    }

    public Trend getTrend(int id){
        return trendMap.get(id);
    }

    public Color getColor(int id){
        if (id == ALL){
            id = 0;
        }
        return trendMap.get(id).color;
    }

    public Stroke getDash(int id){
        if (id == ALL){
            id = 0;
        }
        return trendMap.get(id).dash;
    }

    public boolean getHide(int id){
        if (id == ALL){
            id = 0;
        }
        return trendMap.get(id).hide;
    }

    public double getX(int id){
        if (id == ALL){
            id = 0;
        }
        return trendMap.get(id).area.x;
    }

    public double getWidth(int id){
        if (id == ALL){
            id = 0;
        }
        return trendMap.get(id).area.width;
    }

    public double getHeight(int id){
        if (id == ALL){
            id = 0;
        }
        return trendMap.get(id).area.height;
    }

    public double getDeltaValue(int id){
        if (id == ALL){
            id = 0;
        }
        return trendMap.get(id).deltaValue;
    }

    public double getDeltaTime(int id){
        if (id == ALL){
            id = 0;
        }
        return trendMap.get(id).deltaTime;
    }

    public ArrayList<Integer> getValueCoord(int id){
        if (id == ALL){
            id = 0;
        }
        return trendMap.get(id).getValueCoord();
    }

    public ArrayList<Integer> getTimeCoord(int id){
        if (id == ALL){
            id = 0;
        }
        return trendMap.get(id).getTimeCoord();
    }

    public Set<Integer> getIdSet(){
        return trendMap.keySet();
    }

    public void setTrendAreaX(int id, double value){
        if (id == ALL){
            for (Trend trend : trendMap.values()) {
                trend.setX(value);
            }
            return;
        }
        trendMap.get(id).setX(value);
    }

    public void setTrendAreaY(int id, double value){
        if (id == ALL){
            for (Trend trend : trendMap.values()) {
                trend.setY(value);
            }
            return;
        }
        trendMap.get(id).setY(value);
    }

    public void setTrendAreaWidth(int id, double value){
        if (id == ALL){
            for (Trend trend : trendMap.values()) {
                trend.setWidth(value);
            }
            return;
        }
        trendMap.get(id).setWidth(value);
    }

    public void setTrendAreaHeight(int id, double value){
        if (id == ALL){
            for (Trend trend : trendMap.values()) {
                trend.setHeight(value);
            }
            return;
        }
        trendMap.get(id).setHeight(value);
    }

    public void incrementTrendAreaX(int id, double value){
        if (id == ALL){
            for (Trend trend : trendMap.values()) {
                trend.incrementX(value / trend.getXRatio());
            }
            return;
        }
        trendMap.get(id).incrementX(value / trendMap.get(id).getXRatio());
    }

    public void incrementTrendAreaY(int id, double value){
        if (id == ALL){
            for (Trend trend : trendMap.values()) {
                if(!trend.hide) {
                    trend.incrementY(value / trend.getYRatio());
                }
            }
            return;
        }
        if(!trendMap.get(id).hide) {
            trendMap.get(id).incrementY(value / trendMap.get(id).getYRatio());
        }
    }

    public boolean incrementTrendAreaWidth(int id, double value){
        if (id == ALL){
            for (Trend trend : trendMap.values()) {
                trend.incrementWidth(value / trend.getXRatio());
            }
            return true;
        }
        return trendMap.get(id).incrementWidth(value / trendMap.get(id).getXRatio());
    }

    public boolean incrementTrendAreaHeight(int id, double value){
        if (id == ALL){
            for (Trend trend : trendMap.values()) {
                if(!trend.hide) {
                    trend.incrementHeight(value / trend.getYRatio());
                }
            }
            return true;
        }
        if(!trendMap.get(id).hide) {
            return trendMap.get(id).incrementHeight(value / trendMap.get(id).getYRatio());
        }
        return false;
    }

    public void setStack(){
        Set<Integer> trendMapSet = new HashSet<>(getIdSet());
        for (int id: getIdSet()) {
            Trend trend = getTrend(id);
            if(id == 0 || trend.hide){
                trendMapSet.remove(id);
                continue;
            }
            if (trend.getCurrentExtremums() == null){
                trendMapSet.remove(id);
            }
        }
        double[] extremums;
        int quantity = trendMapSet.size();
        int i = 1;
        for (int id: trendMapSet) {
            extremums = getTrend(id).getCurrentExtremums();
            double deltaValue = (extremums[1] - extremums[0]);
            setTrendAreaY(id, extremums[0] - deltaValue * (quantity - i));
            setTrendAreaHeight(id, deltaValue * quantity);
            i++;
        }
    }

    public void setDefaultAreaValue(){
        for (Trend trend : trendMap.values()) {
            trend.setDefaultAreaValue();
        }
    }

    public void updateShiftGrid(int id){
        if (id == ALL){
            for (Trend trend : trendMap.values()) {
                trend.updateShiftGrid();
            }
            return;
        }
        trendMap.get(id).updateShiftGrid();
    }

    public void calculateYAxisWidth(int id) {
        mainPanel.widthOfYAxis = 0;
        if (id == ALL) {
            for (Trend trend : trendMap.values()) {
                if (trend.id != 0 && !trend.hide) {
                    mainPanel.widthOfYAxis += trend.calculateYAxisWidth();
                }
            }
            return;
        }
        trendMap.get(id).calculateYAxisWidth();
        mainPanel.widthOfYAxis = trendMap.get(id).calculateYAxisWidth();
    }
}
