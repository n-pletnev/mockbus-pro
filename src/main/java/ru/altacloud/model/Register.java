package ru.altacloud.model;

public class Register {
    private final Integer number;
    private Integer value;

    public Register(Integer number, Integer value) {
        this.number = number;
        this.value = value;
    }

    void setValue(Integer value) {
        this.value = value;
    }

    public Integer getNumber() {
        return number;
    }

    public Integer getValue() {
        return value;
    }
}
