package ru.altacloud.model;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

public class ValveTest {

    private final Valve valve = new Valve(1);

    @Test
    public void testUpdateValveSettings() {
        int durationRunRegister = 6;
        valve.writeRegister(new Register(6, 20));
        Assertions.assertThat(valve.readRegister(durationRunRegister).getValue()).isEqualTo(20);
    }
}
