package ir.code;

import common.Label;

public class IR {
    public Label label;


    public String getLabelName() {
        if (null == label) return "";
        return label.name;
    }

}
