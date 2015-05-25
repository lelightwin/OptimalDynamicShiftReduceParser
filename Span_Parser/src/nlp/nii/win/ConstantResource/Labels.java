/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nlp.nii.win.ConstantResource;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author lelightwin
 */
public class Labels {

    /*-----------------------------------------------------------------------------------------------------------------------------------------------------------*/
    //<editor-fold defaultstate="collapsed" desc="constant resources">
    public static final HashMap<Integer, String> consMap = new HashMap<>();
    public static final HashMap<Integer, String> completeCons = new HashMap<>();
    public static final HashMap<Integer, String> incompleteCons = new HashMap<>();

    public static final HashMap<Integer, String> posMap = new HashMap<>();
    public static final HashMap<Integer, String> preTerminalMap = new HashMap<>();
    public static final HashMap<Integer, String> specialsMap = new HashMap<>();
    public static final HashMap<Integer, String> actionsMap = new HashMap<>();

    public static final HashMap<String, Integer> labelsMap = new HashMap<>();
    public static final HashMap<String, Integer> wordsMap = new HashMap<>();
    public static final String[] labelsArray = new String[Global.labelsNum];
    public static final String[] wordsArray = new String[Global.wordsNum];
    public static final HashSet<String> punctuation = new HashSet<>();

    public static final String[] featTypes = new String[]{
        "s0c_s0ft", "s0c_s0fw", "s0c_s0lt", "s0c_s0lw", "s0c_s0len",
        "s1c_s1ft", "s1c_s1fw", "s1c_s1lt", "s1c_s1lw", "s1c_s1len",
        "N0t_N0w", "N1t_N1w", "N2t_N2w", "N3t_N3w",
        "s1lw_s0fw", "s0ft_s1lw", "s1lt_s0fw", "s1lt_s0ft",
        "s1c_s0fw", "s0c_s1fw", "s1c_s0lw", "s0c_s1lw",
        "s0fw_N0w", "s0lw_N0w", "N0t_s0fw", "N0t_s0lw", "s0c_N0w", "s0c_N0t",
        "N0w_N1w", "N1t_N0w", "N0t_N1w", "N0t_N1t",
        "s1fw_N0w", "s1lw_N0w", "N0t_s1fw", "N0t_s1lw", "s1c_N0w", "s1c_N0t",
        "s0c_s1c_N0t", "s1c_N0t_s0fw", "s0c_N0t_s1fw", "s1c_N0t_s0lw", "s0c_N0t_s1lw", "s0c_s1c_N0w",
        "s0fw_s0len", "s0lw_s0len", "s0c_s0ft_s0len", "s0c_s0lt_s0len",
        "s1fw_s1len", "s1lw_s1len", "s1c_s1ft_s1len", "s1c_s1lt_s1len"
    };

    //</editor-fold>
    /*-----------------------------------------------------------------------------------------------------------------------------------------------------------*/
    //<editor-fold defaultstate="collapsed" desc="init block">
    static {
        loadMapAndArray(labelsMap, labelsArray, Global.labelsFile);
        loadMapAndArray(wordsMap, wordsArray, Global.wordsFile);

        loadMap(consMap, Global.constituentsFile);
        loadMap(incompleteCons, Global.incompleteConstituentsFile);
        loadMap(completeCons, Global.completeConstituentsFile);
        loadMap(posMap, Global.posFile);
        loadMap(preTerminalMap, Global.preTerminalFile);
        loadMap(specialsMap, Global.specialsFile);
        loadMap(actionsMap, Global.actionsFile);

//        punctuation.add("$");
        punctuation.add("''");
        punctuation.add(",");
        punctuation.add(".");
        punctuation.add(":");
        punctuation.add("``");
        punctuation.add("-NONE-");
    }

    private static void loadMapAndArray(HashMap<String, Integer> map, String[] array, String fileName) {
        try {
            // load data
            BufferedReader bfr1 = new BufferedReader(new InputStreamReader(new FileInputStream(fileName), "utf-8"));
            String data1 = "";
            while ((data1 = bfr1.readLine()) != null) { // init labels map
                String[] datas = data1.split(" ");
                map.put(datas[0], Integer.parseInt(datas[1]));
            }

            for (String k : map.keySet()) {
                int v = map.get(k);
                array[v] = k;
            }
            bfr1.close();
        } catch (IOException ex) {
            Logger.getLogger(Labels.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static void loadMap(HashMap<Integer, String> map, String fileName) {
        try {
            BufferedReader bfr = new BufferedReader(new InputStreamReader(new FileInputStream(fileName), "utf-8"));
            String data = "";
            while ((data = bfr.readLine()) != null) {
                String[] datas = data.split(" ");
                map.put(Integer.parseInt(datas[1]), datas[0]);
            }
            bfr.close();
        } catch (IOException ex) {
            Logger.getLogger(Labels.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    //</editor-fold>
    /*-----------------------------------------------------------------------------------------------------------------------------------------------------------*/

    //<editor-fold defaultstate="collapsed" desc="function for checking type of constituent tag">
    public static boolean isConstituent(int label) {
        return consMap.containsKey(label);
    }

    public static boolean isIncomplete(int label) {
        return incompleteCons.containsKey(label);
    }

    public static boolean isComplete(int label) {
        return completeCons.containsKey(label);
    }

    public static boolean isPreTerminal(int label) {
        return preTerminalMap.containsKey(label);
    }

    public static boolean isSpecial(int label) {
        return specialsMap.containsKey(label);
    }

    public static boolean isPoS(int label) {
        return posMap.containsKey(label);
    }
    //</editor-fold>
    /*-----------------------------------------------------------------------------------------------------------------------------------------------------------*/

    //<editor-fold defaultstate="collapsed" desc="functions for converting value (string <-> int)">
    public static int wordIntValue(String word) {
        return wordsMap.get(word);
    }

    public static String wordStrValue(int wordValue) {
        return wordsArray[wordValue];
    }

    public static int byteValue(String label) {
        return labelsMap.get(label);
    }

    public static String strValue(int value) {
        if (value > labelsArray.length) {
            System.out.println("constituent not found :" + value);
        }
        return labelsArray[value];
    }

    public static String actionInStr(int action) {
        return actionsMap.get(action);
    }

    public static String featTypeInStr(int featType) {
        return featTypes[featType];
    }

    public static String wordInStr(int word) {
        return wordsArray[word];
    }
    //</editor-fold>

    public static void main(String[] args) {
        for (int i = 0; i < Labels.labelsArray.length; i++) {
            String l = Labels.labelsArray[i];
            System.out.println(l);
        }
    }
}
