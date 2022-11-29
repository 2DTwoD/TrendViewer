package org.goznak.Panels;

import org.goznak.Containers.Trends;
import org.goznak.Instruments.Dialogs;
import org.goznak.Instruments.VerticalLayout;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LegendPanel extends JPanel {

    MainPanel mainPanel;
    Dimension size;
    public JPanel commonPanel;
    public SettingsPanel settingsPanel;
    JPanel strokesPanelWithButtons = new JPanel(new VerticalLayout(new JPanel(), 0, 0, VerticalLayout.LEFT));
    public JPanel strokesPanel  = new JPanel(new VerticalLayout(new JPanel(), 0, 0, VerticalLayout.LEFT));
    JButton addTrendButton = new JButton("+Добавить тренд+");
    final Pattern newStrokePattern = Pattern.compile("\\n");
    final Pattern valuePattern = Pattern.compile("[-+]?(0|[1-9]\\d*)([.,]\\d+)?\\s-\\s");
    final Pattern timePattern = Pattern.compile("\\s-\\s[-+]?(0|[1-9]\\d*)([.,]\\d+)?");

    public LegendPanel(MainPanel mainPanel){
        super(new FlowLayout(FlowLayout.LEFT, 0,0));
        commonPanel = new JPanel(new GridBagLayout());
        this.mainPanel = mainPanel;
        settingsPanel = new SettingsPanel(mainPanel);
        addTrendButton.setPreferredSize(new Dimension(150, 20));
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.NORTH;
        commonPanel.add(settingsPanel, constraints);
        commonPanel.add(strokesPanelWithButtons, constraints);
        strokesPanelWithButtons.add(strokesPanel);
        strokesPanelWithButtons.add(addTrendButton);
        add(commonPanel);
        update();
        addTrendButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                JFileChooser fileOpen = new JFileChooser();
                int ret = fileOpen.showDialog(null, "Открыть файл");
                if (ret == JFileChooser.APPROVE_OPTION) {
                    File file = fileOpen.getSelectedFile();
                    StringBuilder fileContent = new StringBuilder();
                    try(FileReader reader = new FileReader(file))
                    {
                        int c;
                        while((c = reader.read()) != -1){
                            fileContent.append((char) c);
                        }
                    }
                    catch(IOException ex){
                        Dialogs.errorDialog("Ошибка чтения файла: " + ex.getMessage());
                        return;
                    }
                    Matcher matcher;
                    ArrayList<Double> valueList = new ArrayList<>();
                    ArrayList<Long> timeList = new ArrayList<>();
                    String[] strArray = newStrokePattern.split(fileContent);
                    for(String s:strArray){
                        s = s.replace(',', '.');
                        matcher = valuePattern.matcher(s);
                        if (matcher.find()) {
                            valueList.add(Double.valueOf(s.substring(matcher.start(), matcher.end() - 3)));
                        } else {
                            Dialogs.errorDialog("""
                                    Неверный формат значений. Файл должен содержать
                                    текст в формате: X.X - X
                                    где X - цифра""");
                            return;
                        }
                        matcher = timePattern.matcher(s);
                        if (matcher.find()) {
                            timeList.add(Long.valueOf(s.substring(matcher.start() + 3, matcher.end())));
                        } else {
                            Dialogs.errorDialog("""
                                    Неверный формат значений. Файл должен содержать
                                    текст в формате: X.X - X
                                    где X - цифра""");
                            return;
                        }
                    }
                    /*valueList = new ArrayList<>();
                    timeList = new ArrayList<>();
                    double rnd = 40000000 + Math.random() * 100000000L;*/
                    /*for(long i = 0; i <= rnd; i += 1000){
                        valueList.add(Math.random() * 1000 * Math.cos(i / 200000.0));
                        timeList.add(i);
                    }*/
                    /*for(long i = 0; i < rnd; i+=1000){
                        valueList.add((double)i);
                        timeList.add(i);
                    }
                    valueList.set(20000, 0.0);
                    valueList.set(40000, rnd);*/
                    mainPanel.trends.addTrend(valueList, timeList, file.getName());
                    mainPanel.updateAllPanels();
                }
            }
        });
    }

    public void update(){
        size = mainPanel.calculateComponentSize(MainPanel.LEGEND, Trends.ALL);
        addTrendButton.setVisible(mainPanel.trends.getTrendMapSize() < mainPanel.trends.maxNumTrends);
        updateLegendColors();
        setPreferredSize(size);
        updateComponent(commonPanel);
        SwingUtilities.invokeLater(() -> updateComponent(strokesPanelWithButtons));
        updateComponent(strokesPanel);
        updateComponent(settingsPanel);
        updateComponent(this);
    }

    public void updateLegendColors(){
        for (TrendStroke stroke: mainPanel.trends.strokeMap.values()) {
            stroke.updateColor();
        }
        strokesPanel.setBackground(mainPanel.color);
        strokesPanelWithButtons.setBackground(mainPanel.color);
        settingsPanel.updateColor();
        commonPanel.setBackground(mainPanel.color);
        setBackground(mainPanel.color);
    }

    private void updateComponent(Component component) {
        SwingUtilities.invokeLater(() -> {
            component.revalidate();
            component.repaint();
        });
    }
}
