package ru.altacloud.model;

import java.util.Random;

public class TemperatureSensor {

    public enum Purpose {
        WATER, AIR;
    }

    private final Integer registerNumber;
    private final Integer baseTemperature;

    public TemperatureSensor(Integer registerNumber, Purpose purpose) {
        var random = new Random();
        this.registerNumber = registerNumber;
        if (purpose == Purpose.WATER) {
            this.baseTemperature = random.nextInt(18, 21);
        } else {
            this.baseTemperature = random.nextInt(23, 28);
        }
    }

    public Register readRegister(Integer number) {
        return new Register(number, generateTemperature());
    }

    public Integer getRegisterNumber() {
        return registerNumber;
    }

    private Integer generateTemperature() {
        int rawValue = ValueGenerator.generateRandomWithin3Percent(baseTemperature);
        return (rawValue + 100) * 10;
    }
}
