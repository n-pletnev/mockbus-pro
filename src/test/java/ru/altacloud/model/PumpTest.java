package ru.altacloud.model;


import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

public class PumpTest {

    private final Pump pump = new Pump(1,
            new Pump.Settings(1000, 3000, 200, 100, 1000),
            new Pump.Restrictions(3800, 4000, 400, 460));

    @Test
    public void testCreateNewPump() {
        Assertions.assertThat(pump.getSlaveID()).isEqualTo(1);
        Assertions.assertThat(pump.readRegister(0).getNumber()).isEqualTo(0);
        Assertions.assertThat(pump.readRegister(0).getValue()).isEqualTo(1);

        Assertions.assertThat(pump.readRegister(1).getValue()).isEqualTo(Mode.AUTO.ordinal());

        Assertions.assertThat(pump.readRegister(2).getValue()).isGreaterThanOrEqualTo(3000);
        Assertions.assertThat(pump.readRegister(2).getValue()).isLessThanOrEqualTo(5000);

    }

    @Test
    public void testTurnOffPump() {
        pump.writeRegister(new Register<>(1, 0));
        Assertions.assertThat(pump.readRegister(0).getValue()).isEqualTo(0);
        Assertions.assertThat(pump.readRegister(1).getValue()).isEqualTo(Mode.OFF.ordinal());
        Assertions.assertThat(pump.readRegister(2).getValue()).isEqualTo(0);
    }

    @Test
    public void testGetPumpCurrentByRegistryNumber() {
        Register<Integer> register = pump.readRegister(2);
        Assertions.assertThat(register.getNumber()).isEqualTo(2);
        Assertions.assertThat(register.getValue()).isGreaterThan(0);
    }

    @Test
    public void testWriteToIllegalRegistry() {
        pump.writeRegister(new Register<>(2, 0));
        Assertions.assertThat(pump.readRegister(2).getValue()).isGreaterThan(0);
    }

    @Test
    public void testWriteToModeRegistry() {
        pump.writeRegister(new Register<>(1, 0));
        Register<Integer> state = pump.readRegister(0);
        Register<Integer> mode = pump.readRegister(1);
        Register<Integer> current = pump.readRegister(2);
        Assertions.assertThat(current.getValue()).isEqualTo(0);
        Assertions.assertThat(state.getValue()).isEqualTo(0);
        Assertions.assertThat(mode.getValue()).isEqualTo(0);
    }

    @Test
    public void testMultipleRead() {
        List<Register<Integer>> registries = pump.multipleRead(0, 3);
        Assertions.assertThat(registries.size()).isEqualTo(3);
    }

    @Test
    public void testMultipleReadCurrentRegister() {
        List<Register<Integer>> registries = pump.multipleRead(2, 1);
        Assertions.assertThat(registries.size()).isEqualTo(1);
        Assertions.assertThat(registries.getFirst().getNumber()).isEqualTo(2);
    }
}
