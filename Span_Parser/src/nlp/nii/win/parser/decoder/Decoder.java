/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nlp.nii.win.parser.decoder;

import java.util.ArrayList;
import java.util.List;
import nlp.nii.win.parser.element.DPState;
import nlp.nii.win.parser.element.Oracle;
import nlp.nii.win.util.OrderStatistic;

/**
 *
 * @author lelightwin
 */
public abstract class Decoder {

    protected DPState predict = null;
    protected boolean predictIsRight = true;
    protected int stateNums;

    public static final OrderStatistic<DPState> orderer = new OrderStatistic<>();

    public abstract void trainDecoding(Oracle oracle);

    public abstract void performDecoding(DPState state);

    public DPState getPredict() {
        return predict;
    }

    /**
     *
     * @param states
     * @return the if of state with maximum score in states
     */
    protected int maxId(List<DPState> states) {
        int max = 0;
        for (int i = 1; i < states.size(); i++) {
            if (states.get(i).compareTo(states.get(max)) > 0) {
                max = i;
            }
        }
        return max;
    }

    /**
     *
     * @param states
     * @return the state with maximum score in states
     */
    protected DPState max(List<DPState> states) {
        int max = 0;
        for (int i = 1; i < states.size(); i++) {
            if (states.get(i).compareTo(states.get(max)) > 0) {
                max = i;
            }
        }
        return states.get(max);
    }

    /**
     *
     * @param states
     * @return the id of state with minimum score in states
     */
    protected int minId(List<DPState> states) {
        int min = 0;
        for (int i = 1; i < states.size(); i++) {
            if (states.get(i).compareTo(states.get(min)) < 0) {
                min = i;
            }
        }
        return min;
    }

    /**
     *
     * @param states
     * @return the state with minimum score in states
     */
    protected DPState min(List<DPState> states) {
        int min = 0;
        for (int i = 1; i < states.size(); i++) {
            if (states.get(i).compareTo(states.get(min)) < 0) {
                min = i;
            }
        }
        return states.get(min);
    }

    /**
     *
     * @param states
     * @return true if @states contain golden states
     */
    protected DPState goldExist(List<DPState> states) {
        for (DPState st : states) {
            if (st.isGold()) {
                return st;
            }
        }
        return null;
    }

    /**
     * @return the predictIsRight
     */
    public boolean isPredictIsRight() {
        return predictIsRight;
    }

    /**
     * 
     * @return 
     */
    public int statesNum() {
        return stateNums;
    }

}
