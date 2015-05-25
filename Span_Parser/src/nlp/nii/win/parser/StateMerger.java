/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nlp.nii.win.parser;

import nlp.nii.win.parser.element.DPState;

/**
 *
 * @author lelightwin
 */
public class StateMerger {

    public static void directedMerge(DPState s1, DPState s2) {
        if (s1.type() == DPState.SHIFT) { // this is shift state
            s1.setGold(s1.isGold() || s2.isGold());
            s1.leftStates().addAll(s2.leftStates());
        } else if (s1.type() == DPState.REDUCE) { // this is reduce state
            // do nothing because they has the same left states
//            List<DPState> left1 = s1.leftStates();
//            List<DPState> left2 = s2.leftStates();
//            for (int i = 0; i < left1.size(); i++) {
//                DPState l1 = left1.get(i);
//                System.out.print(l1+"\t");
//            }
//            System.out.println("");
//            for (int i = 0; i < left2.size(); i++) {
//                DPState l2 = left2.get(i);
//                System.out.print(l2+"\t");
//            }
//            System.out.println("");
        }
    }

    public static void undirectedMerge(DPState s1, DPState s2) {
        if (s1.compareTo(s2) >= 0) {
            directedMerge(s1, s2);
        } else {
            directedMerge(s2, s1);
        }
    }
}
