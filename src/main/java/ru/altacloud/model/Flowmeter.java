package ru.altacloud.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.IntStream;

import static ru.altacloud.model.Flowmeter.RegisterName.*;

public class Flowmeter implements ModbusDevice<Float> {


    private static final Logger log = LoggerFactory.getLogger(Flowmeter.class);

    enum RegisterName {
        ACCUMULATED_VOLUME,
        FLOAT_DELIMITER,
        FLOW_RATE
    }

    private final Integer slaveID;
    private final Map<Integer, Register<Float>> registers;
    private final Random random = new Random();

    private volatile Boolean isActivePhase = false;

    public Flowmeter(Integer slaveID) {
        this.slaveID = slaveID;
        registers = new ConcurrentHashMap<>() {{
            put(ACCUMULATED_VOLUME.ordinal(), new Register<>(ACCUMULATED_VOLUME.ordinal(), 3f));
            put(FLOAT_DELIMITER.ordinal(), new Register<>(FLOAT_DELIMITER.ordinal(), Float.MAX_VALUE));
            put(FLOW_RATE.ordinal(), new Register<>(FLOW_RATE.ordinal(), 0f));
        }};
        new Thread(() -> {
            log.info("flowmeter {} check active phase thread started", slaveID);
            while (true) {
                try {
                    if (isActivePhase) {
                        Register<Float> accumulatedVolume = this.registers.get(ACCUMULATED_VOLUME.ordinal());
                        float increasedVolume = accumulatedVolume.getValue() + (random.nextFloat(2.3f, 2.7f) / 60);
                        accumulatedVolume.setValue(increasedVolume);
                        log.info("flowmeter {} volume size is {} m3", slaveID, increasedVolume);
                    }
                    Thread.sleep(Duration.of(10, ChronoUnit.SECONDS));
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
    }

    @Override
    public Integer getSlaveID() {
        return this.slaveID;
    }

    @Override
    public List<Register<Float>> multipleRead(Integer start, Integer count) {
        if (start % 2 != 0 || start > registers.size() || start + count > (registers.size() + 1) * 2)
            throw new IllegalArgumentException("Invalid start: " + start + ", count: " + count);
        return IntStream.iterate(start, i -> i + 2)
                .limit(count / 2)
                .mapToObj(this::readRegister)
                .sorted(Comparator.comparing(Register::getNumber))
                .toList();
    }

    @Override
    public Register<Float> readRegister(Integer number) {
        if (Objects.equals(ACCUMULATED_VOLUME.ordinal(), number)) return generateFlowRate();
        if (Objects.equals(FLOW_RATE.ordinal(), number)) return getAccumulatedVolume();
        throw new IllegalArgumentException("Invalid register number: " + number);
    }

    @Override
    public void writeRegister(Register<Float> register) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    private Register<Float> getAccumulatedVolume() {
        Register<Float> accumulatedVolume = registers.get(ACCUMULATED_VOLUME.ordinal());
        return new Register<>(accumulatedVolume.getNumber(), Math.round(accumulatedVolume.getValue() * 10) / 10f);
    }

    private Register<Float> generateFlowRate() {
        long now = Instant.now().toEpochMilli();
        long cyclePosition = now % (20 * 60 * 1000);
        if (cyclePosition < (10 * 60 * 1000)) {
            float rawValue = random.nextFloat(13.7f, 16.5f);
            float rounded = Math.round(rawValue * 10) / 10.0f;
            this.isActivePhase = Boolean.TRUE;
            return new Register<>(FLOW_RATE.ordinal(), rounded);
        } else {
            this.isActivePhase = Boolean.FALSE;
            return new Register<>(FLOW_RATE.ordinal(), 0f);
        }
    }
}
