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
 * This is dynamic programming beam search decoder using early update strategy
 *
 * @author lelightwin
 */
public class DPBSDecoder extends Decoder {

    private int beam;
    private boolean earlyUpdate;

    public DPBSDecoder(int beam, boolean earlyUpdate) {
        this.beam = beam;
        this.earlyUpdate = earlyUpdate;
    }

    @Override
    public void trainDecoding(Oracle oracle) {
        Agenda agenda = new Agenda();
        Agenda temps = new Agenda();
        List<DPState> beamStates = new Agenda();
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

            temps.clear(); // temps arr for storing all the created states in step
            int actionGold = oracle.getNextAction(predict.step());
            for (DPState state : beamStates) { // perform the shift-reduce actions from all the states in beam
                temps.addAll(takeShiftFrom(state, actionGold));
                temps.addAll(takeBReduceFrom(state, actionGold));
                temps.addExceptNull(takeFinishFrom(state));
            }

            // after then, choose the new beam-best states based on their cost
            beamStates.clear();
            agenda.clear(); // agenda for sorting and merging all the states from temps
            Collections.sort(temps); // sorting the temps arr
            agenda.addOrMerge(temps);
            beamStates = agenda.prune(beam);

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
        Agenda temps = new Agenda();
        List<DPState> beamStates = new ArrayList<>();
        predict = state;
        beamStates.add(state); // init beamStates with initial state of oracle

        while (!predict.isFinish()) {
            temps.clear();
            for (DPState s : beamStates) { // perform the shift-reduce actions from all the states in beam
                temps.addAll(takeShiftFrom(s, -1));
                temps.addAll(takeBReduceFrom(s, -1));
                temps.addExceptNull(takeFinishFrom(s));
            }

            // after then, choose the new beam-best states based on their cost
            beamStates.clear();
            agenda.clear(); // agenda such as in train decoding

            Collections.sort(temps);
            agenda.addOrMerge(temps);
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
        DPBSDecoder decoder = new DPBSDecoder(16, false);
        ArrayList<Oracle> oracles = new ArrayList<>();
        for (int i = 0; i < trees.size(); i++) {
            oracles.add(new Oracle(trees.get(i)));
        }
        System.out.println("done loading train corpus");
        long start = System.currentTimeMillis();
        for (int i = 0; i < oracles.size(); i++) {
            Oracle oracle = oracles.get(i);
            decoder.trainDecoding(oracle);
        }
        long end = System.currentTimeMillis();
        System.out.println("speed: " + oracles.size() / ((end - start) / 1000f));
    }
}
