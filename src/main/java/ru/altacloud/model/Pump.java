package ru.altacloud.model;

import java.util.*;
import java.util.stream.IntStream;

import static ru.altacloud.model.Mode.*;

public class Pump implements ModbusDevice {
    private final Random random;
    private final Integer startCurrentValue;
    private final Integer currentLimit;
    private final Integer slaveID;
    private final Register mode;
    private final Register state;
    private final Register current;

    public Pump(Integer slaveID, Integer startCurrentValue, Integer currentLimit) {
        this.random = new Random();
        this.startCurrentValue = startCurrentValue;
        this.currentLimit = currentLimit;

        this.slaveID = slaveID;
        this.state = new Register(0, ON.ordinal());
        this.mode = new Register(1, AUTO.ordinal());
        this.current = new Register(2, random.nextInt(startCurrentValue, currentLimit));

    }

    public Integer getSlaveID() {
        return slaveID;
    }

    @Override
    public Register readRegister(Integer number) {
        var registryMap = Map.of(
                state.getNumber(), this.getState(),
                mode.getNumber(), this.getMode(),
                current.getNumber(), this.getCurrent());
        return Optional.ofNullable(registryMap.get(number))
                .orElseThrow(() -> new IllegalArgumentException("Invalid registry number: " + number));
    }

    @Override
    public List<Register> multipleRead(Integer start, Integer count) {
        if (start > 2 || start + count > 3) throw new IllegalArgumentException("Invalid start: " + start);
        return IntStream.range(start, start + count).mapToObj(this::readRegister).toList();
    }

    @Override
    public void writeRegister(Register register) {
        if (register.getNumber().equals(this.mode.getNumber())) setMode(register.getValue());
    }

    public Register getState() {
        return state;
    }

    public Register getCurrent() {
        if (this.state.getValue().equals(OFF.ordinal())) {
            this.current.setValue(OFF.ordinal());
        } else {
            this.current.setValue(random.nextInt(startCurrentValue, currentLimit));
        }
        return current;
    }

    public Register getMode() {
        return mode;
    }

    public void setMode(Integer mode) {
        if (Objects.equals(mode, OFF.ordinal())) {
            this.state.setValue(OFF.ordinal());
            this.mode.setValue(OFF.ordinal());
        }
        if (Objects.equals(mode, ON.ordinal())) {
            this.state.setValue(ON.ordinal());
            this.mode.setValue(ON.ordinal());
        }
        if (Objects.equals(mode, AUTO.ordinal())) {
            this.state.setValue(ON.ordinal());
            this.mode.setValue(AUTO.ordinal());
        }
    }
}
