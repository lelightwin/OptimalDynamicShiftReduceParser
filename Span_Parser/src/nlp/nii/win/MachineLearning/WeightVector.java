/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nlp.nii.win.MachineLearning;

import nlp.nii.win.ConstantResource.Global;
import nlp.nii.win.parser.element.Value;
import nlp.nii.win.parser.element.Values;
import nlp.nii.win.util.HashMatrix;

/**
 * This is a class for weight vector, it will convert from feature to its weight
 *
 * @author lelightwin
 */
public class WeightVector {

    private float C = 1.0f;
    private int size = 0;
    private int maximumSize;
    private HashMatrix<Value> matrix = new HashMatrix(Global.actionsNum, Global.featTypeNum);
    private float[] W;
    private float[] sumW;
    private boolean train;

    public WeightVector(HashMatrix<Value> matrix, float[] weights){
        this.matrix = matrix;
        this.W = weights;
    }
    
    public WeightVector(int maximumSize, boolean train) {
        this.maximumSize = maximumSize;
        this.W = new float[maximumSize];
        for (int i = 0; i < W.length; i++) {
            W[i] = 0.0f;
        }
        this.train = train;
        if (this.train) {
            this.sumW = new float[maximumSize];
            for (int i = 0; i < sumW.length; i++) {
                sumW[i] = 0.0f;
            }
        }
    }

    /*-------------------------------------------------------------------------------*/
    /*updateIfNotExist weights with an offset*/
    private void updateWithOffset(int idx, float offset) {
        W[idx] += offset;
        if (this.train) {
            sumW[idx] += C * offset;
        }
    }

    public void updateWithOffset(int action, int featType, Value value, float offset) {
        int idx = updateIfNotExist(action, featType, value);
        updateWithOffset(idx, offset);
    }

    /*--------------------------------------------------------------------------------*/
    /*updateIfNotExist new feature if existed*/
    /**
     *
     * @param action
     * @param featType
     * @param value
     * @return return the value from a feature, if there is no, then
     * updateIfNotExist
     */
    public Integer updateIfNotExist(int action, int featType, Value value) {
        int idx = matrix.update(action, featType, value, size);
        if (idx == size) {
            size += 1;
        }
        return idx;
    }

    public void put(int action, int featType, Value value, int idx) {
        matrix.put(action, featType, value, idx);
    }

    /**
     *
     * @param action
     * @param featType
     * @param value
     * @return the index from feature in the weight array
     */
    public Integer get(int action, int featType, Value value) {
        return matrix.get(action, featType, value);
    }

    /**
     *
     * @param action
     * @param featType
     * @param value
     * @return the weight value from feature
     */
    public float value(int action, int featType, Value value) {

        Integer idx = this.get(action, featType, value);
        if (idx != null) {
            return W[idx];
        }
        return 0.0f;
    }

    /**
     *
     * @return the current average weights
     */
    public float[] takeAverage() {
        float[] averageWeights = new float[size];
        for (int i = 0; i < size; i++) {
            float w = W[i];
            float acw = sumW[i];
            float aw = (w - acw / C);
            averageWeights[i] = aw;
        }
        return averageWeights;
    }

    public HashMatrix<Value> getMatrix() {
        return matrix;
    }

    public float[] getWeights() {
        return W;
    }

    public static void main(String[] args) {
        WeightVector wVect = new WeightVector(10, false);
        wVect.updateWithOffset(10, 20, Values.from(1, 2), 0.1f);
        wVect.updateWithOffset(10, 20, Values.from(2, 3), 0.1f);
        wVect.updateWithOffset(10, 20, Values.from(1, 2), 0.25f);
        wVect.updateWithOffset(10, 20, Values.from(2, 3), -0.14f);

        System.out.println(wVect.value(10, 20, Values.from(1, 2)));
        System.out.println(wVect.value(10, 20, Values.from(2, 3)));
    }

    /**
     * @return the C
     */
    public float getC() {
        return C;
    }

    /**
     * @param c the C to set
     */
    public void setC(float c) {
        this.C = c;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int size() {
        return size;
    }

    public void setMatrix(HashMatrix<Value> matrix) {
        this.matrix = matrix;
    }

    public void setWeights(float[] weights) {
        this.W = weights;
    }
}
