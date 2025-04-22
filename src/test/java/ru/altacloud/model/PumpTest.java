package ru.altacloud.model;


import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

public class PumpTest {

    @Test
    public void testCreateNewPump() {
        Pump pump = new Pump(1, 1500, 2000);

        Assertions.assertThat(pump.getSlaveID()).isEqualTo(1);
        Assertions.assertThat(pump.getState().getNumber()).isEqualTo(0);
        Assertions.assertThat(pump.getState().getValue()).isEqualTo(1);

        Assertions.assertThat(pump.getMode().getValue()).isEqualTo(Mode.AUTO.ordinal());

        Assertions.assertThat(pump.getCurrent().getValue()).isGreaterThanOrEqualTo(1500);
        Assertions.assertThat(pump.getCurrent().getValue()).isLessThanOrEqualTo(2000);

    }

    @Test
    public void testTurnOffPump() {
        Pump pump = new Pump(1, 1500, 2000);
        pump.setMode(0);
        Assertions.assertThat(pump.getState().getValue()).isEqualTo(0);
        Assertions.assertThat(pump.getMode().getValue()).isEqualTo(Mode.OFF.ordinal());
        Assertions.assertThat(pump.getCurrent().getValue()).isEqualTo(0);
    }

    @Test
    public void testGetPumpCurrentByRegistryNumber() {
        Pump pump = new Pump(1, 1500, 2000);
        Register register = pump.readRegister(2);
        Assertions.assertThat(register.getNumber()).isEqualTo(2);
        Assertions.assertThat(register.getValue()).isGreaterThan(0);
    }

    @Test
    public void testWriteToIllegalRegistry() {
        Pump pump = new Pump(1, 1500, 2000);
        pump.writeRegister(new Register(2, 0));
        Assertions.assertThat(pump.getCurrent().getValue()).isGreaterThan(0);
    }

    @Test
    public void testWriteToModeRegistry() {
        Pump pump = new Pump(1, 1500, 2000);
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
        Pump pump = new Pump(1, 1500, 2000);
        List<Register> registries = pump.multipleRead(0, 3);
        Assertions.assertThat(registries.size()).isEqualTo(3);
    }

    @Test
    public void testMultipleReadCurrentRegister() {
        Pump pump = new Pump(1, 1500, 2000);
        List<Register> registries = pump.multipleRead(2, 1);
        Assertions.assertThat(registries.size()).isEqualTo(1);
        Assertions.assertThat(registries.getFirst().getNumber()).isEqualTo(2);
    }
}
