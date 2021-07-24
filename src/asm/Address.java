package asm;

import genir.code.AddressOrData;

import java.util.Objects;

public class Address {
    public AddressOrData address;
    public boolean isLVal = false;

    /*public Address(int address) {
        this.address = address;
    }*/

    public Address(AddressOrData address) {
        this.address = address;
    }

    public Address(AddressOrData address, boolean isLVal) {
        this.address = address;
        this.isLVal = isLVal;
    }

    @Override
    public String toString() {
        return "$" + address;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Address address1 = (Address) o;
        return address == address1.address;
    }

    @Override
    public int hashCode() {
        return Objects.hash(address);
    }


}
