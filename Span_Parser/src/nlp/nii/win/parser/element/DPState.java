/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nlp.nii.win.parser.element;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import nlp.nii.win.corpus.stanford.syntax.Tree;
import nlp.nii.win.util.CustomizeStack;
import nlp.nii.win.ConstantResource.Labels;

/**
 *
 * @author lelightwin
 */
public class DPState implements Comparable<DPState> {

    public static final int SHIFT = 1;
    public static final int REDUCE = 0;

    private ArrayList<DPState> backPointer = new ArrayList<>(); // back pointer for recreating the output tree

    private int unary = -1; // for storing unary label

    private ArrayList<DPState> leftStates = new ArrayList<>(); // left states of this state

    private boolean gold = false; // true if this state is a golden state

    private boolean finish = false; // true if this state is a finish state

    private int action = -1; // the previous action to this state

    private float cost = 0.0f; // real cost

    private float insideCost = 0.0f; // inside cost (within span)

    private float shiftCost = 0.0f; // cost for shift into another state

    private int queueSize = -1; // current size of queue

    private int type = -1;

    private int step = 0;

    private Sentence sentence;

    /*-----------------------------------------------------------------------------------------------------------------------------------------------------*/
    //<editor-fold defaultstate="collapsed" desc="basic elements of a DPState">
    private DPPoint s0 = null;
    private DPPoint s1 = null;

    //</editor-fold>
    /*-----------------------------------------------------------------------------------------------------------------------------------------------------*/
    //<editor-fold defaultstate="collapsed" desc="constructor">
    public DPState(Sentence sentence, int queueSize) {
        this.sentence = sentence;
        this.queueSize = queueSize;
    }

    public DPState(Sentence sentence, CustomizeStack<Tree<String>> stack, int queueSize) {
        this.sentence = sentence;
        this.queueSize = queueSize;
        // create state from stack
        Tree<String> st0 = stack.peek(0);
        Tree<String> st1 = stack.peek(1);

        if (st0 != null) {
            this.s0 = new DPPoint(st0, this.sentence);

            if (st1 != null) {
                this.s1 = new DPPoint(st1, this.sentence);
            }
        }
    }
        //</editor-fold>
    /*-----------------------------------------------------------------------------------------------------------------------------------------------------*/

    //<editor-fold defaultstate="collapsed" desc="methods for basic elements">
    //<editor-fold defaultstate="collapsed" desc="set methods">
    public void setS0(DPPoint s0) {
        this.s0 = s0;
    }

    public void setS1(DPPoint s1) {
        this.s1 = s1;
    }

    public void setFinish(boolean finish) {
        this.finish = finish;
    }

    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="checking methods">
    public boolean hasS0() {
        return s0 != null;
    }

    public boolean hasS1() {
        return s1 != null;
    }

    public boolean hasN0() {
        return queueSize > 0;
    }

    public boolean hasN1() {
        return queueSize > 1;
    }

    public boolean hasN2() {
        return queueSize > 2;
    }

    public boolean hasN3() {
        return queueSize > 3;
    }

    public boolean qIsEmpty() {
        return queueSize == 0;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="return methods for basic elements">
    /**
     * @return the s0
     */
    public DPPoint s0() {
        return s0;
    }

    /**
     * @return the s1
     */
    public DPPoint s1() {
        return s1;
    }

    public Word n0() {
        return sentence.queueWord(0, queueSize);
    }

    public Word n1() {
        return sentence.queueWord(1, queueSize);
    }

    public Word n2() {
        return sentence.queueWord(2, queueSize);
    }

    public Word n3() {
        return sentence.queueWord(3, queueSize);
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="return methods for basic features">
    public int s0c() {
        if (this.hasS0()) {
            return s0.c();
        }
        return -1;
    }

    public int s0ft() {
        if (this.hasS0()) {
            return s0.ft();
        }
        return -1;
    }

    public int s0fw() {
        if (this.hasS0()) {
            return s0.fw();
        }
        return -1;
    }

    public int s0lt() {
        if (this.hasS0()) {
            return s0.lt();
        }
        return -1;
    }

    public int s0lw() {
        if (this.hasS0()) {
            return s0.lw();
        }
        return -1;
    }

    public int s0len() {
        if (this.hasS0()) {
            return s0.length();
        }
        return -1;
    }

    public int s1c() {
        if (this.hasS1()) {
            return s1.c();
        }
        return -1;
    }

    public int s1ft() {
        if (this.hasS1()) {
            return s1.ft();
        }
        return -1;
    }

    public int s1fw() {
        if (this.hasS1()) {
            return s1.fw();
        }
        return -1;
    }

    public int s1lt() {
        if (this.hasS1()) {
            return s1.lt();
        }
        return -1;
    }

    public int s1lw() {
        if (this.hasS1()) {
            return s1.lw();
        }
        return -1;
    }

    public int s1len() {
        if (this.hasS1()) {
            return s1.length();
        }
        return -1;
    }

    public int N0t() {
        if (this.hasN0()) {
            return n0().t();
        }
        return -1;
    }

    public int N0w() {
        if (this.hasN0()) {
            return n0().w();
        }
        return -1;
    }

    public int N1t() {
        if (this.hasN1()) {
            return n1().t();
        }
        return -1;
    }

    public int N1w() {
        if (this.hasN1()) {
            return n1().w();
        }
        return -1;
    }

    public int N2t() {
        if (this.hasN2()) {
            return n2().t();
        }
        return -1;
    }

    public int N2w() {
        if (this.hasN2()) {
            return n2().w();
        }
        return -1;
    }

    public int N3t() {
        if (this.hasN3()) {
            return n3().t();
        }
        return -1;
    }

    public int N3w() {
        if (this.hasN3()) {
            return n3().w();
        }
        return -1;
    }

    //</editor-fold>
    //</editor-fold>
    /*-----------------------------------------------------------------------------------------------------------------------------------------------------*/
    //<editor-fold defaultstate="collapsed" desc="some output of DPState">
    /**
     *
     * @return the states which is inside the span of current s0
     */
    public List<DPState> insideStates() {
        List<DPState> states = new ArrayList();
        if (!this.backPointer().isEmpty()) {
            for (DPState child : this.backPointer()) {
                states.addAll(child.insideStates());
            }
        }
        states.add(this);
        return states;
    }

    /**
     *
     * @return all states from the start to current
     */
    public List<DPState> allStates() {
        List<DPState> states = new ArrayList();
        if (!this.leftStates().isEmpty()) {
            DPState topLeft = this.leftStates().get(0); // get the most promising left state
            states.addAll(topLeft.allStates());
        }
        states.addAll(this.insideStates());
        return states;
    }

    /**
     *
     * @return parse tree induced by current state
     */
    public Tree<String> makeTree() {
        if (this.isFinish()) {
            return this.makeTreeByTopNode();
        }
        Tree<String> root = new Tree("S");
        List<Tree<String>> localTrees = new ArrayList();
        DPState pivot = this; // start with current node

        while (!pivot.leftStates().isEmpty()) {
            // process all the nodes on stack and create the local trees and add it to root
            root.getChildren().add(0, pivot.makeTreeByTopNode());
            pivot = pivot.leftStates().get(0);
        }
        for (int i = 0; i < this.queueSize() - 1; i++) {
            // the rest of words in queue
            Tree<String> wordi = sentence.queueWord(i, this.queueSize()).getTree();
            root.getChildren().add(wordi);
        }
        return root;
    }

    /**
     *
     * @return parse tree induced by top node (s0) of current state
     */
    public Tree<String> makeTreeByTopNode() {
        Tree<String> t;
        if (this.isFinish()) {
            return backPointer.get(0).makeTreeByTopNode();
        }
        Tree<String> ut;
        if (backPointer.isEmpty()) { // this is a leaf state (PoS or unary -> PoS)
            if (this.unary() != -1) { // this is an unary state, create unary node first then add it to the children of current node
                t = new Tree(Labels.strValue(s0.c()));
                ut = sentence.word(s0.fi()).getTree();
                t.getChildren().add(ut);
            } else {
                t = sentence.word(s0.fi()).getTree();
            }
        } else { // this is not a leaf state (constituent)
            t = new Tree(Labels.strValue(s0.c()));

            List<Tree<String>> children = new ArrayList();
            for (DPState s : backPointer) {
                children.add(s.makeTreeByTopNode());
            }

            if (this.unary() != -1) { // this is an unary state, create unary node first then add it to the children of current node
                ut = new Tree(Labels.strValue(this.unary()));
                ut.setChildren(children);
                t.getChildren().add(ut);
            } else {
                t.setChildren(children);
            }
        }
        return t;
    }
    //</editor-fold>
    /*-----------------------------------------------------------------------------------------------------------------------------------------------------*/

    //<editor-fold defaultstate="collapsed" desc="common methods (getter, setter, toString, hashcode and equals)">
    @Override
    public String toString() {
        String result = "DPState{";
        if (s0 != null) {

            if (s1 != null) {
                result += ", s1=" + s1;
            }
            result += ", s0=" + s0;
        }
        return result + ", " + Labels.actionInStr(this.getAction()) + "}";
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final DPState other = (DPState) obj;
        if (!Objects.equals(this.s0, other.s0)) {
            return false;
        }
        return Objects.equals(this.s1, other.s1);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 23 * hash + Objects.hashCode(this.s0);
        hash = 23 * hash + Objects.hashCode(this.s1);
        return hash;
    }

    public int step() {
        return step;
    }

    public void takeStep(int step) {
        this.step = step;
    }

    /**
     * @param cost the cost to set
     */
    public void setCost(float cost) {
        this.cost = cost;
    }

    /**
     * @param insideCost the insideCost to set
     */
    public void setInsideCost(float insideCost) {
        this.insideCost = insideCost;
    }

    /**
     * @param shiftCost the shiftCost to set
     */
    public void setShiftCost(float shiftCost) {
        this.shiftCost = shiftCost;
    }

    public void setBackPointer(ArrayList<DPState> backPointer) {
        this.backPointer = backPointer;
    }

    public void setLeftStates(ArrayList<DPState> leftStates) {
        this.leftStates = leftStates;
    }

    /**
     * @return the shORbr
     */
    public int type() {
        return type;
    }

    /**
     * @param type the shORbr to set
     */
    public void setType(int type) {
        this.type = type;
    }

    /**
     * @return the backPointer
     */
    public ArrayList<DPState> backPointer() {
        return backPointer;
    }

    /**
     * @return the leftStates
     */
    public ArrayList<DPState> leftStates() {
        return leftStates;
    }

    /**
     * @return the gold
     */
    public boolean isGold() {
        return gold;
    }

    /**
     *
     * @param gold to set
     */
    public void setGold(boolean gold) {
        this.gold = gold;
    }

    /**
     * @return the action
     */
    public int getAction() {
        return action;
    }

    /**
     * @param action the action to set
     */
    public void setAction(int action) {
        this.action = action;
    }

    /**
     * @return the cost
     */
    public float cost() {
        return cost;
    }

    /**
     * @return the insideCost
     */
    public float insideCost() {
        return insideCost;
    }

    /**
     * @return the shiftCost
     */
    public float shiftCost() {
        return shiftCost;
    }

    /**
     * @return the queueSize
     */
    public int queueSize() {
        return queueSize;
    }

    public void setSentence(Sentence sentence) {
        this.sentence = sentence;
    }

    /**
     * @return the sentence
     */
    public Sentence sentence() {
        return sentence;
    }

    /**
     * @return the finish
     */
    public boolean isFinish() {
        return finish;
    }

    public void setUnary(int unary) {
        this.unary = unary;
    }

    public int unary() {
        return unary;
    }

    //</editor-fold>

    /*-----------------------------------------------------------------------------------------------------------------------------------------------------*/
    //<editor-fold defaultstate="collapsed" desc="comparable interface">
    private int compare(float value1, float value2) {
        if (value1 == value2) {
            return 0;
        }
        if (value1 > value2) {
            return 1;
        }
        return -1;
    }

    @Override
    public int compareTo(DPState o) {
        int c = compare(this.cost(), o.cost());
        if (c == 0) { // their cost is equal
            return compare(this.insideCost(), o.insideCost());
        } else {
            return c;
        }
    }

    //</editor-fold>
}
