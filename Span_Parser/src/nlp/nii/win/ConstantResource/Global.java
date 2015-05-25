/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nlp.nii.win.ConstantResource;

/**
 *
 * @author lelightwin
 */
public class Global {
    /*---------------------------------------------------------------------------------------------------------------------------------------------------------*/

    //<editor-fold defaultstate="collapsed" desc="grammar files">
    public static final String wsjDirectory = System.getProperty("user.dir") + "/data/binarizedWSJ/";
    public static final String grammarRulesFile = System.getProperty("user.dir") + "/data/binarizeRules.gr";
    public static final String byteGrammarRulesFile = System.getProperty("user.dir") + "/data/byteRules.gr";
    //</editor-fold>

    /*---------------------------------------------------------------------------------------------------------------------------------------------------------*/
    //<editor-fold defaultstate="collapsed" desc="labels files">
    public static final String labelsFile = System.getProperty("user.dir") + "/data/LabelsMap.dat";
    public static final String wordsFile = System.getProperty("user.dir") + "/data/wordsMap.dat";
    public static final String posFile = System.getProperty("user.dir") + "/data/PoS.dat";
    public static final String specialsFile = System.getProperty("user.dir") + "/data/Specials.dat";
    public static final String constituentsFile = System.getProperty("user.dir") + "/data/consMap.dat";
    public static final String incompleteConstituentsFile = System.getProperty("user.dir") + "/data/incompleteConsMap.dat";
    public static final String completeConstituentsFile = System.getProperty("user.dir") + "/data/completeConsMap.dat";
    public static final String preTerminalFile = System.getProperty("user.dir") + "/data/preTerminalMap.dat";
    public static final String actionsFile = System.getProperty("user.dir") + "/data/actionsMap.dat";
    //</editor-fold>

    /*---------------------------------------------------------------------------------------------------------------------------------------------------------*/
    //<editor-fold defaultstate="collapsed" desc="model files">
    public static final String indexesMatrixFile = System.getProperty("user.dir") + "/data/Learning Parameter/matrix.dat";
    public static final String weightsFile = System.getProperty("user.dir") + "/data/Learning Parameter/weights.dat";
    //</editor-fold>

    public static final int numberOfIteration = 30;
    public static final int wordsNum = 43633;
    public static final int labelsNum = 97;
    public static final int actionsNum = 168;
    public static final int featTypeNum = 52;
    public static final int SHIFT = 166;
    public static final int FINISH = 167;
}
