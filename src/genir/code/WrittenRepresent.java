package genir.code;

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
}
