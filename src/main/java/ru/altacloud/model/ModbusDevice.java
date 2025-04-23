package ru.altacloud.model;

import java.util.List;

public interface ModbusDevice<T extends Number> {

    Integer getSlaveID();
    Register<T> readRegister(Integer number);
    List<Register<T>> multipleRead(Integer start, Integer count);
    void writeRegister(Register<T> register);
}
