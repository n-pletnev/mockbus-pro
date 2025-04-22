package ru.altacloud.model;

import java.util.*;
import java.util.stream.IntStream;

import static ru.altacloud.model.Mode.*;
import static ru.altacloud.model.Pump.RegisterName.*;

public class Pump implements ModbusDevice {

    private final Random random;
    private final Integer startCurrentValue;
    private final Integer currentLimit;

    enum RegisterName {
        STATE, MODE,
        CURRENT_CONSUMPTION_A, CURRENT_CONSUMPTION_B, CURRENT_CONSUMPTION_C,
        SET_CURRENT_MAX, SET_CURRENT_MIN, SET_CURRENT_DELTA
    }

    public record Settings(Integer min, Integer max, Integer delta) {}

    private final Integer slaveID;
    private final Map<Integer, Register> registers;

    public Pump(Integer slaveID, Integer startCurrentValue, Integer currentLimit, Settings settings) {
        this.slaveID = slaveID;
        this.random = new Random();
        this.startCurrentValue = startCurrentValue;
        this.currentLimit = currentLimit;

        registers = Map.of(
                STATE.ordinal(), new Register(STATE.ordinal(), ON.ordinal()),
                MODE.ordinal(), new Register(MODE.ordinal(), AUTO.ordinal()),
                CURRENT_CONSUMPTION_A.ordinal(), new Register(CURRENT_CONSUMPTION_A.ordinal(), random.nextInt(startCurrentValue, currentLimit)),
                CURRENT_CONSUMPTION_B.ordinal(), new Register(CURRENT_CONSUMPTION_B.ordinal(), random.nextInt(startCurrentValue, currentLimit)),
                CURRENT_CONSUMPTION_C.ordinal(), new Register(CURRENT_CONSUMPTION_C.ordinal(), random.nextInt(startCurrentValue, currentLimit)),
                SET_CURRENT_MIN.ordinal(), new Register(SET_CURRENT_MIN.ordinal(), settings.min),
                SET_CURRENT_MAX.ordinal(), new Register(SET_CURRENT_MAX.ordinal(), settings.max),
                SET_CURRENT_DELTA.ordinal(), new Register(SET_CURRENT_DELTA.ordinal(), settings.delta)
        );
    }

    @Override
    public Integer getSlaveID() {
        return slaveID;
    }

    @Override
    public List<Register> multipleRead(Integer start, Integer count) {
        if (start > registers.size() - 1 || start + count > registers.size())
            throw new IllegalArgumentException("Invalid start: " + start);
        return IntStream.range(start, start + count).mapToObj(this::readRegister).toList();
    }

    @Override
    public Register readRegister(Integer number) {
        List<Integer> currents = List.of(CURRENT_CONSUMPTION_A.ordinal(), CURRENT_CONSUMPTION_B.ordinal(), CURRENT_CONSUMPTION_C.ordinal());
        if (currents.contains(number))
            return registers.get(STATE.ordinal()).getValue() == OFF.ordinal() ? new Register(number, OFF.ordinal()) : generateCurrentConsumption(number);

        return Optional.ofNullable(this.registers.get(number))
                .orElseThrow(() -> new IllegalArgumentException("Invalid registry number: " + number));
    }

    @Override
    public void writeRegister(Register register) {
        List<Integer> settings = List.of(SET_CURRENT_MIN.ordinal(), SET_CURRENT_MAX.ordinal(), SET_CURRENT_DELTA.ordinal());
        if (Objects.equals(register.getNumber(), MODE.ordinal()))
            setMode(register.getValue());
        if (settings.contains(register.getNumber()))
            registers.get(register.getNumber()).setValue(register.getValue());
    }

    private void setMode(Integer mode) {
        if (Objects.equals(mode, OFF.ordinal())) {
            this.registers.get(STATE.ordinal()).setValue(OFF.ordinal());
            this.registers.get(MODE.ordinal()).setValue(OFF.ordinal());
        }
        if (Objects.equals(mode, ON.ordinal())) {
            this.registers.get(STATE.ordinal()).setValue(ON.ordinal());
            this.registers.get(MODE.ordinal()).setValue(ON.ordinal());
        }
        if (Objects.equals(mode, AUTO.ordinal())) {
            this.registers.get(STATE.ordinal()).setValue(ON.ordinal());
            this.registers.get(MODE.ordinal()).setValue(AUTO.ordinal());
        }
    }

    private Register generateCurrentConsumption(Integer number) {
        return new Register(number, random.nextInt(startCurrentValue, currentLimit));
    }
}
