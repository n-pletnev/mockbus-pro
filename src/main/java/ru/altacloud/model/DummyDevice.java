package ru.altacloud.model;

import java.util.*;
import java.util.stream.IntStream;

import static ru.altacloud.model.DummyDevice.RegisterName.MODE;
import static ru.altacloud.model.DummyDevice.RegisterName.STATE;
import static ru.altacloud.model.Mode.OFF;
import static ru.altacloud.model.Mode.ON;

public class DummyDevice implements ModbusDevice<Integer> {

    enum RegisterName {
        STATE, MODE
    }

    private final Integer slaveID;
    private final Map<Integer, Register<Integer>> registers;

    public DummyDevice(Integer slaveID) {
        this.slaveID = slaveID;
        registers = new HashMap<>() {{
            put(STATE.ordinal(), new Register<>(STATE.ordinal(), 0));
            put(MODE.ordinal(), new Register<>(MODE.ordinal(), 0));
        }};
    }

    @Override
    public Integer getSlaveID() {
        return this.slaveID;
    }

    @Override
    public Register<Integer> readRegister(Integer number) {
        return Optional.ofNullable(this.registers.get(number))
                .orElseThrow(() -> new IllegalArgumentException("Invalid registry number: " + number));
    }

    @Override
    public List<Register<Integer>> multipleRead(Integer start, Integer count) {
        if (start > registers.size() - 1 || start + count > registers.size())
            throw new IllegalArgumentException("Invalid start: " + start);
        return IntStream.range(start, start + count).mapToObj(this::readRegister).toList();
    }

    @Override
    public void writeRegister(Register<Integer> register) {
        if (Objects.equals(register.getNumber(), MODE.ordinal())) {
            setMode(register.getValue());
        }
    }

    private void setMode(Integer mode) {
        if (mode == ON.ordinal() || mode == OFF.ordinal()) {
            this.registers.get(MODE.ordinal()).setValue(mode);
            this.registers.get(STATE.ordinal()).setValue(mode);
        }
    }
}
