package ru.altacloud.model;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import static ru.altacloud.model.DummyDevice.RegisterName.MODE;
import static ru.altacloud.model.DummyDevice.RegisterName.STATE;
import static ru.altacloud.model.Mode.OFF;
import static ru.altacloud.model.Mode.ON;

public class DummyDeviceTest {

    private final DummyDevice dummyDevice = new DummyDevice(1);

    @Test
    public void testTurnOff() {
        dummyDevice.writeRegister(new Register(MODE.ordinal(), OFF.ordinal()));
        Assertions.assertThat(dummyDevice.readRegister(STATE.ordinal()).getValue()).isEqualTo(OFF.ordinal());
        Assertions.assertThat(dummyDevice.readRegister(MODE.ordinal()).getValue()).isEqualTo(OFF.ordinal());
    }

    @Test
    public void testTurnOn() {
        dummyDevice.writeRegister(new Register(MODE.ordinal(), ON.ordinal()));
        Assertions.assertThat(dummyDevice.readRegister(STATE.ordinal()).getValue()).isEqualTo(ON.ordinal());
    }

    @Test
    public void testWhenWriteUnknownRegisterThenNothingThrown() {
        Assertions.assertThatCode(() -> dummyDevice.writeRegister(new Register(100, 100)))
                .doesNotThrowAnyException();
    }
}
