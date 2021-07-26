package compiler.asm;

import compiler.genir.code.AddressOrData;

import java.util.Objects;

/**
 * 记录某一条IR对某一三地址变量是读还是写
 */
public class AddressRWInfo {
    public AddressOrData address;
    public boolean isWrite = false;

    /*public Address(int address) {
        this.address = address;
    }*/

    public AddressRWInfo(AddressOrData address) {
        this.address = address;
    }

    public AddressRWInfo(AddressOrData address, boolean isWrite) {
        this.address = address;
        this.isWrite = isWrite;
    }

    @Override
    public String toString() {
        return "$" + address;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AddressRWInfo addressRWInfo1 = (AddressRWInfo) o;
        return address == addressRWInfo1.address;
    }

    @Override
    public int hashCode() {
        return Objects.hash(address);
    }


}
