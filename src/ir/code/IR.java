package ir.code;

import common.ILabel;
import common.Label;

public class IR {
    public ILabel label;

    public IR setLabel(ILabel label) {
        this.label = label;
        return this;
    }

    public String getLabelName() {
        if (null == label) return "";
        return label.getLabelName();
    }

}
