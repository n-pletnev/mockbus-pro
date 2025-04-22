package ru.altacloud.server;

import com.ghgande.j2mod.modbus.procimg.ProcessImage;
import ru.altacloud.model.Pump;

public class ProcessImageFactory {

    public static ProcessImage createPumpProcessImage(Integer slaveID, Integer startCurrentValue, Integer currentLimit, Pump.Settings settings) {
        var pump = new Pump(slaveID, startCurrentValue, currentLimit, settings);
        return CustomProcessImage.of(pump);
    }
}
