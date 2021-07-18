package gencode;

import genir.code.*;

import java.util.List;
import java.util.function.Consumer;

public class Util {

    public static void traverseAddress(InterRepresent ir, Consumer<Address> consumer) {
        if (null == ir) return;

        if (ir instanceof BinocularRepre) {
            BinocularRepre tmp = (BinocularRepre) ir;
            if (!tmp.target.isData) consumer.accept(new Address(tmp.target, true));
            if (!tmp.sourceFirst.isData) consumer.accept(new Address(tmp.sourceFirst));
            if (!tmp.sourceSecond.isData) consumer.accept(new Address(tmp.sourceSecond));
        } else if (ir instanceof CallRepresent){
            CallRepresent tmp = (CallRepresent) ir;
            if (null != tmp.returnResult) consumer.accept(new Address(tmp.returnResult, true));
            if (null != tmp.params) {
                for (AddressOrData param : tmp.params) {
                    if (!param.isData) consumer.accept(new Address(param));
                }
            }
        } else if (ir instanceof IfGotoRepresent) {
            IfGotoRepresent tmp = (IfGotoRepresent) ir;
            if (!tmp.left.isData) consumer.accept(new Address(tmp.left));
            if (!tmp.right.isData) consumer.accept(new Address(tmp.right));
        } else if (ir instanceof LoadRepresent) {
            LoadRepresent tmp = (LoadRepresent) ir;
            consumer.accept(new Address(tmp.target, true));
        } else if (ir instanceof ReturnRepresent) {
            ReturnRepresent tmp = (ReturnRepresent) ir;
            if (null != tmp.returnData && !tmp.returnData.isData) consumer.accept(new Address(tmp.returnData));
        } else if (ir instanceof SaveRepresent) {
            SaveRepresent tmp = (SaveRepresent) ir;
            if (!tmp.target.isData) consumer.accept(new Address(tmp.target));
        } else if (ir instanceof UnaryRepre) {
            UnaryRepre tmp = (UnaryRepre) ir;
            consumer.accept(new Address(tmp.target, true));
            if (!tmp.source.isData) consumer.accept(new Address(tmp.source));
        }
    }
}
