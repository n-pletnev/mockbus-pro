package ru.altacloud.model;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.IntStream;

import static ru.altacloud.model.DummyDevice.RegisterName.MODE;
import static ru.altacloud.model.DummyDevice.RegisterName.STATE;
import static ru.altacloud.model.Mode.*;
import static ru.altacloud.model.Valve.RegisterName.*;

public class Valve implements ModbusDevice<Integer> {

    enum RegisterName {
        STATE, MODE, OVERPRESSURE,
        SET_OVERPRESSURE_MAX, SET_OVERPRESSURE_MIN,
        SET_RUN_DURATION, SET_PAUSE_DURATION
    }

    private final Integer slaveID;
    private final Map<Integer, Register<Integer>> registers;
    private final Integer workingOverPressure;
    private final Integer stoppedOverPressure;

    public Valve(Integer slaveID) {
        Random random = new Random();
        this.slaveID = slaveID;
        this.registers = new ConcurrentHashMap<>() {{
            put(STATE.ordinal(), new Register<>(STATE.ordinal(), ON.ordinal()));
            put(MODE.ordinal(), new Register<>(MODE.ordinal(), ON.ordinal()));
            put(OVERPRESSURE.ordinal(), new Register<>(OVERPRESSURE.ordinal(), 0));
            put(SET_OVERPRESSURE_MAX.ordinal(), new Register<>(SET_OVERPRESSURE_MAX.ordinal(), 350));
            put(SET_OVERPRESSURE_MIN.ordinal(), new Register<>(SET_OVERPRESSURE_MAX.ordinal(), 100));
            put(SET_RUN_DURATION.ordinal(), new Register<>(SET_RUN_DURATION.ordinal(), 40 * 60)); //seconds
            put(SET_PAUSE_DURATION.ordinal(), new Register<>(SET_PAUSE_DURATION.ordinal(), 20 * 60)); //seconds
        }};

        this.workingOverPressure = random.nextInt(310, 330);
        this.stoppedOverPressure = random.nextInt(150, 160);
    }

    @Override
    public Integer getSlaveID() {
        return slaveID;
    }

    @Override
    public List<Register<Integer>> multipleRead(Integer start, Integer count) {
        if (start > registers.size() - 1 || start + count > registers.size())
            throw new IllegalArgumentException("Invalid start: " + start);
        return IntStream.range(start, start + count).mapToObj(this::readRegister).toList();
    }

    @Override
    public Register<Integer> readRegister(Integer number) {
        if (Objects.equals(OVERPRESSURE.ordinal(), number)) return generateOverPressure();
        return Optional.ofNullable(this.registers.get(number))
                .orElseThrow(() -> new IllegalArgumentException("Invalid registry number: " + number));
    }

    @Override
    public void writeRegister(Register<Integer> register) {
        List<Integer> settings = List.of(SET_PAUSE_DURATION.ordinal(), SET_RUN_DURATION.ordinal(),
                SET_OVERPRESSURE_MAX.ordinal(), SET_OVERPRESSURE_MIN.ordinal());
       if (MODE.ordinal() == register.getNumber()) setMode(register.getValue());
       if (settings.contains(register.getNumber())) {
           this.registers.get(register.getNumber()).setValue(register.getValue());
       }
    }

    private Register<Integer> generateOverPressure() {
        if (registers.get(STATE.ordinal()).getValue() == OFF.ordinal())
            return new Register<>(OVERPRESSURE.ordinal(), ValueGenerator.generateRandomWithin3Percent(workingOverPressure));

        return new Register<>(OVERPRESSURE.ordinal(), ValueGenerator.generateRandomWithin3Percent(stoppedOverPressure));
    }

    private void setMode(Integer mode) {
        if (mode == ON.ordinal() || mode == OFF.ordinal()) {
            this.registers.get(MODE.ordinal()).setValue(mode);
            this.registers.get(STATE.ordinal()).setValue(mode);
        }
    }
}
