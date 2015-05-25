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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author lelightwin
 */
public class DataInfo {

    /*-----------------------------------------------------------------------------------------------------------------------------------------------------------*/
    //<editor-fold defaultstate="collapsed" desc="constant resources">
    private final Map<Integer, String> consMap = new HashMap<>();
    private final Map<Integer, String> incompleteCons = new HashMap<>();

    private final Map<Integer, String> preTerminalMap = new HashMap<>();
    private final Map<Integer, String> actionsMap = new HashMap<>();

    private final Map<String, Integer> labelsMap = new HashMap<>();
    private final Map<String, Integer> wordsMap = new HashMap<>();
    private final List<String> labelsArray = new ArrayList<>();
    private final List<String> wordsArray = new ArrayList<>();
    private final Set<String> punctuation = new HashSet<>();
    private static DataInfo instance = null;

    //</editor-fold>
    /*-----------------------------------------------------------------------------------------------------------------------------------------------------------*/
    //<editor-fold defaultstate="collapsed" desc="init block">
    public static DataInfo instance() {
        if (instance == null) {
            instance = new DataInfo();
        }
        return instance;
    }

    public DataInfo() {
        loadMapAndArray(wordsMap, wordsArray, Global.wordsFile);
        loadMapAndArray(labelsMap, labelsArray, Global.labelsFile);

        loadMap(consMap, Global.constituentsFile);
        loadMap(incompleteCons, Global.incompleteConstituentsFile);
        loadMap(preTerminalMap, Global.preTerminalFile);
        loadMap(actionsMap, Global.actionsFile);

        punctuation.add("''");
        punctuation.add(",");
        punctuation.add(".");
        punctuation.add(":");
        punctuation.add("``");
        punctuation.add("-NONE-");
    }

    private void loadMapAndArray(Map<String, Integer> map, List<String> array, String fileName) {
        try {
            // load data
            BufferedReader bfr1 = new BufferedReader(new InputStreamReader(new FileInputStream(fileName), "utf-8"));
            String data1 = "";
            while ((data1 = bfr1.readLine()) != null) { // init labels map
                String[] datas = data1.split(" ");
                map.put(datas[0], Integer.parseInt(datas[1]));
            }

            String[] a1 = new String[map.size()];
            for (String k : map.keySet()) {
                int v = map.get(k);
                a1[v] = k;
            }
            for (String str : a1) {
                array.add(str);
            }
            bfr1.close();
        } catch (IOException ex) {
        }
    }

    private void loadMap(Map<Integer, String> map, String fileName) {
        try {
            BufferedReader bfr = new BufferedReader(new InputStreamReader(new FileInputStream(fileName), "utf-8"));
            String data = "";
            while ((data = bfr.readLine()) != null) {
                String[] datas = data.split(" ");
                map.put(Integer.parseInt(datas[1]), datas[0]);
            }
            bfr.close();
        } catch (IOException ex) {
        }
    }
    //</editor-fold>
    /*-----------------------------------------------------------------------------------------------------------------------------------------------------------*/

    //<editor-fold defaultstate="collapsed" desc="function for checking type of constituent tag">
    public boolean isConstituent(int label) {
        return consMap.containsKey(label);
    }

    public boolean isIncomplete(int label) {
        return incompleteCons.containsKey(label);
    }

    public boolean isPreTerminal(int label) {
        return preTerminalMap.containsKey(label);
    }

    //</editor-fold>
    /*-----------------------------------------------------------------------------------------------------------------------------------------------------------*/
    //<editor-fold defaultstate="collapsed" desc="functions for converting value (string <-> int)">
    public int wordIntValue(String word) {
        Integer intValue = wordsMap.get(word);
        if (intValue != null){
            
        }
        return intValue;
    }

    public int labelIntValue(String label) {
        return labelsMap.get(label);
    }

    public String labelInStr(int value) {
        return labelsArray.get(value);
    }

    public String actionInStr(int action) {
        return actionsMap.get(action);
    }

    public String wordInStr(int word) {
        return wordsArray.get(word);
    }

    public Set<String> punctuation() {
        return punctuation;
    }
    //</editor-fold>

    public static void main(String[] args) {
    }
}
