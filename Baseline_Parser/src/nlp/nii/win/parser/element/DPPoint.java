/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nlp.nii.win.parser.element;

import nlp.nii.win.corpus.stanford.syntax.Tree;
import nlp.nii.win.parser.RuleManager;

/**
 *
 * @author lelightwin
 */
public class DPPoint {

    private int label = -1; // integer value of label
    private int head = -1;
    private int first = -1;
    private int last = -1;
    private int rule = -1;

    public DPPoint(int label, int head, int first, int last) {
        this.label = label;
        this.head = head;
        this.first = first;
        this.last = last;
    }

    public DPPoint(Word word) {
        this.label = word.t();
        this.head = word.i();
        this.first = word.i();
        this.last = word.i();
    }

    public DPPoint(Tree<String> t, Sentence sentence) {
        this.label = t.getNumberLabel();
        this.head = t.getHeadIdx();
        this.first = t.getStartIdx();
        this.last = t.getEndIdx();
    }

    /**
     * @return the label
     */
    public int c() {
        return label;
    }

    /**
     * @return the headIdx
     */
    public int h() {
        return head;
    }

    public int f() {
        return first;
    }

    public int l() {
        return last;
    }
}
