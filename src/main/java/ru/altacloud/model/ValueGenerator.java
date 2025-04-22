package ru.altacloud.model;

import java.util.Random;

public class ValueGenerator {

    private static final Random RANDOM = new Random();

    public static int generateRandomWithin3Percent(int base) {
        if (base == 0) return 0;
        double percent = Math.abs(base) * 0.03;
        double min = base - percent;
        double max = base + percent;
        return (int) Math.round(min + (max - min) * RANDOM.nextDouble());
    }
}
