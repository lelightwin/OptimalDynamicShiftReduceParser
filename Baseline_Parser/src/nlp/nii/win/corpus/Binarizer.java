/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nlp.nii.win.corpus;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import nlp.nii.win.corpus.stanford.syntax.Tree;

/**
 *
 * @author lelightwin
 */
public class Binarizer {

    public static void removeConstituent(Tree<String> t) {
        if (!t.isPreTerminal() && !t.isLeaf()) { // t is a constituent node
            t.setLabel("W");
        }

        for (Tree<String> c : t.getChildren()) {
            removeConstituent(c);
        }
    }

    public static void binarizing(Tree<String> t) {
        List<Tree<String>> children = t.getChildren();
        List<Tree<String>> childrenToModify = new ArrayList(children);

        if (childrenToModify.size() > 2) {
            boolean left = true;
            Tree<String> side = childrenToModify.get(0); // first element
            if (side.getLabel().endsWith("-H")) { // if first element is head
                side = childrenToModify.get(childrenToModify.size() - 1); // last element
                left = false;
            }

            childrenToModify.remove(side); // remove side from children

            String label = t.getLabel();

            if (!label.endsWith("*-H")) {
                if (label.endsWith("-H")) {
                    label = label.replace("-H", "*-H");
                } else {
                    label = label.concat("*-H");
                }
            }

            Tree<String> iNode = new Tree(label, childrenToModify);//create intermediate node
            List<Tree<String>> newChildren = new ArrayList();
            if (left) {
                newChildren.add(side);
                newChildren.add(iNode);
            } else {
                newChildren.add(iNode);
                newChildren.add(side);
            }
            t.setChildren(newChildren);
            binarizing(iNode);
        }
        for (Tree<String> c : children) {
            binarizing(c);
        }
    }

    public static void debinarizing(Tree<String> t) {
        String label = t.getLabel();
        List<Tree<String>> newChildren = realchildrenFrom(t);
        t.setChildren(newChildren);
        for (Tree<String> nc : newChildren) {
            debinarizing(nc);
        }
    }

    private static List<Tree<String>> realchildrenFrom(Tree<String> t) {
        List<Tree<String>> realChildren = new ArrayList();
        for (Tree<String> c : t.getChildren()) {
            if (c.isLeaf() || c.isPreTerminal()) {
                realChildren.add(c);
            } else {
                if (!c.getLabel().endsWith("*")) {
                    realChildren.add(c);
                } else {
                    realChildren.addAll(realchildrenFrom(c));
                }
            }
        }
        return realChildren;
    }

    public static void main(String[] args) throws IOException {
        WinTreeBankReader reader = new WinTreeBankReader();

        int count1 = 0;
        for (int i = 0; i < 25; i++) {
            for (Tree<String> tree : reader.readSection(i)) {
                if (tree.getChild(0).hasUnaryChain()) {
                    System.out.println(tree);
                    tree.getChild(0).mergeUnaryChains();
                    System.out.println(tree);
                    System.out.println("");
                    count1++;
                }
            }
        }

        System.out.println(count1);
    }
}
