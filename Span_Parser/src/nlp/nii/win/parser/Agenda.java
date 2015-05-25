/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nlp.nii.win.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;
import nlp.nii.win.parser.element.DPState;

/**
 *
 * @author lelightwin
 */
public class Agenda extends ArrayList<DPState> {

    /**
     * this is function for dynamic programming
     *
     * @param state
     */
    public void addOrMerge(DPState state) {
        int idx = this.indexOf(state);
        if (idx != -1) {
            StateMerger.directedMerge(this.get(idx), state);
        } else {
            this.add(state);
        }
    }

    public void addOrMerge(List<DPState> states) {
        for (int i = 0; i < states.size(); i++) {
            DPState state = states.get(i);
            this.addOrMerge(state);
        }
    }

    public void addExceptNull(DPState state) {
        if (state != null) {
            this.add(state);
        }
    }

    public DPState[] toStateArray() {
        DPState[] stateArr = new DPState[this.size()];
        Object[] oArr = this.toArray();
        for (int i = 0; i < oArr.length; i++) {
            stateArr[i] = (DPState) oArr[i];
        }
        return stateArr;
    }

    public List<DPState> prune(int beam) {
        
        if (beam > size()) {
            return new ArrayList<>(this);
        } else {
            return new ArrayList<>(this.subList(0, beam));
        }
    }
    
    public static void main(String[] args) {
        SortedMap<Integer,Integer> set = new TreeMap();
        set.put(5,2);
        set.put(100,3);
        set.put(10,4);
        set.put(55,70);
        set.put(40,100);

        for (Entry<Integer, Integer> e: set.entrySet()){
            System.out.println(e.getKey()+" "+e.getValue());
        }
    }
}
