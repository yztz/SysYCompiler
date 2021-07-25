package compiler.genir.code;

import compiler.asm.Address;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class WrittenRepresent extends InterRepresent{
    public AddressOrData target;

    public WrittenRepresent() {
    }
    public WrittenRepresent(int targetAddress)
    {
        this.target = new AddressOrData(false,targetAddress);
    }
    public WrittenRepresent(AddressOrData target) {
        this.target = target;
    }

    @Override
    public Collection<Address> getAllAddress() {
        List<Address> addresses = new ArrayList<>();
        addresses.add(new Address(target,true));
        return addresses;
    }
}
