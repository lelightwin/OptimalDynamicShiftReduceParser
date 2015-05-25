/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nlp.nii.win.parser.element;

import java.util.List;
import nlp.nii.win.corpus.stanford.syntax.Tree;

/**
 *
 * @author lelightwin
 */
public class Sentence {

    private Word[] words;
    private int pointer = 0;

    public Sentence(int k) {
        words = new Word[k];
    }

    public Sentence(List<Tree<String>> wList) {
        words = new Word[wList.size()];
        for (int i = 0; i < wList.size(); i++) {
            this.add(wList.get(i));
        }
    }

    /**
     * add word w into sentence
     *
     * @param w
     */
    private void add(Tree<String> w) {
        words[pointer] = new Word(w);
        words[pointer].setIndex(pointer);
        pointer++;
    }

    public Word word(int i) {
        return words[i];
    }

    public Word queueWord(int i, int queueSize) {
        if (i < queueSize) {
            return words[length() - queueSize + i];
        }
        return null;
    }

    public int length() {
        return words.length;
    }

    @Override
    public String toString() {
        String result = "Sentence{ ";
        for (int i = 0; i < words.length; i++) {
            Word word = words[i];
            result += word + " ";
        }
        return result + '}';
    }

    public static void main(String[] args) {
        Tree<String> n1 = new Tree("NNP");
        Tree<String> n2 = new Tree("CC");
        Tree<String> n3 = new Tree("JJ");
        Tree<String> n4 = new Tree("NN");

        n1.getChildren().add(new Tree("Quang"));
        n2.getChildren().add(new Tree("Thang"));
        n3.getChildren().add(new Tree("Tong"));
        n4.getChildren().add(new Tree("Noi"));

        Sentence s = new Sentence(4);
        s.add(n1);
        s.add(n2);
        s.add(n3);
        s.add(n4);

        System.out.println(s.word(0));
    }
}
