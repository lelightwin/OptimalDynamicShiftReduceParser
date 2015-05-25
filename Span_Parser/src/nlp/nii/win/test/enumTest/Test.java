/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nlp.nii.win.test.enumTest;

import java.util.EnumMap;

/**
 *
 * @author lelightwin
 */
public class Test {

    public static void main(String[] args) {
        EnumMap<MyEnum, Integer> map = new EnumMap<>(MyEnum.class);
        Integer a = 4;
        Integer b = a;
       
        a = 3;
        System.out.println(b);
    }
}
