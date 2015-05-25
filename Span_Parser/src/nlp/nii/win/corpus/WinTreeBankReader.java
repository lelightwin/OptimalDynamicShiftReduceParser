/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nlp.nii.win.corpus;

import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import nlp.nii.win.ConstantResource.Global;
import nlp.nii.win.corpus.stanford.syntax.Tree;
import nlp.nii.win.corpus.stanford.syntax.Trees;
import nlp.nii.win.util.CustomizeHashMap;
import nlp.nii.win.ConstantResource.Labels;

/**
 *
 * @author lelightwin
 */
public class WinTreeBankReader {

    /*-------------------------------------------------------------------------------*/
    /* for converting */
    /*-------------------------------------------------------------------------------*/
    /* for reading tree instance*/
    /**
     *
     * @param i
     * @param n
     * @return list of trees from section i
     */
    public ArrayList<Tree<String>> readSection(int i, int n) {
        ArrayList<Tree<String>> trees = readTreesFrom(Global.wsjDirectory + "PennWSJ_section" + i + ".MRG", n);
        System.out.println("section " + i + " done!!");
        return trees;
    }

    /**
     *
     * @param fileName
     * @param n
     * @return list of trees from file
     */
    public ArrayList<Tree<String>> readTreesFrom(String fileName, int n) {
        ArrayList<Tree<String>> trees = new ArrayList();

        try {
            Trees.PennTreeReader reader = new Trees.PennTreeReader(new InputStreamReader(new FileInputStream(fileName)));
            for (int i = 0; i < n; i++) {
                if (reader.hasNext()) {
                    // preprocess: remove unary chains, redundant unaries and some modification
                    Tree<String> tree = reader.next().getChild(0);
                    List<Tree<String>> words = tree.getPreTerminals();

                    headFinding(tree);
                    tree.removeDuplicateUnary();
                    tree.removeUnaryChains();
                    Tree.resetPivot();
                    tree.extractHeadIdx();
                    tree.extractSpanInfo();
                    extractIntValue(tree);
                    trees.add(tree);
                } else {
                    return trees;
                }
            }

        } catch (FileNotFoundException ex) {
            Logger.getLogger(WinTreeBankReader.class.getName()).log(Level.SEVERE, null, ex);
        }
        return trees;
    }

    /**
     * extract the integer value of labels
     *
     * @param tree
     */
    private void extractIntValue(Tree<String> tree) {
        if (tree.getLabel() != null) {
            if (!tree.isLeaf()) {
                tree.setNumberLabel(Labels.byteValue(tree.getLabel()));
            } else {
                tree.setNumberLabel(Labels.wordIntValue(tree.getLabel()));
            }
        }
        for (Tree<String> c : tree.getChildren()) {
            extractIntValue(c);
        }
    }

    /**
     * extract head token
     *
     * @param tree
     */
    private void headFinding(Tree<String> tree) {
        if (tree.getLabel().contains("-H")) {
            tree.setLabel(tree.getLabel().replaceAll("-H", ""));
            tree.setHeadToken(true);
        }
        for (Tree<String> c : tree.getChildren()) {
            headFinding(c);
        }
    }

    /*----------------------------------------------------------------------------------------*/
    /**
     * save list of rules to file
     */
    public void extractRules() {
        try {
            CustomizeHashMap<String> ruleMap = new CustomizeHashMap<>();

            BufferedWriter bfw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(Global.grammarRulesFile), "utf-8"));
            for (int i = 0; i < 25; i++) {
                for (Tree<String> t : readSection(i, 5000)) {
                    for (String rule : rulesFrom(t)) {
                        ruleMap.updateWithOffset(rule, 1);
                    }
                }
            }

            for (String key : ruleMap.keySet()) { // save rule to file
                bfw.write(key + " -> " + ruleMap.get(key));
                bfw.newLine();
            }

            bfw.close();
        } catch (IOException ex) {
            Logger.getLogger(WinTreeBankReader.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     *
     * @param t
     * @return list of rules (String) from @t
     */
    private ArrayList<String> rulesFrom(Tree<String> t) {
        ArrayList<String> result = new ArrayList<>();
        if (!t.isPreTerminal()) {
            String r = t.getLabel();
            int head = -1;
            for (int i = 0; i < t.getChildren().size(); i++) { // process through all the children of @t
                Tree<String> c = t.getChild(i);
                result.addAll(rulesFrom(c)); // add recursively all the rules from @t's children 

                if (c.isHeadToken()) {
                    head = i;
                }
                r = r.concat(" ").concat(c.getLabel()); // create the rule of @t
            }
            r = r.concat(" -> " + head); // adding the head idx of @t
            result.add(r); // add the created rule into result
        }
        return result;
    }

    public static void main(String[] args) {
        WinTreeBankReader reader = new WinTreeBankReader();
//        for (int i = 0; i <= 24; i++) {
        Tree<String> tree = reader.readSection(0, 500).get(67);
        tree.display(0);
//        }

//        reader.extractRules();
    }
}
