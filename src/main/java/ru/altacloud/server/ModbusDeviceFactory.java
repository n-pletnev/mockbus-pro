package ru.altacloud.server;

import ru.altacloud.model.ModbusDevice;
import ru.altacloud.model.Pump;

public class ModbusDeviceFactory {

    public static ModbusDevice createPump(Integer slaveID, Integer startCurrentValue, Integer currentLimit, Pump.Settings settings) {
         return new Pump(slaveID, startCurrentValue, currentLimit, settings);
    }
}
