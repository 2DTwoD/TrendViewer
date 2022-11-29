package org.goznak.Panels;

import org.goznak.Containers.Trends;

import javax.swing.*;

public class CommonPanel extends JPanel {

    public boolean moveFlag = false;
    public boolean zoomFlag = false;
    public boolean onlyGridFlag = false;
    public int panelType;
    MainPanel mainPanel;
    Trends trends;
    int id;

    public CommonPanel(int panelType, MainPanel mainPanel, int id){
        super();
        this.panelType = panelType;
        this.mainPanel = mainPanel;
        trends = mainPanel.trends;
        this.id = id;
    }
}
