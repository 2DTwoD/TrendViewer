package org.goznak.SCC;

import org.goznak.Containers.Palette;
import org.goznak.Instruments.Dialogs;
import org.goznak.Panels.MainPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class SimpleColorChooser extends JFrame {

    JPanel panel = new JPanel(new GridLayout(6, 8));
    JButton okButton = new JButton("Выбрать");
    Color color;
    Color currentColor;
    MainPanel mainPanel;
    JButton selectedButton;

    public SimpleColorChooser(MainPanel mainPanel, Color currentColor, Point location){
        super();
        this.mainPanel = mainPanel;
        this.currentColor = currentColor;
        setLocation(location);
        setLayout(new GridBagLayout());
        setResizable(false);
        setAlwaysOnTop(true);
        setTitle("Выбор цвета");
        GridBagConstraints constraints = new GridBagConstraints();
        Dimension dim = new Dimension(20, 20);
        Dimension butDim = new Dimension(160, 20);
        color = currentColor;
        for (int i = 0; i < Palette.colors.length; i ++){
            JButton button = new JButton("X");
            if (i == 0){
                selectedButton = button;
            }
            button.setContentAreaFilled(false);
            button.setOpaque(true);
            button.setPreferredSize(dim);
            button.setBackground(Palette.colors[i]);
            button.setForeground(Palette.getInverseColor(Palette.colors[i]));
            if (color.equals(Palette.colors[i])){
                selectedButton = button;
                button.setBorder(BorderFactory.createLineBorder(Palette.getInverseColor(color), 2));
            } else {
                button.setBorder(BorderFactory.createEmptyBorder());
            }
            if (Palette.freeColors.contains(Palette.colors[i])){
                button.setText("");
            }
            int tmpI = i;
            button.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    color = Palette.colors[tmpI];
                    okButton.setBackground(color);
                    okButton.setForeground(Palette.getInverseColor(color));
                    selectedButton.setBorder(BorderFactory.createEmptyBorder());
                    button.setBorder(BorderFactory.createLineBorder(Palette.getInverseColor(color), 2));
                    selectedButton = button;
                }
            });
            panel.add(button);
        }
        okButton.setPreferredSize(butDim);
        okButton.setBackground(currentColor);
        okButton.setForeground(Palette.getInverseColor(currentColor));
        constraints.gridx = 0;
        constraints.gridy = 0;
        add(panel, constraints);
        constraints.gridy = 1;
        add(okButton, constraints);
        setResizable(false);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setVisible(true);
        pack();
    }

    protected boolean selectButtonAction(){
        if (color != currentColor && !Palette.freeColors.contains(color)){
            close();
            Dialogs.messageDialog("Такой цвет уже используется, выберите другой");
            return false;
        }
        Palette.changeColor(currentColor, color);
        mainPanel.updateAllPanels();
        close();
        return true;
    }

    public void close(){
        setVisible(false);
        dispose();
    }
}
