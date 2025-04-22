package ru.altacloud.server;

import com.ghgande.j2mod.modbus.procimg.ProcessImage;
import com.ghgande.j2mod.modbus.procimg.SimpleRegister;
import ru.altacloud.model.Pump;

import java.util.stream.IntStream;

public class ProcessImageFactory {

    public static ProcessImage createPumpProcessImage(Integer slaveID, Integer startCurrentValue, Integer currentLimit) {
        var pump = new Pump(slaveID, startCurrentValue, currentLimit);
        var customProcessImage = CustomProcessImage.of(pump);
        IntStream.range(1, 4).forEach(i -> customProcessImage.addRegister(new SimpleRegister(0)));
        return customProcessImage;
    }
}
