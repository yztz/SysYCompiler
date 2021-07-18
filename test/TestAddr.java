import gencode.Address;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class TestAddr {
    @Test
    public void testAddr() {
        Map<Address, Boolean> map = new HashMap<>();
        Address addr1 = new Address(1);
        Address addr2 = new Address(1);
        map.put(addr1, true);
        map.put(addr2, false);
        System.out.println(map.get(addr1));
    }
}
