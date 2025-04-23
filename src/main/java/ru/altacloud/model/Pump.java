package ru.altacloud.model;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.IntStream;

import static ru.altacloud.model.Mode.*;
import static ru.altacloud.model.Pump.RegisterName.*;

public class Pump implements ModbusDevice {

    enum RegisterName {
        STATE, MODE,
        CURRENT_CONSUMPTION_A, CURRENT_CONSUMPTION_B, CURRENT_CONSUMPTION_C,
        OVERPRESSURE, SET_OVERPRESSURE_MAX, SET_OVERPRESSURE_MIN,
        SET_CURRENT_MAX, SET_CURRENT_MIN, SET_CURRENT_DELTA
    }

    public record Settings(Integer minCurrent, Integer maxCurrent, Integer deltaCurrent, Integer minOverPressure,
                           Integer maxOverPressure) {
    }

    public record Restrictions(Integer lowerCurrent, Integer higherCurrent, Integer lowerOverPressure,
                               Integer higherOverPressure) {
    }

    private final Integer slaveID;
    private final Map<Integer, Register> registers;

    private final Integer workingCurrentConsumption;
    private final Integer workingOverPressure;

    public Pump(Integer slaveID, Settings settings, Restrictions restrictions) {
        this.slaveID = slaveID;
        Random random = new Random();

        registers = new ConcurrentHashMap<>() {{
            put(STATE.ordinal(), new Register(STATE.ordinal(), ON.ordinal()));
            put(MODE.ordinal(), new Register(MODE.ordinal(), AUTO.ordinal()));
            put(CURRENT_CONSUMPTION_A.ordinal(), new Register(CURRENT_CONSUMPTION_A.ordinal(), 0));
            put(CURRENT_CONSUMPTION_B.ordinal(), new Register(CURRENT_CONSUMPTION_B.ordinal(), 0));
            put(CURRENT_CONSUMPTION_C.ordinal(), new Register(CURRENT_CONSUMPTION_C.ordinal(), 0));
            put(OVERPRESSURE.ordinal(), new Register(OVERPRESSURE.ordinal(), 0));

            put(SET_OVERPRESSURE_MAX.ordinal(), new Register(SET_OVERPRESSURE_MAX.ordinal(), settings.maxOverPressure));
            put(SET_OVERPRESSURE_MIN.ordinal(), new Register(SET_OVERPRESSURE_MAX.ordinal(), settings.minOverPressure));
            put(SET_CURRENT_MIN.ordinal(), new Register(SET_CURRENT_MIN.ordinal(), settings.minCurrent));
            put(SET_CURRENT_MAX.ordinal(), new Register(SET_CURRENT_MAX.ordinal(), settings.maxCurrent));
            put(SET_CURRENT_DELTA.ordinal(), new Register(SET_CURRENT_DELTA.ordinal(), settings.deltaCurrent));
        }};

        this.workingCurrentConsumption = random.nextInt(restrictions.lowerCurrent, restrictions.higherCurrent);
        this.workingOverPressure = random.nextInt(restrictions.lowerOverPressure, restrictions.higherOverPressure);

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
        if (currents.contains(number)) return generateCurrentConsumption(number);
        if (Objects.equals(OVERPRESSURE.ordinal(), number)) return generateOverPressure(number);
        return Optional.ofNullable(this.registers.get(number))
                .orElseThrow(() -> new IllegalArgumentException("Invalid registry number: " + number));
    }

    @Override
    public void writeRegister(Register register) {
        List<Integer> settings = List.of(SET_CURRENT_MIN.ordinal(), SET_CURRENT_MAX.ordinal(), SET_CURRENT_DELTA.ordinal(),
                SET_OVERPRESSURE_MAX.ordinal(), SET_OVERPRESSURE_MIN.ordinal());
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
        if (this.registers.get(STATE.ordinal()).getValue() == OFF.ordinal()) return new Register(number, OFF.ordinal());
        return new Register(number, ValueGenerator.generateRandomWithin3Percent(this.workingCurrentConsumption));
    }

    private Register generateOverPressure(Integer number) {
        if (this.registers.get(STATE.ordinal()).getValue() == OFF.ordinal()) return new Register(number, OFF.ordinal());
        return new Register(number, ValueGenerator.generateRandomWithin3Percent(this.workingOverPressure));
    }
}
