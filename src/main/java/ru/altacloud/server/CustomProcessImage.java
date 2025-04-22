package ru.altacloud.server;

import com.ghgande.j2mod.modbus.procimg.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.altacloud.model.ModbusDevice;

import java.util.stream.IntStream;

public class CustomProcessImage extends SimpleProcessImage {

    private static final Logger log = LoggerFactory.getLogger(CustomProcessImage.class);
    private final ModbusDevice device;

    public static CustomProcessImage of(ModbusDevice device) {
        return new CustomProcessImage(device);
    }

    private CustomProcessImage(ModbusDevice device) {
        this.device = device;
        IntStream.range(1, 6).forEach(i -> this.addRegister(i, new SimpleRegister(0)));
    }

    @Override
    public synchronized Register getRegister(int ref) throws IllegalAddressException {
        try {
            ru.altacloud.model.Register register = this.device.readRegister(ref);
            return new SimpleRegister(register.getValue());
        } catch (IllegalArgumentException e) {
            throw new IllegalAddressException(e.getMessage());
        }
    }

    @Override
    public synchronized void setRegister(int ref, Register reg) throws IllegalAddressException {
        log.info("write register: {} with new value: {}", ref, reg.getValue());
        this.device.writeRegister(new ru.altacloud.model.Register(ref, reg.getValue()));
    }
}
