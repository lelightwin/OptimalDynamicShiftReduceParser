/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nlp.nii.win.corpus;

import java.io.BufferedWriter;
import java.io.File;
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
import nlp.nii.win.ConstantResource.DataInfo;

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
     * @return list of trees from section i
     */
    public List<Tree<String>> readSection(int i) {
        String index = "" + i;
        if (i < 10) {
            index = "0" + i;
        }

        List<Tree<String>> trees = new ArrayList();
        File[] files = new File(Global.wsjDirectory + "/" + index).listFiles();

        for (File file : files) {
            trees.addAll(readTreesFrom(file));
        }
        return trees;
    }

    public List<Tree<String>> readTreesFrom(File file) {
        List<Tree<String>> trees = new ArrayList();
        try {
            Trees.PennTreeReader reader = new Trees.PennTreeReader(new InputStreamReader(new FileInputStream(file)));
            while (reader.hasNext()) {
                Tree<String> tree = reader.next();
                removeFunctionTag(tree);
                tree.removeDuplicateUnary();
                trees.add(tree);
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(WinTreeBankReader.class.getName()).log(Level.SEVERE, null, ex);
        }
        return trees;
    }

    private void removeFunctionTag(Tree<String> tree) {
        if ((!tree.isPreTerminal()) && (!tree.isLeaf())) {
            String label = tree.getLabel();
            int pivot1 = label.indexOf("-");
            if (pivot1 != -1) {
                label = label.substring(0, pivot1);
            }
            int pivot2 = label.indexOf("=");
            if (pivot2 != -1) {
                label = label.substring(0, pivot2);
            }
            tree.setLabel(label);

            for (Tree<String> child : tree.getChildren()) {
                removeFunctionTag(child);
            }
        }
    }

    /* ---------------------------------------------------------------------------------------------*/
    /* for binarize treebank*/
    public List<Tree<String>> readBinarizeSection(int i, int n) {
        List<Tree<String>> trees = readBinarizeTreesFrom(Global.binarizeWSJDirectory + "PennWSJ_section" + i + ".MRG", n);
        System.out.println("section " + i + " done!!");
        return trees;
    }

    /**
     *
     * @param fileName
     * @param n
     * @return list of trees from file
     */
    public List<Tree<String>> readBinarizeTreesFrom(String fileName, int n) {
        List<Tree<String>> trees = new ArrayList();

        try {
            Trees.PennTreeReader reader = new Trees.PennTreeReader(new InputStreamReader(new FileInputStream(fileName)));
            for (int i = 0; i < n; i++) {
                if (reader.hasNext()) {
                    // preprocess: remove unary chains, redundant unaries and some modification
                    Tree<String> tree = reader.next().getChild(0);
                    List<Tree<String>> words = tree.getPreTerminals();
                    for (int j = 0; j < words.size(); j++) {
                        words.get(j).setHeadIdx(j);
                    }

                    headFinding(tree);
                    tree.extractHeadIdx();
                    tree.removeDuplicateUnary();
                    tree.removeUnaryChains();
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
                tree.setNumberLabel(DataInfo.instance().labelIntValue(tree.getLabel()));
            } else {
                tree.setNumberLabel(DataInfo.instance().wordIntValue(tree.getLabel()));
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
            tree.setLabel(tree.getLabel().replace("-H", ""));
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
                for (Tree<String> t : readBinarizeSection(i, 5000)) {
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

    }
}
