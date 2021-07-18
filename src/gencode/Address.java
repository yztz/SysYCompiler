package gencode;

import genir.code.AddressOrData;

public class Address implements Location{
    public Object address;

    public Address(AddressOrData address) {
        this.address = address.item;
    }

    public Address(Object address) {
        this.address = address;
    }
}
