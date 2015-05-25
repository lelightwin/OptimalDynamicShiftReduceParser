/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nlp.nii.win.parser.decoder;

import nlp.nii.win.parser.element.DPState;
import nlp.nii.win.parser.element.Oracle;

/**
 * This is the beam search decoder using max violation strategy
 *
 * @author lelightwin
 */
public class BSMaxViolationDecoder extends Decoder {

    private int beam;

    public BSMaxViolationDecoder(int beam) {
        this.beam = beam;
    }

    @Override
    public void trainDecoding(Oracle oracle) {

    }

    @Override
    public void performDecoding(DPState state) {

    }

}
