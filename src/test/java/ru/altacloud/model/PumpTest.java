package ru.altacloud.model;


import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

public class PumpTest {

    private final Pump pump = new Pump(1, 1500, 2000,
            new Pump.Settings(1000, 3000, 200));

    @Test
    public void testCreateNewPump() {
        Assertions.assertThat(pump.getSlaveID()).isEqualTo(1);
        Assertions.assertThat(pump.readRegister(0).getNumber()).isEqualTo(0);
        Assertions.assertThat(pump.readRegister(0).getValue()).isEqualTo(1);

        Assertions.assertThat(pump.readRegister(1).getValue()).isEqualTo(Mode.AUTO.ordinal());

        Assertions.assertThat(pump.readRegister(2).getValue()).isGreaterThanOrEqualTo(1500);
        Assertions.assertThat(pump.readRegister(2).getValue()).isLessThanOrEqualTo(2000);

    }

    @Test
    public void testTurnOffPump() {
        pump.writeRegister(new Register(1, 0));
        Assertions.assertThat(pump.readRegister(0).getValue()).isEqualTo(0);
        Assertions.assertThat(pump.readRegister(1).getValue()).isEqualTo(Mode.OFF.ordinal());
        Assertions.assertThat(pump.readRegister(2).getValue()).isEqualTo(0);
    }

    @Test
    public void testGetPumpCurrentByRegistryNumber() {
        Register register = pump.readRegister(2);
        Assertions.assertThat(register.getNumber()).isEqualTo(2);
        Assertions.assertThat(register.getValue()).isGreaterThan(0);
    }

    @Test
    public void testWriteToIllegalRegistry() {
        pump.writeRegister(new Register(2, 0));
        Assertions.assertThat(pump.readRegister(2).getValue()).isGreaterThan(0);
    }

    @Test
    public void testWriteToModeRegistry() {
        pump.writeRegister(new Register(1, 0));
        Register state = pump.readRegister(0);
        Register mode = pump.readRegister(1);
        Register current = pump.readRegister(2);
        Assertions.assertThat(current.getValue()).isEqualTo(0);
        Assertions.assertThat(state.getValue()).isEqualTo(0);
        Assertions.assertThat(mode.getValue()).isEqualTo(0);
    }

    @Test
    public void testMultipleRead() {
        List<Register> registries = pump.multipleRead(0, 3);
        Assertions.assertThat(registries.size()).isEqualTo(3);
    }

    @Test
    public void testMultipleReadCurrentRegister() {
        List<Register> registries = pump.multipleRead(2, 1);
        Assertions.assertThat(registries.size()).isEqualTo(1);
        Assertions.assertThat(registries.getFirst().getNumber()).isEqualTo(2);
    }
}
