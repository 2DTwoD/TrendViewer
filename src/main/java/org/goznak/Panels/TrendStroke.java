package org.goznak.Panels;

import org.goznak.Instruments.CommonFunctions;
import org.goznak.Containers.Palette;
import org.goznak.Containers.Trend;
import org.goznak.Instruments.Dialogs;
import org.goznak.SCC.SCCTrendCurve;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.DecimalFormat;
import java.util.Objects;

public class TrendStroke extends JPanel {

    MainPanel mainPanel;
    Trend trend;
    JTextField nameField = new JTextField();
    JButton colorButton = new JButton();
    String[] lineType = {"тонкий","средний", "толстый"};
    JComboBox<String> lineTypeCombo = new JComboBox<>(lineType);
    JLabel lowLabel = new JLabel("Y низ: ", JLabel.RIGHT);
    JTextField lowLimField = new JTextField();
    JLabel highLabel = new JLabel("Y верх: ", JLabel.RIGHT);
    JTextField highLimField = new JTextField();
    JButton applyButton = new JButton("Применить");
    JButton resetButton = new JButton("Сбросить");
    JButton hideButton = new JButton("O");
    JButton deleteButton = new JButton("X");
    static Dimension small = new Dimension(50, 20);
    static Dimension middle = new Dimension(100, 20);
    static Dimension big = new Dimension(150, 20);

    public TrendStroke(MainPanel mainPanel, Trend trend){
        super(new FlowLayout(FlowLayout.LEFT, 0, 0));
        this.mainPanel = mainPanel;
        this.trend = trend;
        nameField.setText(trend.name);
        lowLimField.setText(new DecimalFormat(trend.numTemplate).format(trend.minValue));
        highLimField.setText(new DecimalFormat(trend.numTemplate).format(trend.maxValue));
        lineTypeCombo.setSelectedIndex(trend.lineType);
        nameField.setPreferredSize(big);
        colorButton.setPreferredSize(small);
        lineTypeCombo.setPreferredSize(middle);
        lowLabel.setPreferredSize(small);
        lowLimField.setPreferredSize(middle);
        highLabel.setPreferredSize(small);
        highLimField.setPreferredSize(middle);
        applyButton.setPreferredSize(middle);
        resetButton.setPreferredSize(middle);
        hideButton.setPreferredSize(small);
        deleteButton.setPreferredSize(small);
        updateColor();
        add(nameField);
        add(colorButton);
        add(lineTypeCombo);
        add(lowLabel);
        add(lowLimField);
        add(highLabel);
        add(highLimField);
        add(applyButton);
        add(resetButton);
        add(hideButton);
        add(deleteButton);
        colorButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (mainPanel.SCCMain != null) {
                    mainPanel.SCCMain.close();
                }
                mainPanel.SCCMain = new SCCTrendCurve(mainPanel, trend.color, e.getLocationOnScreen(), trend);
            }
        });
        lineTypeCombo.addActionListener(e -> {
            String selectedItem = (String)lineTypeCombo.getSelectedItem();
            if(Objects.equals(selectedItem, lineType[0])){
                trend.setLineType(Trend.LINE_TYPE0);
            } else if(Objects.equals(selectedItem, lineType[1])){
                trend.setLineType(Trend.LINE_TYPE1);
            } else if(Objects.equals(selectedItem, lineType[2])){
                trend.setLineType(Trend.LINE_TYPE2);
            }
            mainPanel.updateAllPanels();
        });
        applyButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                CommonFunctions.fillLimitField(lowLimField, highLimField, mainPanel, trend.id, CommonFunctions.Y_AXIS);
                mainPanel.updateAllPanels();
            }
        });
        resetButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                trend.setDefaultAreaValue();
                mainPanel.updateAllPanels();
            }
        });
        hideButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                trend.hide = !trend.hide;
                String butLabel = trend.hide? "-": "O";
                hideButton.setText(butLabel);
                mainPanel.updateAllPanels();
            }
        });
        deleteButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if(Dialogs.confirmDialog("Удалить тренд '" + trend.name + "'?") != 0){
                    return;
                }
                mainPanel.trends.removeTrend(trend.id);
            }
        });
        nameField.addCaretListener(e -> trend.name = nameField.getText());
    }

    public void updateColor(){
        nameField.setBackground(trend.color);
        nameField.setForeground(trend.inverseColor);
        colorButton.setBackground(trend.color);
        setBackground(mainPanel.color);
        lowLabel.setForeground(Palette.getInverseColor(mainPanel.color));
        highLabel.setForeground(Palette.getInverseColor(mainPanel.color));
    }
}
