package ru.altacloud.model;

public class Register<T extends Number> {

    private final Integer number;
    private T value;

    public Register(Integer number, T value) {
        this.number = number;
        this.value = value;
    }

    void setValue(T value) {
        this.value = value;
    }

    public Integer getNumber() {
        return number;
    }

    public T getValue() {
        return value;
    }
}
