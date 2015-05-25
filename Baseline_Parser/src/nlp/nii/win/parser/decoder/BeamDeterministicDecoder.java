/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nlp.nii.win.parser.decoder;

import java.util.ArrayList;
import java.util.List;
import nlp.nii.win.corpus.WinTreeBankReader;
import nlp.nii.win.corpus.stanford.syntax.Tree;
import nlp.nii.win.parser.Agenda;
import nlp.nii.win.parser.ParserPerceptronCommunicator;
import nlp.nii.win.parser.element.DPState;
import nlp.nii.win.parser.element.Oracle;

/**
 *
 * @author lelightwin
 */
public class BeamDeterministicDecoder extends Decoder {

    private int beam;

    public BeamDeterministicDecoder(int beam) {
        this.beam = beam;
    }

    @Override
    public void trainDecoding(Oracle oracle) {
        Agenda agenda = new Agenda();
        predict = null;
        ArrayList<DPState> beamStates = new ArrayList<>();
        step = 0;
        DPState startState = oracle.start();
        startState.setGold(true);
        beamStates.add(startState); // init beamStates with initial state of oracle
        predictIsRight = true;

        while (step < oracle.finalStep()) {
            // perform early update
            if (goldExist(beamStates) == null) {
                predictIsRight = false;
                int maxId = maxId(beamStates);
                DPState candidate = beamStates.get(maxId); // get the most promise state in beam
                DPState gold = goldExist(agenda); // get the golden state
                ArrayList<DPState> predictChain = candidate.listStates();
                ArrayList<DPState> goldenChain = gold.listStates();
                ParserPerceptronCommunicator.update(goldenChain, predictChain); // update the perceptron parameter with them
                beamStates.set(maxId, gold); // add the golden state into beam states
            }
            
            agenda.clear();
            int actionGold = oracle.getNextAction(step);
            for (DPState state : beamStates) { // perform the shift-reduce actions from all the states in beam
                agenda.addAll(state.takeShift(actionGold));
                agenda.addAll(state.takeBReduce(actionGold));
                agenda.addExceptNull(state.takeFinish());
            }
            // after then, choose the new beam-best states based on their cost
            beamStates.clear();
            if (beam >= agenda.size()) {
                beamStates.addAll(agenda);
            } else {
                DPState[] stateArr = agenda.toStateArray();
                int pivot = ParserPerceptronCommunicator.orderer.kthLargest(stateArr, beam);
                for (int j = pivot; j < stateArr.length; j++) {
                    beamStates.add(stateArr[j]);
                }
            }

            step += 1;
        }
        predict = max(beamStates);
        predictIsRight = predictIsRight && predict.isGold();
    }

    public static void main(String[] args) {
        WinTreeBankReader reader = new WinTreeBankReader();
        List<Tree<String>> trees = reader.readBinarizeSection(23, 5000);
        BeamSearchDecoder decoder = new BeamSearchDecoder(16, true);
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
//            System.out.println(oracle.getSentence());
//            decoder.predict.displayStatesPath();
//            System.out.println("");
        }
        long end = System.currentTimeMillis();
        System.out.println("speed: " + oracles.size() / ((end - start) / 1000f));
    }

    @Override
    public void performDecoding(DPState state) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
