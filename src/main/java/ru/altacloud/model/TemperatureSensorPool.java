package ru.altacloud.model;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class TemperatureSensorPool implements ModbusDevice {

    private final Integer slaveID;
    private final Map<Integer, TemperatureSensor> sensors;

    public TemperatureSensorPool(Integer slaveID, TemperatureSensor.Purpose purpose, Integer sensorCount) {
        this.slaveID = slaveID;
        this.sensors = IntStream.range(0, sensorCount)
                .mapToObj(i -> new TemperatureSensor(i, purpose))
                .collect(Collectors.toMap(TemperatureSensor::getRegisterNumber, temperatureSensor -> temperatureSensor));
    }

    @Override
    public Integer getSlaveID() {
        return slaveID;
    }

    @Override
    public List<Register> multipleRead(Integer start, Integer count) {
        if (start > sensors.size() - 1 || start + count > sensors.size())
            throw new IllegalArgumentException("Invalid start: " + start);
        return IntStream.range(start, start + count).mapToObj(this::readRegister).toList();
    }

    @Override
    public Register readRegister(Integer number) {
        return Optional.ofNullable(this.sensors.get(number))
                .map(temperatureSensor -> temperatureSensor.readRegister(number))
                .orElseThrow(() -> new IllegalArgumentException("Invalid registry number: " + number));
    }

    @Override
    public void writeRegister(Register register) {
        throw new UnsupportedOperationException("Not supported");
    }
}
