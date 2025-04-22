package ru.altacloud.server;

import com.ghgande.j2mod.modbus.Modbus;
import com.ghgande.j2mod.modbus.ModbusException;
import com.ghgande.j2mod.modbus.slave.ModbusSlave;
import com.ghgande.j2mod.modbus.slave.ModbusSlaveFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.altacloud.model.Pump;

public class ModbusServer {

    private static final Logger log = LoggerFactory.getLogger(ModbusServer.class);
    private final ModbusSlave modbusSlave;

    public ModbusServer() throws ModbusException {
        modbusSlave = ModbusSlaveFactory.createTCPSlave(Modbus.DEFAULT_PORT, 1024, false);
        modbusSlave.addProcessImage(1, ProcessImageFactory
                .createPumpProcessImage(1, 3000, 3200, new Pump.Settings(1000, 4000, 1000)));
        modbusSlave.addProcessImage(2, ProcessImageFactory
                .createPumpProcessImage(2, 1500, 1700, new Pump.Settings(1000, 2000, 500)));
    }

    public void run() throws ModbusException {
        this.modbusSlave.open();
        log.info("ModbusServer started");
    }

}
