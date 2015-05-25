/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nlp.nii.win.parser.element;

import java.util.Objects;
import nlp.nii.win.ConstantResource.Labels;
import nlp.nii.win.corpus.stanford.syntax.Tree;

/**
 *
 * @author lelightwin
 */
public class DPPoint {

    private int label; // integer value of label
    private int length; // length of span
    private Word first; // first word of span
    private Word last; // last word of span

    public DPPoint(int label, Word first, Word last) {
        this.label = label;
        this.first = first;
        this.last = last;
        this.length = last.i() - first.i();
    }

    public DPPoint(Word word) {
        this.label = word.t();
        this.first = word;
        this.last = word;
        this.length = last.i() - first.i();
    }

    public DPPoint(Tree<String> t, Sentence sentence) {
        this.label = t.getNumberLabel();
        this.first = sentence.word(t.getStart());
        this.last = sentence.word(t.getEnd());
        this.length = last.i() - first.i();
    }

    /**
     * @return the label
     */
    public int c() {
        return label;
    }

    /**
     *
     * @return index of first word
     */
    public int fi() {
        return first.i();
    }

    /**
     *
     * @return PoS of first word
     */
    public int ft() {
        return first.t();
    }

    /**
     *
     * @return value of first word
     */
    public int fw() {
        return first.w();
    }

    /**
     *
     * @return index of last word
     */
    public int li() {
        return last.i();
    }

    /**
     *
     * @return PoS of last word
     */
    public int lt() {
        return last.t();
    }

    /**
     *
     * @return value of last word
     */
    public int lw() {
        return last.w();
    }

    public Word f() {
        return first;
    }

    public Word l() {
        return last;
    }

    public int length() {
        return length;
    }

    @Override
    public String toString() {
        return Labels.strValue(label) + "[" + first.i() + "," + last.i() + "]";
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        DPPoint other = (DPPoint) obj;
        if (this.label != other.label) {
            return false;
        }
        if (this.first.i() != other.first.i()) {
            return false;
        }
        return this.last.i() == other.last.i();
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 89 * hash + this.label;
        hash = 89 * hash + Objects.hashCode(this.first);
        hash = 89 * hash + Objects.hashCode(this.last);
        return hash;
    }

    
}
