package compiler.genir.code;

import compiler.asm.AddressRWInfo;

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
    public Collection<AddressRWInfo> getAllAddressRWInfo() {
        List<AddressRWInfo> addressRWInfos = new ArrayList<>();
        addressRWInfos.add(new AddressRWInfo(target, true));
        return addressRWInfos;
    }
}
