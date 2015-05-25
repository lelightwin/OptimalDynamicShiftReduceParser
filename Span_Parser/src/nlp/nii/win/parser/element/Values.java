/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nlp.nii.win.parser.element;

/**
 *
 * @author lelightwin
 */
public class Values {

    public static Value from(int v1) {
        if (v1 < 0) {
            return null;
        }
        return new Value(v1);
    }

    public static Value from(int v1, int v2) {
        if (v1 < 0) {
            return null;
        }
        if (v2 < 0) {
            return null;
        }
        return new Value(v1, v2);
    }

    public static Value from(int v1, int v2, int v3) {
        if (v1 < 0) {
            return null;
        }
        if (v2 < 0) {
            return null;
        }
        if (v3 < 0) {
            return null;
        }
        return new Value(v1, v2, v3);
    }

    public static Value from(String s) {
        int v1, v2, v3;
        String[] datas = s.split(" ");
        if (datas.length == 1) {
            v1 = Integer.parseInt(datas[0]);
            return new Value(v1);
        } else if (datas.length == 2) {
            v1 = Integer.parseInt(datas[0]);
            v2 = Integer.parseInt(datas[1]);
            return new Value(v1, v2);
        } else if (datas.length == 3) {
            v1 = Integer.parseInt(datas[0]);
            v2 = Integer.parseInt(datas[1]);
            v3 = Integer.parseInt(datas[2]);
            return new Value(v1, v2, v3);
        }
        return null;
    }
}
