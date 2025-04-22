package ru.altacloud.server;

import ru.altacloud.model.Blower;
import ru.altacloud.model.ModbusDevice;
import ru.altacloud.model.Pump;

public class ModbusDeviceFactory {

    public static ModbusDevice createVihr450Pump(Integer slaveID) {
        Pump.Settings settings = new Pump.Settings(1000, 4500, 1000, 200, 500);
        Pump.Restrictions restrictions = new Pump.Restrictions(3800, 4000, 400, 460);
        return new Pump(slaveID, settings, restrictions);
    }

    public static ModbusDevice createPedrolloPump(Integer slaveID) {
        Pump.Settings settings = new Pump.Settings(1000, 12000, 1000, 200, 5500);
        Pump.Restrictions restrictions = new Pump.Restrictions(11000, 11800, 5000, 5400);
        return new Pump(slaveID, settings, restrictions);
    }

    public static ModbusDevice createPumpingUnitPump(Integer slaveID) {
        Pump.Settings settings = new Pump.Settings(1000, 7500, 1000, 200, 1500);
        Pump.Restrictions restrictions = new Pump.Restrictions(6800, 7200, 1200, 1300);
        return new Pump(slaveID, settings, restrictions);
    }

    public static ModbusDevice createBlower(Integer slaveID) {
        Blower.Settings settings = new Blower.Settings(1000, 8000, 1000, 400, 100, 80);
        return new Blower(slaveID, settings);
    }
}
