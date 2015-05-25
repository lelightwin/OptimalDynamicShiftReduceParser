/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nlp.nii.win.parser.element;

import nlp.nii.win.util.CustomizeHashMap;

/**
 *
 * @author lelightwin
 */
public class Value {

    private int value1 = -1;
    private int value2 = -1;
    private int value3 = -1;
//    private int index = -1;

    public Value(int v1) {
        this.value1 = v1;
    }

    public Value(int v1, int v2) {
        this.value1 = v1;
        this.value2 = v2;
    }

    public int getValue1() {
        return value1;
    }

    public int getValue2() {
        return value2;
    }

    public int getValue3() {
        return value3;
    }

    public Value(int v1, int v2, int v3) {
        this.value1 = v1;
        this.value2 = v2;
        this.value3 = v3;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        if (this.value1 >= 0) {
            hash = 59 * hash + this.value1;
        }
        if (this.value2 >= 0) {
            hash = 59 * hash + this.value2;
        }
        if (this.value3 >= 0) {
            hash = 59 * hash + this.value3;
        }
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Value other = (Value) obj;
        if (this.value1 != other.value1) {
            return false;
        }
        if (this.value2 != other.value2) {
            return false;
        }
        if (this.value3 != other.value3) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return toFeatValue();
    }

    public String toFeatValue() {
        String result = "";
        if (value1 >= 0) {
            result += value1 + " ";
        }
        if (value2 >= 0) {
            result += value2 + " ";
        }
        if (value3 >= 0) {
            result += value3 + " ";
        }
        return result.trim();
    }

    public static void main(String[] args) {
        CustomizeHashMap<Value> map = new CustomizeHashMap();
        Value f3 = Values.from(10, -1);
        map.updateIfNotExist(f3);

        Value f6 = Values.from(10, -1);
        System.out.println(map.get(f6));
    }
}
