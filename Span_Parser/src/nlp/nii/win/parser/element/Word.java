/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nlp.nii.win.parser.element;

import nlp.nii.win.ConstantResource.Labels;
import nlp.nii.win.corpus.stanford.syntax.Tree;

/**
 *
 * @author lelightwin
 */
public class Word {

    private int pos;
    private int value;
    private int index;
    private Tree<String> tree;

    public Word(Tree<String> w) {
        this.pos = w.getNumberLabel();
        this.value = w.getChild(0).getNumberLabel();
        this.tree = w;
    }

    /**
     * @return the pos
     */
    public int t() {
        return pos;
    }

    /**
     * @return the value
     */
    public int w() {
        return value;
    }

    /**
     * @return the tree
     */
    public Tree<String> getTree() {
        return tree;
    }

    @Override
    public String toString() {
        return Labels.strValue(this.pos) + "/" + this.index;
    }

    /**
     * @return the index
     */
    public int i() {
        return index;
    }

    /**
     * @param index the index to set
     */
    public void setIndex(int index) {
        this.index = index;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 73 * hash + this.index;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Word other = (Word) obj;
        if (this.index != other.index) {
            return false;
        }
        return true;
    }

}