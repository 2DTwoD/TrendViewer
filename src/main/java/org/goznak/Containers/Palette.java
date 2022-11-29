package org.goznak.Containers;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;

public class Palette {

    public static Color[] colors = {
            new Color(255, 128, 128),
            new Color(255, 255, 128),
            new Color(128, 255, 128),
            new Color(0, 255, 128),
            new Color(128, 255, 255),
            new Color(0, 128, 255),
            new Color(255, 128, 192),
            new Color(255, 128, 255),
            new Color(255, 0, 0),
            new Color(255, 255, 0),
            new Color(128, 255, 0),
            new Color(0, 255, 64),
            new Color(0, 255, 255),
            new Color(0, 128, 192),
            new Color(128, 128, 192),
            new Color(255, 0, 255),
            new Color(128, 64, 64),
            new Color(255, 128, 64),
            new Color(0, 255, 0),
            new Color(0, 128, 128),
            new Color(0, 64, 128),
            new Color(128, 128, 255),
            new Color(128, 0, 64),
            new Color(255, 0, 128),
            new Color(128, 0, 0),
            new Color(255, 128, 0),
            new Color(0, 128, 0),
            new Color(0, 128, 64),
            new Color(0, 0, 255),
            new Color(0, 0, 160),
            new Color(128, 0, 128),
            new Color(128, 0, 255),
            new Color(64, 0, 0),
            new Color(128, 64, 0),
            new Color(0, 64, 0),
            new Color(0, 64, 64),
            new Color(0, 0, 128),
            new Color(0, 0, 64),
            new Color(64, 0, 64),
            new Color(64, 0, 128),
            new Color(0, 0, 0),
            new Color(128, 128, 0),
            new Color(128, 128, 64),
            new Color(128, 128, 128),
            new Color(64, 128, 128),
            new Color(192, 192, 192),
            new Color(238, 238, 238),
            new Color(255, 255, 255)
    };
    static public ArrayList<Color> freeColors = new ArrayList<>(Arrays.asList(colors));

    public static Color getRandomColor(){
        if(freeColors.size() == 0){
            return null;
        }
        int random = (int)(Math.random() * freeColors.size());
        Color result = freeColors.get(random);
        freeColors.remove(random);
        return result;
    }

    public static Color getSpecificColor(int index){
        for(int i = 0; i < freeColors.size(); i++) {
            if(freeColors.get(i).equals(colors[index])){
                freeColors.remove(i);
                return colors[index];
            }
        }
        return getRandomColor();
    }

    public static void changeColor(Color oldColor, Color newColor){
        for(Color clr: freeColors){
            if(clr.equals(oldColor)){
                return;
            }
        }
        freeColors.add(oldColor);
        if (newColor != null){
            for(int i = 0; i < freeColors.size(); i++) {
                if(freeColors.get(i).equals(newColor)){
                    freeColors.remove(i);
                    return;
                }
            }
        }
    }

    public static Color getInverseColor(Color color){
        if(color.getGreen() >= 160){
            return new Color(0, 0, 0);
        }
        return new Color(255, 255, 255);
    }

    public static Color getGridColor(Color color){
        return new Color(128 + color.getRed() / 4, 128 + color.getGreen() / 4, 128 + color.getBlue() / 4);
    }
}
