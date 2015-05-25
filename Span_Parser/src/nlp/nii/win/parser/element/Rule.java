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
public class Rule<T> {

    private T cons;
    private T action;
    private int head;

    public Rule(T cons, T action, int head) {
        this.cons = cons;
        this.action = action;
        this.head = head;
    }

    public int getHead() {
        return head;
    }

    public void setHead(int head) {
        this.head = head;
    }

    /**
     * @return the cons
     */
    public T getCons() {
        return cons;
    }

    /**
     * @param cons the cons to set
     */
    public void setCons(T cons) {
        this.cons = cons;
    }

    /**
     * @return the action
     */
    public T getAction() {
        return action;
    }

    /**
     * @param action the action to set
     */
    public void setAction(T action) {
        this.action = action;
    }
}
