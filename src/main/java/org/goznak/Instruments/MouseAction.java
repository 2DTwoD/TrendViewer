package org.goznak.Instruments;

import org.goznak.Panels.CommonPanel;
import org.goznak.Panels.MainPanel;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

public class MouseAction extends MouseAdapter {

    MainPanel mainPanel;
    CommonPanel panel;
    int id;

    public MouseAction(MainPanel mainPanel, CommonPanel panel, int id){
        this.mainPanel = mainPanel;
        this.panel = panel;
        this.id = id;
    }

    @Override
    public void mousePressed(MouseEvent e) {
        //ЛКМ
        if (e.getButton() == MouseEvent.BUTTON1) {
            mainPanel.updateMouseCoord(e.getXOnScreen(), e.getYOnScreen());
            panel.moveFlag = true;
        }
        //Колесо
        if (e.getButton() == MouseEvent.BUTTON2) {
            panel.onlyGridFlag = true;
            panel.moveFlag = true;
            mainPanel.updateMouseCoord(e.getXOnScreen(), e.getYOnScreen());
        }
        //ПКМ
        if (e.getButton() == MouseEvent.BUTTON3) {
            mainPanel.updateMouseCoord(e.getX(), e.getY());
            mainPanel.zoomMouseXStart = e.getX();
            mainPanel.zoomMouseYStart = e.getY();
            panel.zoomFlag = true;
        }
        mainPanel.updateAllPanels();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        panel.onlyGridFlag = false;
        panel.moveFlag = false;
        panel.zoomFlag = false;
        if (e.getButton() == MouseEvent.BUTTON3) {
            mainPanel.scaleArea(panel.panelType, id);
        }
        mainPanel.updateAllPanels();
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        int mouseX = 0;
        int mouseY = 0;
        if (panel.moveFlag) {
            mouseX = e.getXOnScreen();
            mouseY = e.getYOnScreen();
            mainPanel.dragAreaXY(mouseX, mouseY, panel.panelType, id, panel.onlyGridFlag);
        }
        if (panel.zoomFlag){
            if(panel.panelType == MainPanel.TREND_AREA) {
                mouseX = e.getX();
                mouseY = e.getY();
            } else {
                mouseX = e.getX();
                mouseY = e.getY();
                mainPanel.dragScale(mouseX, mouseY, panel.panelType, id);
            }
        }
        mainPanel.updateMouseCoord(mouseX, mouseY);
        mainPanel.updateAllPanels();
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e){
        mainPanel.stepScale(e.getX(), e.getY(), e.getWheelRotation(), id);
        mainPanel.updateAllPanels();
    }
}