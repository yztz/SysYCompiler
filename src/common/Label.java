package common;

import ir.code.IR;

public class Label implements ILabel {
    public static final Scheme DEFAULT = new Scheme(".L");
    public static final Scheme LOOP = new Scheme(".LOOP");

    public final String name;
    public IR bindIR;


    public IR getIR() {
        return this.bindIR;
    }

    private Label(String name) {
        this.name = name;
    }

    public static Label newLabel() {
        return newLabel(DEFAULT);
    }
    public static Label newLabel(String name) {
        return new Label(name);
    }
    public static Label newLabel(Scheme scheme) {
        return new Label(scheme.name + scheme.nextID++);
    }

    public static Label newLabel(IR ir) {
        Label label = newLabel();
        label.bindIR = ir;
        return label;
    }


    @Override
    public String toString() {
        return name;
    }


    @Override
    public String getLabelName() {
        return name;
    }

    static class Scheme {
        final String name;
        int nextID = 0;
        Scheme(String name) {
            this.name = name;
        }
    }
}
