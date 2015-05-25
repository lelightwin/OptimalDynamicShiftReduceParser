/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nlp.nii.win.parser;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import nlp.nii.win.ConstantResource.Global;
import nlp.nii.win.parser.element.Rule;
import nlp.nii.win.util.CustomizeHashMap;
import nlp.nii.win.ConstantResource.DataInfo;

/**
 *
 * @author lelightwin
 */
public class RuleManager {

    private static ArrayList<Rule<Integer>>[][] binaryMapper = new ArrayList[Global.labelsNum][Global.labelsNum];
    private static ArrayList<Rule<Integer>>[] unaryMapper = new ArrayList[Global.labelsNum];

    static {
        BufferedReader bfr = null;
        try {
            for (int i = 0; i < Global.labelsNum; i++) {
                for (int j = 0; j < Global.labelsNum; j++) {
                    binaryMapper[i][j] = new ArrayList<>();
                }
            }
            for (int i = 0; i < Global.labelsNum; i++) {
                unaryMapper[i] = new ArrayList<>();
            }
            bfr = new BufferedReader(new InputStreamReader(new FileInputStream(Global.byteGrammarRulesFile), "utf-8"));
            String data = "";
            int count = 0;
            while ((data = bfr.readLine()) != null) {
                String[] datas = data.split(" -> ");

                int action = Integer.parseInt(datas[0]); // action
                String[] labels = datas[1].split(" "); // list of labels
                int head = Integer.parseInt(datas[2]);
                int c = Integer.parseInt(labels[0]); // constituent tag

                if (labels.length == 3) { // binary rule
                    int i = Integer.parseInt(labels[1]); // left child
                    int j = Integer.parseInt(labels[2]); // right child

                    Rule r = new Rule(c, action, head); // rule with left head
                    r.setId(count);
                    binaryMapper[i][j].add(r);
                } else if (labels.length == 2) { //unary rule
                    int i = Integer.parseInt(labels[1]); // unary child

                    Rule u1 = new Rule(c, action, head);
                    u1.setId(count);
                    unaryMapper[i].add(u1);
                }
                count++;
            }
            bfr.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(RuleManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(RuleManager.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                bfr.close();
            } catch (IOException ex) {
                Logger.getLogger(RuleManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    /**
     *
     * @param left
     * @param right
     * @param parent
     * @return
     */
    public static Rule<Integer> getBinaryRule(int left, int right, int parent) {
        ArrayList<Rule<Integer>> rArr = getBinary(left, right);
        for (int i = 0; i < rArr.size(); i++) {
            Rule<Integer> r = rArr.get(i);
            if ((r.getCons() == parent)) {
                return r;
            }
        }
        return null;
    }

    /**
     *
     * @param unary
     * @param parent
     * @return
     */
    public static Rule<Integer> getUnaryRule(int unary, int parent) {
        ArrayList<Rule<Integer>> rArr = getUnary(unary);
        for (Rule<Integer> r : rArr) {
            if (parent == r.getCons()) {
                return r;
            }
        }
        return null;
    }

    /**
     *
     * @param left
     * @param right
     * @return list of binary rules with i and j is the right hand side
     */
    public static ArrayList<Rule<Integer>> getBinary(int left, int right) {
        return binaryMapper[left][right];
    }

    /**
     *
     * @param unary
     * @return list of unary rules with i is the right hand side
     */
    public static ArrayList<Rule<Integer>> getUnary(int unary) {
        return unaryMapper[unary];
    }

    /**
     * create grammar rules with integer value of constituent labels this
     * function also create the action which is relevant to a rule
     */
    public static void createIntGrammarRules() {
        BufferedReader bfr = null;
        try {
            ArrayList<String> byteRules = new ArrayList<>();
            CustomizeHashMap<String> actions = new CustomizeHashMap<>();

            bfr = new BufferedReader(new InputStreamReader(new FileInputStream(Global.grammarRulesFile), "utf-8"));
            String data = "";
            while ((data = bfr.readLine()) != null) {
                String[] datas = data.split(" -> ");
                String[] cons = datas[0].split(" "); // get the list of constituent
                int head = Integer.parseInt(datas[1]); // head of this rule

                String byteRule = "";
                for (String con : cons) {
                    byteRule = byteRule.concat(DataInfo.instance().labelIntValue(con) + " "); // add the current constituent label
                }

                // extract the action corresponding to the rule
                String action = "";
                if (cons.length == 3) {
                    if (head == 0) {
                        action = "B-l(" + cons[0] + ")";
                    } else {
                        action = "B-r(" + cons[0] + ")";
                    }
//                    action = "B(" + cons[0] + ")";
                } else if (cons.length == 2) {
                    if (DataInfo.instance().isConstituent(DataInfo.instance().labelIntValue(cons[1]))) {
                        if (head == 0) {
                            action = "B-l(" + cons[1] + ")/U(" + cons[0] + ")";
                        } else {
                            action = "B-r(" + cons[1] + ")/U(" + cons[0] + ")";
                        }
//                        action = "B("+cons[1]+")/U("+cons[0]+")";
                    } else if (DataInfo.instance().isPreTerminal(DataInfo.instance().labelIntValue(cons[1]))) {
                        action = "Sh/U(" + cons[0] + ")";
                    }
                }
                // convert the action to int value
                String idx = actions.updateIfNotExist(action) + " ";
                System.out.println(action + " " + data);
                byteRule = idx.concat("-> ").concat(byteRule.trim()).concat(" -> ").concat(datas[1]).concat(" -> ").concat(datas[2]);
                byteRules.add(byteRule);
            }
            bfr.close();

            BufferedWriter bfw1 = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(Global.byteGrammarRulesFile), "utf-8")); // save grammar rules with integer value
            for (String r : byteRules) {
                bfw1.write(r);
                bfw1.newLine();
            }
            bfw1.close();

            BufferedWriter bfw2 = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(Global.actionsFile), "utf-8")); // save actions map into file
            for (String action : actions.keySet()) {
                bfw2.write(action + " " + actions.get(action));
                bfw2.newLine();
            }
            bfw2.close();
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(RuleManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(RuleManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void main(String[] args) throws IOException {
//        ArrayList<Rule<Integer>> rs = Global.ruleManager.getUnary(Labels.byteValue("VP"));
//
//        for (Rule<Integer> r : rs) {
//            System.out.println(Labels.strValue(r.getCons()) + " " + r.getAction());
//        }

    }
}
