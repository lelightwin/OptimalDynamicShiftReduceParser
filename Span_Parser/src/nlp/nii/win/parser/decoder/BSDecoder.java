/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nlp.nii.win.parser.decoder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import nlp.nii.win.corpus.WinTreeBankReader;
import nlp.nii.win.corpus.stanford.syntax.Tree;
import nlp.nii.win.parser.Agenda;
import nlp.nii.win.parser.PerceptronInteractor;
import nlp.nii.win.parser.element.DPState;
import nlp.nii.win.parser.element.Oracle;
import static nlp.nii.win.parser.SRActionsManager.*;

/**
 * This is beam search decoder using early update strategy
 *
 * @author lelightwin
 */
public class BSDecoder extends Decoder {

    private int beam;
    private boolean earlyUpdate;

    public BSDecoder(int beam, boolean earlyUpdate) {
        this.beam = beam;
        this.earlyUpdate = earlyUpdate;
    }

    @Override
    public void trainDecoding(Oracle oracle) {
        Agenda agenda = new Agenda();
        List<DPState> beamStates = new ArrayList<>();
        DPState startState = oracle.start();
        predict = startState;

        startState.setGold(true);
        beamStates.add(startState); // init beamStates with initial state of oracle
        predictIsRight = true;

        while (!predict.isFinish()) {
            // perform early update
            if (earlyUpdate) {
                if (goldExist(beamStates) == null) {
                    return;
                }
            }
            agenda.clear(); // agenda for all the states in a step
            int actionGold = oracle.getNextAction(predict.step());
            for (DPState state : beamStates) { // perform the shift-reduce actions from all the states in beam
                agenda.addAll(takeShiftFrom(state, actionGold));
                agenda.addAll(takeBReduceFrom(state, actionGold));
                agenda.addExceptNull(takeFinishFrom(state));
            }

            // after then, choose the new beam-best states based on their cost
            beamStates.clear();
            Collections.sort(agenda);
            beamStates = agenda.prune(beam); // get @beam-best elements

            // for non-early update case
            if (!beamStates.isEmpty()) {
                predict = beamStates.get(0);
                predictIsRight = predict.isGold();
            } else {
                return;
            }
        }
    }

    
    @Override
    public void performDecoding(DPState state) {

        Agenda agenda = new Agenda();
        predict = state;
        List<DPState> beamStates = new ArrayList<>();
        stateNums = 0;

        beamStates.add(state); // init beamStates with initial state of oracle

        while (!predict.isFinish()) {
            agenda.clear();
            for (DPState s : beamStates) { // perform the shift-reduce actions from all the states in beam
                agenda.addAll(takeShiftFrom(s, -1));
                agenda.addAll(takeBReduceFrom(s, -1));
                agenda.addExceptNull(takeFinishFrom(s));
            }

            stateNums += beamStates.size();

            // after then, choose the new beam-best states based on their cost
            beamStates.clear();
            Collections.sort(agenda);
            beamStates = agenda.prune(beam);

            // for non-early update case
            if (!beamStates.isEmpty()) {
                predict = beamStates.get(0);
            } else {
                return;
            }
        }
    }

    /**
     * @param earlyUpdate the earlyUpdate to set
     */
    public void setEarlyUpdate(boolean earlyUpdate) {
        this.earlyUpdate = earlyUpdate;
    }

    public static void main(String[] args) {
        PerceptronInteractor.createLearner();
        WinTreeBankReader reader = new WinTreeBankReader();
        ArrayList<Tree<String>> trees = reader.readSection(0, 5000);
        BSDecoder decoder = new BSDecoder(16, false);
        ArrayList<Oracle> oracles = new ArrayList<>();
        for (int i = 0; i < trees.size(); i++) {
            oracles.add(new Oracle(trees.get(i)));
        }
        System.out.println("done loading train corpus");
        long start = System.currentTimeMillis();
        for (int i = 0; i < oracles.size(); i++) {
//            if (i%50 == 0) System.out.println("sentence: "+i);
            Oracle oracle = oracles.get(i);
            decoder.trainDecoding(oracle);
//            System.out.println("");
//            System.out.println(oracle.sentence());
//            decoder.predict.displayStatesPath();
//            System.out.println("");
        }
        long end = System.currentTimeMillis();
        System.out.println("speed: " + oracles.size() / ((end - start) / 1000f));
    }
}
