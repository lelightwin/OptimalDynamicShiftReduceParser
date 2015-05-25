/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nlp.nii.win.parser;

import nlp.nii.win.ConstantResource.Global;
import nlp.nii.win.parser.element.DPState;
import nlp.nii.win.parser.element.Value;
import nlp.nii.win.parser.element.Values;

/**
 *
 * @author lelightwin
 */
public class FeatureExtractor {

    public static Value[] featuresFrom(DPState p) {
        Value[] feats = new Value[Global.featTypeNum];
        int s0c = p.s0c(),
                s0ft = p.s0ft(), s0fw = p.s0fw(),
                s0lt = p.s0lt(), s0lw = p.s0lw(),
                s1c = p.s1c(),
                s1ft = p.s1ft(), s1fw = p.s1fw(),
                s1lt = p.s1lt(), s1lw = p.s1lw();

        int s0len = p.s0len();
        int s1len = p.s1len();

        int N0t = p.N0t(), N1t = p.N1t(), N2t = p.N2t(), N3t = p.N3t();
        int N0w = p.N0w(), N1w = p.N1w(), N2w = p.N2w(), N3w = p.N3w();

        feats[0] = Values.from(s0c, s0ft);
        feats[1] = Values.from(s0c, s0fw);
        feats[2] = Values.from(s0c, s0lt);
        feats[3] = Values.from(s0c, s0lw);
        feats[4] = Values.from(s0c, s0len);

        feats[5] = Values.from(s1c, s1ft);
        feats[6] = Values.from(s1c, s1fw);
        feats[7] = Values.from(s1c, s1lt);
        feats[8] = Values.from(s1c, s1lw);
        feats[9] = Values.from(s1c, s1len);

        feats[10] = Values.from(N0t, N0w);
        feats[11] = Values.from(N1t, N1w);
        feats[12] = Values.from(N2t, N2w);
        feats[13] = Values.from(N3t, N3w);

        // bigram features
        feats[14] = Values.from(s1lw, s0fw);
        feats[15] = Values.from(s0ft, s1lw);
        feats[16] = Values.from(s1lt, s0fw);
        feats[17] = Values.from(s1lt, s0ft);

        feats[18] = Values.from(s1c, s0fw);
        feats[19] = Values.from(s0c, s1fw);
        feats[20] = Values.from(s1c, s0lw);
        feats[21] = Values.from(s0c, s1lw);

        feats[22] = Values.from(s0fw, N0w);
        feats[23] = Values.from(s0lw, N0w);
        feats[24] = Values.from(N0t, s0fw);
        feats[25] = Values.from(N0t, s0lw);
        feats[26] = Values.from(s0c, N0w);
        feats[27] = Values.from(s0c, N0t);

        feats[28] = Values.from(N0w, N1w);
        feats[29] = Values.from(N1t, N0w);
        feats[30] = Values.from(N0t, N1w);
        feats[31] = Values.from(N0t, N1t);

        feats[32] = Values.from(s1fw, N0w);
        feats[33] = Values.from(s1lw, N0w);
        feats[34] = Values.from(N0t, s1fw);
        feats[35] = Values.from(N0t, s1lw);
        feats[36] = Values.from(s1c, N0w);
        feats[37] = Values.from(s1c, N0t);

        // trigram features
        feats[38] = Values.from(s0c, s1c, N0t);
        feats[39] = Values.from(s1c, N0t, s0fw);
        feats[40] = Values.from(s0c, N0t, s1fw);
        feats[41] = Values.from(s1c, N0t, s0lw);
        feats[42] = Values.from(s0c, N0t, s1lw);
        feats[43] = Values.from(s0c, s1c, N0w);

//        feats[44] = Values.from(s0fw, s0len);
//        feats[45] = Values.from(s0lw, s0len);
//        feats[46] = Values.from(s0c, s0ft, s0len);
//        feats[47] = Values.from(s0c, s0lt, s0len);
//
//        feats[48] = Values.from(s1fw, s1len);
//        feats[49] = Values.from(s1lw, s1len);
//        feats[50] = Values.from(s1c, s1ft, s1len);
//        feats[51] = Values.from(s1c, s1lt, s1len);
        return feats;
    }

}
