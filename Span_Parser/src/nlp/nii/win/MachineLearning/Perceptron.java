/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nlp.nii.win.MachineLearning;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import nlp.nii.win.ConstantResource.Global;
import nlp.nii.win.parser.element.Value;
import nlp.nii.win.parser.element.Values;
import nlp.nii.win.util.CustomizeHashMap;
import nlp.nii.win.util.HashMatrix;

/**
 *
 * @author lelightwin
 */
public class Perceptron {

    public static boolean log = false;

    private WeightVector wVect;

    public Perceptron(int size, boolean train) {
        wVect = new WeightVector(size, train);
    }

    public Perceptron(WeightVector wVect) {
        this.wVect = wVect;
    }

    /**
     * updateIfNotExist the weight from labeled features by an offset
     *
     * @param feat
     * @param label
     * @param type
     * @param offset
     */
    public void update(Value feat, int label, int type, float offset) {
        if (feat != null) {
            wVect.updateWithOffset(label, type, feat, offset);
        }
    }

    /**
     *
     * @param feat
     * @param label
     * @param type
     * @return the sum weight score from label with its previous features
     */
    public float scoring(Value feat, int label, int type) {
        float sc = 0.0f;
        if (feat != null) {
            sc += wVect.value(label, type, feat);
        }
        return sc;
    }

    public int modelSize() {
        return wVect.size();
    }

    public void accumulate() {
        wVect.setC(wVect.getC() + 1);
    }

    public void displayWeights() {
        for (float w : wVect.getWeights()) {
            System.out.print(w + " ");
        }
        System.out.println("");
    }

    /**
     *
     * @param hashMatrixFile to save indexer from weight vector
     * @param weightsFile to save weight array from weight vector
     * @return
     */
    public WeightVector saveModels(String hashMatrixFile, String weightsFile) {
        WeightVector wv = null;
        try {

            BufferedWriter bfw1 = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(hashMatrixFile), "utf-8"));
            BufferedWriter bfw2 = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(weightsFile), "utf-8"));
            HashMatrix<Value> matrix = wVect.getMatrix();
            float[] averageWeights = wVect.takeAverage(); // get average weights
            wv = new WeightVector(matrix, averageWeights); // return the average weight vector
            for (int i = 0; i < Global.actionsNum; i++) {
                bfw1.write("$a$: " + i);
                bfw1.newLine();
                for (int j = 0; j < Global.featTypeNum; j++) {
                    CustomizeHashMap<Value> map = matrix.getMap(i, j);
                    if (!map.isEmpty()) {
                        bfw1.write("$f$: " + j);
                        bfw1.newLine();
                        Iterator<Entry<Value, Integer>> entries = map.entrySet().iterator();
                        while (entries.hasNext()) {
                            Entry entry = entries.next();
                            bfw1.write(entry.getKey().toString());
                            bfw1.write(">>");
                            bfw1.write(entry.getValue() + "");
                            bfw1.newLine();
                        }
                    }
                }
            }
            for (int i = 0; i < averageWeights.length; i++) {
                float w = averageWeights[i];
                bfw2.write(w + " ");
            }
            bfw2.close();
            bfw1.close();
        } catch (IOException ex) {
            Logger.getLogger(Perceptron.class.getName()).log(Level.SEVERE, null, ex);
        }
        return wv;
    }

    /**
     *
     * @param hashMatrixFile to load indexer from weight vector
     * @param weightsFile to load weight array from weight vector
     */
    public void loadModels(String hashMatrixFile, String weightsFile) {
        String error = "";

        try {
            BufferedReader bfr1 = new BufferedReader(new InputStreamReader(new FileInputStream(hashMatrixFile), "utf-8"));
            BufferedReader bfr2 = new BufferedReader(new InputStreamReader(new FileInputStream(weightsFile), "utf-8"));

            String[] weightsStr = bfr2.readLine().split(" ");
            for (int i = 0; i < weightsStr.length; i++) {
                wVect.getWeights()[i] = Float.parseFloat(weightsStr[i]);
            }
            wVect.setSize(weightsStr.length);

            String data1 = "";
            int x = -1;
            int y = -1;
            while ((data1 = bfr1.readLine()) != null) {
                if (data1.startsWith("$a$: ")) {
                    x = Integer.parseInt(data1.substring(5));
                } else if (data1.startsWith("$f$: ")) {
                    y = Integer.parseInt(data1.substring(5));
                } else {
                    String[] datas = data1.split(">>");
                    error = data1;
                    Value f = Values.from(datas[0]);
                    int idx = Integer.parseInt(datas[1]);
                    if (wVect.getWeights()[idx] != 0) {
                        wVect.put(x, y, f, idx);
                    }
                }
            }

            bfr2.close();
            bfr1.close();
        } catch (IOException ex) {
            Logger.getLogger(Perceptron.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NumberFormatException ex) {
            System.out.println(error);
        }
    }

    public static void main(String[] args) {
    }
}
