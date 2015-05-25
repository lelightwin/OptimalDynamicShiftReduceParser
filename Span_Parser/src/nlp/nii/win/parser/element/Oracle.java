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
import static nlp.nii.win.parser.PerceptronInteractor.*;

/**
 *
 * @author lelightwin
 */
public class Oracle {

    private Tree<String> tree;
    private ArrayList<DPState> states = new ArrayList<>();
    private ArrayList<String> actions = new ArrayList();
    private Sentence sentence;

    public Oracle(Tree<String> tree) {
        this.tree = tree;
        createStates();
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
        states.add(0, finishState);
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

            states.add(0, state);
        }

        DPState initState = new DPState(sentence, stack, queueSize); // initial state has no previous action
        states.add(0, initState);
    }

    public void genenrateScore() {
        states.get(0).setCost(0.0f);
        for (int i = 1; i < states.size(); i++) {
            DPState si = states.get(i); // state i (s{i})
            DPState si_1 = states.get(i - 1); // state i-1 (s{i-1})
            float cost = score(si.getAction(), si_1); // get transition cost from s{i-1} to s{i}
            si.setCost(cost + si_1.cost()); // s{i}.cost = s{i-1}.cost + transition cost
        }
    }

    public List<DPState> getStates(int step) {
        return states.subList(0, step + 1);
    }

    public int getNextAction(int step) { // return the next action at the step
        return states().get(step + 1).getAction();
    }

    public DPState state(int step) {
        return states.get(step);
    }

    public DPState start() {
        return states.get(0);
    }

    public int finalStep() {
        return states.size() - 1;
    }

    /**
     * @return the tree
     */
    public Tree<String> getTree() {
        return tree;
    }

    public ArrayList<DPState> states() {
        return states;
    }

    public Sentence sentence() {
        return sentence;
    }

    public void displayStates() {

        for (DPState ds : states()) {
            System.out.println(ds);
        }
    }

    public static void main(String[] args) {
        WinTreeBankReader reader = new WinTreeBankReader();
//
        Tree<String> t = reader.readSection(0, 100).get(67);
        Oracle oracle = new Oracle(t);
//        oracle.displayStates();

//        oracle.displayFeatures();
//        ArrayList<Oracle> oracles = new ArrayList<>();
//        System.out.println("Loading corpus!!!");
//        for (int i = 2; i <= 21; i++) { // section 2 to 21
//            for (Tree<String> t : reader.readSection(i, 5000)) {
//                oracles.add(new Oracle(t));
//            }
//            System.out.println("Section " + i + " done!!! (oracles size: " + oracles.size() + ")");
//        }
//        System.out.println("Complete!!!!!");
//        System.out.println("Number of training instances: " + oracles.size());
    }
}
