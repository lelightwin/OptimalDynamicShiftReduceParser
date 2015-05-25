/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nlp.nii.win.parser;

import java.util.List;
import nlp.nii.win.ConstantResource.Global;
import nlp.nii.win.MachineLearning.Perceptron;
import nlp.nii.win.parser.element.DPState;
import nlp.nii.win.parser.element.Value;
import static nlp.nii.win.parser.FeatureExtractor.*;

/**
 *
 * @author lelightwin
 */
public class PerceptronInteractor {

    private static final int maximumModelSize = 16000000;
    private static Perceptron learner; // leaner from system
    private static Perceptron parser; // leaner from system
    private static Perceptron pointer;
    public static final float offset = 0.0f;
    public static final float alpha = 1.0f;

    //<editor-fold defaultstate="collapsed" desc="update methods">
    public static void update(List<DPState> golds, List<DPState> predicts) {
        update(golds, -alpha); // decrease weights of gold one
        update(predicts, alpha); // increase weights of predict one
        pointer.accumulate();
    }

    private static void update(List<DPState> states, float offset) {
        for (int i = 1; i < states.size(); i++) {
            int action = states.get(i).getAction();
            update(action, states.get(i - 1), offset);
        }
    }

    public static void update(int action, DPState p, float offset) {
        Value[] feats = featuresFrom(p);
        for (int i = 0; i < feats.length; i++) {
            Value f = feats[i];
            pointer.update(f, action, i, offset);
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="scoring methods">
    public static float score(int action, DPState p) {
        float sc = 0.0f;
        Value[] feats = featuresFrom(p);
        for (int i = 0; i < feats.length; i++) {
            Value f = feats[i];
            sc += pointer.scoring(f, action, i);
        }
        return sc;
    }

    /**
     *
     * @param p
     * @param g
     * @return the violation between the score of predict and gold
     */
    public static float violation(DPState p, DPState g) {
        return p.cost() - g.cost();
    }
    //</editor-fold>

    public static void createLearner() {
        learner = new Perceptron(maximumModelSize, true);
        changeToLearn();
    }

    public static void saveLearner(String model) {
        learner.saveModels(Global.indexesMatrixFile + "." + model, Global.weightsFile + "." + model);
    }

    public static void loadParser(String model) {
        parser = new Perceptron(maximumModelSize, false);
        parser.loadModels(Global.indexesMatrixFile + "." + model, Global.weightsFile + "." + model);
        changeToParser();
    }

    public static int modelSize() {
        return pointer.modelSize();
    }

    public static void changeToLearn() {
        pointer = learner;
    }

    public static void changeToParser() {
        pointer = parser;
    }
}
