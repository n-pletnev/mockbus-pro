package ru.altacloud.model;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.IntStream;

import static ru.altacloud.model.Blower.RegisterName.*;
import static ru.altacloud.model.Mode.*;

public class Blower implements ModbusDevice {

    public record Settings(Integer currentMin, Integer currentMax, Integer currentDelta, Integer overpressureMax,
                           Integer overpressureMin, Integer underpressureMax) {}

    enum RegisterName {
        STATE, MODE,
        CURRENT_CONSUMPTION_A, CURRENT_CONSUMPTION_B, CURRENT_CONSUMPTION_C,
        UNDERPRESSURE, OVERPRESSURE,
        SET_OVERPRESSURE_MAX, SET_OVERPRESSURE_MIN, SET_UNDERPRESSURE_MAX,
        SET_CURRENT_MAX, SET_CURRENT_MIN, SET_CURRENT_DELTA

    }

    private final Integer slaveID;
    private final Map<Integer, Register> registers;

    private final Integer workingCurrentConsumption;

    private final Integer workingOverPressure;
    private final Integer stoppedOverPressure;

    private final Integer workingUnderPressure;
    private final Integer stoppedUnderPressure;

    public Blower(Integer slaveID, Settings settings) {
        Random random = new Random();
        AvailableValueRange workingCurrentConsumption = new AvailableValueRange(7200, 7500);
        AvailableValueRange workingOverPressure = new AvailableValueRange(340, 360);
        AvailableValueRange stoppedOverPressure = new AvailableValueRange(4, 10);
        AvailableValueRange workingUnderPressure = new AvailableValueRange(30, 70);
        AvailableValueRange stoppedUnderPressure = new AvailableValueRange(2, 6);

        this.slaveID = slaveID;

        this.registers = new ConcurrentHashMap<>() {{
            put(STATE.ordinal(), new Register(STATE.ordinal(), ON.ordinal()));
            put(MODE.ordinal(), new Register(MODE.ordinal(), ON.ordinal()));

            put(CURRENT_CONSUMPTION_A.ordinal(), new Register(CURRENT_CONSUMPTION_A.ordinal(), 0));
            put(CURRENT_CONSUMPTION_B.ordinal(), new Register(CURRENT_CONSUMPTION_B.ordinal(), 0));
            put(CURRENT_CONSUMPTION_C.ordinal(), new Register(CURRENT_CONSUMPTION_C.ordinal(), 0));
            put(UNDERPRESSURE.ordinal(), new Register(UNDERPRESSURE.ordinal(), 0));
            put(OVERPRESSURE.ordinal(), new Register(UNDERPRESSURE.ordinal(), 0));

            put(SET_OVERPRESSURE_MAX.ordinal(), new Register(SET_OVERPRESSURE_MAX.ordinal(), settings.overpressureMax));
            put(SET_OVERPRESSURE_MIN.ordinal(), new Register(SET_OVERPRESSURE_MIN.ordinal(), settings.overpressureMin));
            put(SET_UNDERPRESSURE_MAX.ordinal(), new Register(SET_UNDERPRESSURE_MAX.ordinal(), settings.underpressureMax));

            put(SET_CURRENT_MIN.ordinal(), new Register(SET_CURRENT_MIN.ordinal(), settings.currentMin));
            put(SET_CURRENT_MAX.ordinal(), new Register(SET_CURRENT_MAX.ordinal(), settings.currentMax));
            put(SET_CURRENT_DELTA.ordinal(), new Register(SET_CURRENT_DELTA.ordinal(), settings.currentDelta));

        }};

        this.workingCurrentConsumption = random.nextInt(workingCurrentConsumption.min(), workingCurrentConsumption.max());
        this.workingOverPressure = random.nextInt(workingOverPressure.min(), workingOverPressure.max());
        this.stoppedOverPressure = random.nextInt(stoppedOverPressure.min(), stoppedOverPressure.max());
        this.workingUnderPressure = random.nextInt(workingUnderPressure.min(), workingUnderPressure.max());
        this.stoppedUnderPressure = random.nextInt(stoppedUnderPressure.min(), stoppedUnderPressure.max());
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
        if (currents.contains(number)) return new Register(number, generateCurrentConsumption());
        if (Objects.equals(OVERPRESSURE.ordinal(), number)) return new Register(number, generateOverPressure());
        if (Objects.equals(UNDERPRESSURE.ordinal(), number)) return new Register(number, generateUnderPressure());
        return Optional.ofNullable(this.registers.get(number))
                .orElseThrow(() -> new IllegalArgumentException("Invalid registry number: " + number));
    }

    @Override
    public void writeRegister(Register register) {
        List<Integer> settings = List.of(SET_CURRENT_MIN.ordinal(), SET_CURRENT_MAX.ordinal(), SET_CURRENT_DELTA.ordinal(),
                SET_OVERPRESSURE_MAX.ordinal(), SET_OVERPRESSURE_MIN.ordinal(), SET_UNDERPRESSURE_MAX.ordinal());
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
    }

    private Integer generateCurrentConsumption() {
        if (this.registers.get(STATE.ordinal()).getValue() == OFF.ordinal()) return OFF.ordinal();
        return ValueGenerator.generateRandomWithin3Percent(this.workingCurrentConsumption);
    }

    private Integer generateUnderPressure() {
        if (this.registers.get(STATE.ordinal()).getValue() == OFF.ordinal())
            return ValueGenerator.generateRandomWithin3Percent(stoppedUnderPressure);
        return ValueGenerator.generateRandomWithin3Percent(workingUnderPressure);
    }

    private Integer generateOverPressure() {
        if (this.registers.get(STATE.ordinal()).getValue() == OFF.ordinal())
            return ValueGenerator.generateRandomWithin3Percent(stoppedOverPressure);
        return ValueGenerator.generateRandomWithin3Percent(workingOverPressure);
    }
}
