/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nlp.nii.win.parser;

import nlp.nii.win.parser.decoder.Decoder;
import nlp.nii.win.parser.decoder.BSDecoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import nlp.nii.win.ConstantResource.Labels;
import nlp.nii.win.corpus.Binarizer;
import nlp.nii.win.corpus.WinTreeBankReader;
import nlp.nii.win.corpus.stanford.syntax.Tree;
import nlp.nii.win.parser.element.DPState;
import nlp.nii.win.parser.element.Oracle;
import nlp.nii.win.parser.metrics.LabeledConstituentEval;
import static nlp.nii.win.parser.PerceptronInteractor.*;

/**
 *
 * @author lelightwin
 */
public class Parser {

    private Decoder decoder;
    private final LabeledConstituentEval<String> eval = new LabeledConstituentEval<>(Collections.singleton("ROOT"), Labels.punctuation);

    public Parser(Decoder decoder) {

        this.decoder = decoder;
    }

    public void train(List<Oracle> oracles, int numberOfIterations, String model) {
        createLearner();
        System.out.println("Start training...");
        int iterationIdx;
        int oracleIdx;
        boolean learn = true;
        float errorRate;
        int error;

        for (int i = 0; i < numberOfIterations; i++) {
            iterationIdx = i + 1;
            error = 0;
            System.out.println("------------------------------------------------------------------------------------------------------------");
            System.out.println("Iteration " + iterationIdx + "th:");
            System.out.println("------------------------------------------------------------------------------------------------------------");
            if (learn) {
                long start = System.currentTimeMillis();
                double precision = 0.0;
                double recall = 0.0;
                long decodingTime = 0;
                long updatingTime = 0;
                for (int j = 0; j < oracles.size(); j++) {
                    oracleIdx = j + 1;
                    Oracle oracle = oracles.get(j);

                    // decoding and update parameters
                    long start1 = System.currentTimeMillis();
                    decoder.trainDecoding(oracle);
                    long end1 = System.currentTimeMillis();
                    decodingTime += (end1 - start1);

                    long start2 = System.currentTimeMillis();
                    DPState predict = decoder.getPredict();
                    if (predict != null) {
                        if (predict.isFinish()) { // predict is complete, evaluate the derived parse tree
                            Tree<String> t1 = predict.makeTree();
                            Tree<String> t2 = oracle.getTree();
                            Binarizer.debinarizing(t1);
                            Binarizer.debinarizing(t2);

                            eval.evaluate(t1, t2); // evaluate the derived tree with golden tree
                            precision += eval.getPrecision(); // get precision
                            recall += eval.getRecall(); // get recall
                        }

                        if (!predict.isGold()) { // if predict is not a gold
                            //update parameters
                            List<DPState> predictChain = predict.allStates();
                            List<DPState> goldenChain = oracle.getStates(predict.step());
                            update(goldenChain, predictChain);
                            error += 1;
                        }
                    }
                    long end2 = System.currentTimeMillis();
                    updatingTime += (end2 - start2) / 1000f;
                }
                long end = System.currentTimeMillis();
                if (error == 0) {
                    learn = false;
                }
                saveLearner(model + ".iteration." + iterationIdx);

                System.out.println("");
                System.out.println("ITERATION COMPLETE------------------------------------------------------------");
                errorRate = error * 100f / oracles.size();
                System.out.println(String.format("Parseval precision: %.3f", 100 * precision / oracles.size()));
                System.out.println(String.format("Parseval recall: %.3f", 100 * recall / oracles.size()));
                System.out.println(String.format("full sentence accuracy: %.3f", (float) (100 - errorRate)) + "%");
                System.out.println("number of error sentence: " + error);
                System.out.println("decoding speed(sentences/second): " + oracles.size() / ((end - start) / 1000f));
                System.out.println("model size: " + modelSize());
                System.out.println("Total time: " + (end - start) / 1000f);
                System.out.println("Total decoding time: " + decodingTime / 1000f);
                System.out.println("Total updating time: " + updatingTime / 1000f);
                System.out.println("");
            }
        }
        System.out.println("Learning complete!!!!");
    }

    public void evaluate(List<Oracle> oracles, String model) {
        System.out.println("Loading model...");
        loadParser(model);
        System.out.println("Done!!!");
        System.out.println("model size :" + modelSize());

        System.out.println("Start evaluation...");
        long start = System.currentTimeMillis();
        double precision = 0.0;
        double recall = 0.0;
        double totalFscore = 0.0;
        float correct = 0.0f;

        Random r = new Random();

        for (int i = 0; i < oracles.size(); i++) {
            Oracle oracle = oracles.get(i);
            decoder.performDecoding(oracle.start());
            DPState predict = decoder.getPredict();
//            System.out.print("sentence " + i + "... ");
            Tree<String> t1 = predict.makeTree();
            Tree<String> t2 = oracle.getTree();
            Binarizer.debinarizing(t1);
            Binarizer.debinarizing(t2);

            double fscore = eval.evaluate(t1, t2); // evaluate the derived tree with golden tree

            totalFscore += fscore;
//            System.out.printf("P:%.3f R:%.3f F:%.3f \n", 100 * eval.getPrecision(), 100 * eval.getRecall(), 100 * fscore);
            precision += eval.getPrecision();
            recall += eval.getRecall();
            if (fscore == 1.0f) {
                correct += 1;
            }
        }

        long end = System.currentTimeMillis();
        float totalTime = (end - start) / 1000f;
        System.out.println("------------------------------------------------------------------------------------------------------------");
        System.out.printf("P:%.3f - R:%.3f - F:%.3f \n", 100 * precision / oracles.size(), 100 * recall / oracles.size(), 100 * totalFscore / oracles.size());
        System.out.printf("Full sentence accuracy: %.3f \n", (100 * correct / oracles.size()));

        System.out.println("time: " + totalTime + " seconds");
        System.out.println("parsing speed(sentences/second): " + (oracles.size() / totalTime));
    }

    public static void main(String[] args) {

//        DPBSDecoder dpbeamDecoder = new DPBSDecoder(16, true);
        BSDecoder beamDecoder = new BSDecoder(16, true);

        Parser parser = new Parser(beamDecoder);
        WinTreeBankReader reader = new WinTreeBankReader();
        List<Oracle> oracles = new ArrayList<>();

        System.out.println("begin loading corpus...");
//        for (int i = 2; i <= 21; i++) {
        int i = 22;
        for (Tree<String> t : reader.readSection(i, 5000)) {
            oracles.add(new Oracle(t));
        }
//        }
        System.out.println("Done loading!!!");
//        parser.train(oracles, 50, "beam16.SF");
        for (int j = 0; j < 45; j++) {
            int iter = j + 1;
            System.out.println("----------------------------------------------------------------------");
            System.out.println("Iteration " + iter);
            parser.evaluate(oracles, "beam16.SF.iteration." + iter);
        }
//        parser.evaluate(oracles, "beam16.SF.iteration.2");
    }
}
