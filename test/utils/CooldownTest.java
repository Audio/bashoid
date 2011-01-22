package utils;

import org.junit.Test;
import static org.junit.Assert.*;


public class CooldownTest {

    public Cooldown createAndStartSample() {
        Cooldown sample = new Cooldown(5);
        sample.start();
        return sample;
    }

    @Test
    public void testWrongInput() {
        try {
            Cooldown instance = new Cooldown(-5);
            fail("Number of seconds cannot be negative.");
        } catch (IllegalArgumentException e) {
            // Passed
        }
    }

    @Test
    public void testLenght() {
        long fiveSeconds = 5;
        Cooldown instance = new Cooldown(fiveSeconds);
        long lenght = instance.length();
        assertEquals(lenght, fiveSeconds);
    }

    @Test
    public void testStart() {
        Cooldown instance = createAndStartSample();
        assertTrue( instance.isActive() );
    }

    @Test
    public void testStop() {
        Cooldown instance = createAndStartSample();
        instance.stop();
        assertFalse( instance.isActive() );
    }

    @Test
    public void testDoubleStart() {
        Cooldown instance = createAndStartSample();
        instance.stop();
        instance.start();
        assertTrue( instance.isActive() );
    }

}
