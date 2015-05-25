/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nlp.nii.win.parser;

import java.util.ArrayList;
import java.util.List;
import nlp.nii.win.ConstantResource.Global;
import nlp.nii.win.parser.element.DPPoint;
import nlp.nii.win.parser.element.DPState;
import nlp.nii.win.parser.element.Rule;
import nlp.nii.win.parser.element.Word;
import static nlp.nii.win.parser.RuleManager.*;
import static nlp.nii.win.parser.element.Conditioner.*;

/**
 *
 * @author lelightwin
 */
public class SRActionsManager {

    //<editor-fold defaultstate="collapsed" desc="fundamental methods for shift-reduce actions">
    /**
     *
     * @param s current state
     * @return state after taking shift action from this state
     */
    private static DPState doShift(DPState s) {
        if (canShift(s)) { // check if shift action could be performed
            Word newWord = s.n0();
            DPState newState = new DPState(s.sentence(), s.queueSize() - 1); // create new state with same input sentence and substract the queueSize by 1
            newState.setS0(new DPPoint(newWord));
            newState.setS1(s.s0());
            newState.setAction(Global.SHIFT);
            return newState;
        }
        return null;
    }

    /**
     *
     * @param left left state of current state
     * @param right current state
     * @return list of states after taking binary reduce action between two
     * states
     */
    private static ArrayList<DPState> doBReduce(DPState left, DPState right) {
        ArrayList<DPState> newStates = new ArrayList<>(); // list of newly formed states

        if (canBReduce(left, right)) { // check if b-reduce action could be performed
            DPPoint ltop = left.s0(); // left child of b-reduce action
            DPPoint rtop = right.s0(); // right child of b-reduce action

            for (Rule<Integer> rule : getBinary(ltop.c(), rtop.c())) { // process list of rules which have lhs = l and r
                if (canBReduceWithRule(left, right, rule)) { // check if b-reduce action could be performed with this resulting node (indicated by rule)
                    int c = rule.getCons();
                    DPPoint newPoint; // create newly formed point
                    DPState newState = new DPState(right.sentence(), right.queueSize()); // create newly formed state

                    newPoint = new DPPoint(c, ltop.f(), rtop.l());

                    newState.setS0(newPoint);
                    newState.setS1(left.s1()); // this.s2
                    newState.setAction(rule.getAction());
                    newStates.add(newState);
                }
            }
        }
        return newStates;
    }

    /**
     *
     * @param s current state
     * @return list of states after taking unary reduce action from this state
     */
    private static ArrayList<DPState> doUReduce(DPState s) {
        ArrayList<DPState> newStates = new ArrayList<>();

        if (canUReduce(s)) { // check if u-reduce action could be performed
            DPPoint top = s.s0(); // unary child of u-reduce action
            for (Rule<Integer> rule : getUnary(top.c())) { // process list of rules which have lhs = u
                DPPoint newPoint = new DPPoint(rule.getCons(), top.f(), top.l());
                DPState newState = new DPState(s.sentence(), s.queueSize());
                newState.setS0(newPoint);
                newState.setS1(s.s1());
                newState.setAction(rule.getAction());
                newStates.add(newState);
            }
        }
        return newStates;
    }

    /**
     *
     * @param s current state
     * @return return finish state from this state
     */
    private static DPState doFinish(DPState s) {
        if (canFinish(s)) { // check if finish action could be performed
            DPState newState = new DPState(s.sentence(), 0);
            newState.setAction(Global.FINISH);
            newState.setFinish(true);
            return newState;
        }
        return null;
    }

    //</editor-fold>
    /*-----------------------------------------------------------------------------------------------------------------------------------------------------*/
    //<editor-fold defaultstate="collapsed" desc="functions for using fundamental shift-reduce actions">
    /**
     * add state shifted from a source state to a certain list of states
     *
     * @param states
     * @param shiftState
     * @param source
     * @param actionGold
     */
    private static void addShiftState(List<DPState> states, DPState shiftState, DPState source, int actionGold) {
        float shCost = PerceptronInteractor.score(shiftState.getAction(), source);
        source.setShiftCost(shCost);
        shiftState.setCost(source.cost() + shCost + PerceptronInteractor.offset);
        shiftState.leftStates().add(source);
        shiftState.setGold(source.isGold() && (actionGold == shiftState.getAction()));
        shiftState.setType(DPState.SHIFT);
        shiftState.takeStep(source.step() + 1);
        states.add(shiftState);
    }

    /**
     *
     * @param s
     * @param actionGold is the correct action (only use for training)
     * @return list of states after taking shift/unary actions from s
     */
    public static List<DPState> takeShiftFrom(DPState s, int actionGold) {
        List<DPState> newStates = new ArrayList();
        DPState shiftState = doShift(s);
        if (shiftState != null) {
            //<editor-fold defaultstate="collapsed" desc="this is for regular shift action">
            addShiftState(newStates, shiftState, s, actionGold);
            //</editor-fold>

            //<editor-fold defaultstate="collapsed" desc="this is for shift merging with unary action">
            for (DPState shUState : doUReduce(shiftState)) {
                addShiftState(newStates, shUState, s, actionGold);
                shUState.setUnary(shiftState.s0c());
            }
            //</editor-fold>
        }
        return newStates;
    }

    /**
     * add state reduced from two states to a certain list of states
     *
     * @param states
     * @param left
     * @param right
     * @param previousTotalCost
     * @param previousInsideCost
     * @param actionGold
     */
    private static void addBReduceState(List<DPState> states,
            DPState left, DPState right, DPState binState,
            float previousInsideCost, float previousTotalCost,
            int actionGold) {
        float binCost = PerceptronInteractor.score(binState.getAction(), right);
        binState.setInsideCost(previousInsideCost + binCost);
        binState.setCost(previousTotalCost + binCost + PerceptronInteractor.offset);
        binState.setGold(left.isGold() && right.isGold() && (actionGold == binState.getAction()));
        binState.setLeftStates(left.leftStates());
        binState.backPointer().add(left);
        binState.backPointer().add(right);
        binState.setType(DPState.REDUCE);
        binState.takeStep(right.step() + 1);
        states.add(binState);
    }

    /**
     *
     * @param q
     * @param actionGold is the correct action (only use for training)
     * @return list of states after taking binary/unary actions
     */
    public static List<DPState> takeBReduceFrom(DPState q, int actionGold) {
        ArrayList<DPState> newStates = new ArrayList();
        for (DPState p : q.leftStates()) {
            float c1 = p.shiftCost() + q.insideCost();
            float c2 = p.insideCost() + c1; // sum of inside cost of p and q
            float c3 = p.cost() + c1; // sum of total cost of p and q 
            for (DPState binState : doBReduce(p, q)) {
                //<editor-fold defaultstate="collapsed" desc="this is for regular binary action">
                addBReduceState(newStates, p, q, binState, c2, c3, actionGold);
                //</editor-fold>

                //<editor-fold defaultstate="collapsed" desc="this is for binary/unary action">
                for (DPState binUState : doUReduce(binState)) {
                    addBReduceState(newStates, p, q, binUState, c2, c3, actionGold);
                    binUState.setUnary(binState.s0c());
                }
                //</editor-fold>
            }
        }
        return newStates;
    }

    /**
     *
     * @param s
     * @return list of states after taking binary/unary actions
     */
    public static DPState takeFinishFrom(DPState s) {
        DPState finState = doFinish(s);
        if (finState != null) {
            float finCost = PerceptronInteractor.score(finState.getAction(), s);
            finState.setCost(s.cost() + finCost);
            finState.backPointer().add(s);
            finState.leftStates().addAll(s.leftStates());
            finState.setGold(s.isGold());
            finState.takeStep(s.step() + 1);
        }
        return finState;
    }
    //</editor-fold>
}
