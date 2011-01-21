package utils;

import org.junit.Test;
import static org.junit.Assert.*;


public class CooldownTest {

    @Test
    public void testLenght() {
        long fiveSeconds = 5;
        Cooldown instance = new Cooldown(fiveSeconds);
        long lenght = instance.length();
        assertEquals(lenght, fiveSeconds);
    }

}
