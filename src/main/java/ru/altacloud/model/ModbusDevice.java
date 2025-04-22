package ru.altacloud.model;

import java.util.List;

public interface ModbusDevice {

    Integer getSlaveID();
    Register readRegister(Integer number);
    List<Register> multipleRead(Integer start, Integer count);
    void writeRegister(Register register);
}
