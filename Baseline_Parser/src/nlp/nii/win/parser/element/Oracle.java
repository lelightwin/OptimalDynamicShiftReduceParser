/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nlp.nii.win.parser.element;

import java.util.ArrayList;
import java.util.List;
import nlp.nii.win.ConstantResource.Global;
import nlp.nii.win.corpus.WinTreeBankReader;
import nlp.nii.win.corpus.stanford.syntax.Tree;
import nlp.nii.win.corpus.stanford.util.Pair;
import nlp.nii.win.util.CustomizeStack;
import static nlp.nii.win.parser.RuleManager.*;

/**
 *
 * @author lelightwin
 */
public class Oracle {

    private Tree<String> tree;
    private ArrayList<DPState> goldenStates = new ArrayList<>();
    private Sentence sentence;

    public Oracle(Tree<String> tree) {
        this.tree = tree;
        createStates();
        createFeatures();
    }

    /**
     * generate the golden states from the input tree
     */
    private void createStates() {
        CustomizeStack<Tree<String>> stack = new CustomizeStack<>();
        List<Tree<String>> words = tree.getPreTerminals();
        sentence = new Sentence(words);
        int queueSize = 0; // size of current queue
        DPState finishState = new DPState(sentence, stack, queueSize); // finish state
        finishState.setAction(Global.FINISH);
        goldenStates.add(0, finishState);
        stack.push(tree); // init stack with only root node
        while (!stack.isEmpty()) {
            DPState state = new DPState(sentence, stack, queueSize); // this is current state
            Tree<String> t = stack.pop();
            int p = t.getNumberLabel();
            int action = -1;

            if (t.isBinary()) { //if t is a binary node, this is a binary action
                Pair<Tree<String>, Tree<String>> tlr = t.getBinaryChild();
                int lc = tlr.getFirst().getNumberLabel();
                int rc = tlr.getSecond().getNumberLabel();
                action = getBinaryRule(lc, rc, p).getAction(); // get action value
                stack.push(tlr.getFirst()); // add left child to stack
                stack.push(tlr.getSecond()); // add right child to stack
            } else { // if t is a unary node, there are three cases
                if (t.isPreTerminal()) { // t is a preterminal, this is shift action
                    queueSize += 1;
                    action = Global.SHIFT; // current action
                } else {
                    Tree<String> tu = t.getUnaryChild(); // check t's unary child: tu
                    int tuc = tu.getNumberLabel();
                    action = getUnaryRule(tuc, p).getAction(); // get action value

                    if (tu.isPreTerminal()) { // if tu is preTerminal, this is shift/unary action
                        queueSize += 1;
                    } else { // if tu is non-terminal, this is binary/unary action (there is no unary chain) => tu must be binary node
                        Pair<Tree<String>, Tree<String>> tulr = tu.getBinaryChild();
                        stack.push(tulr.getFirst()); // add left child to stack
                        stack.push(tulr.getSecond()); // add right child to stack
                    }
                }
            }

            state.setAction(action); // set action to be the previous action to current state (the action must take to get to state)
            goldenStates.add(0, state);
        }

        DPState initState = new DPState(sentence, stack, queueSize); // initial state has no previous action
        goldenStates.add(0, initState);
    }

    public ArrayList<DPState> getStates(int step) {
        ArrayList<DPState> states = new ArrayList<>();
        for (int i = 0; i <= step; i++) {
            states.add(goldenStates.get(i));
        }
        return states;
    }

    /**
     * create features for each golden state and the connection between them
     * (state[i-1] = previous state of state[i])
     */
    private void createFeatures() {
        for (int i = 0; i < goldenStates.size(); i++) {
            DPState si = goldenStates.get(i);
            if (i > 0) {
                DPState si_1 = goldenStates.get(i - 1);
                si.setPreState(si_1);
            }
        }
    }

    public int getNextAction(int step) { // return the next action at the step
        return getGoldenStates().get(step + 1).getAction();
    }

    public DPState state(int step) {
        return goldenStates.get(step);
    }

    public DPState start() {
        return goldenStates.get(0);
    }

    public int finalStep() {
        return goldenStates.size() - 1;
    }

    /**
     * @return the tree
     */
    public Tree<String> getTree() {
        return tree;
    }

    public ArrayList<DPState> getGoldenStates() {
        return goldenStates;
    }

    public Sentence getSentence() {
        return sentence;
    }

    public void displayStates() {

        for (DPState ds : getGoldenStates()) {
            System.out.println(ds);
        }
    }

    public static void main(String[] args) {
        WinTreeBankReader reader = new WinTreeBankReader();
//
//        Tree<String> t = reader.readSection(0, 100).get(0);
//        Oracle oracle = new Oracle(t);
//        oracle.displayStates();
//        oracle.displayFeatures();
        ArrayList<Oracle> oracles = new ArrayList<>();
        System.out.println("Loading corpus!!!");
        for (int i = 2; i <= 21; i++) { // section 2 to 21
            for (Tree<String> t : reader.readBinarizeSection(i, 5000)) {
                oracles.add(new Oracle(t));
            }
            System.out.println("Section " + i + " done!!! (oracles size: " + oracles.size() + ")");
        }
        System.out.println("Complete!!!!!");
        System.out.println("Number of training instances: " + oracles.size());
    }
}
