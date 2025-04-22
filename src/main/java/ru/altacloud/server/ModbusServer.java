package ru.altacloud.server;

import com.ghgande.j2mod.modbus.Modbus;
import com.ghgande.j2mod.modbus.ModbusException;
import com.ghgande.j2mod.modbus.slave.ModbusSlave;
import com.ghgande.j2mod.modbus.slave.ModbusSlaveFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ModbusServer {

    private static final Logger log = LoggerFactory.getLogger(ModbusServer.class);
    private final ModbusSlave modbusSlave;

    public ModbusServer() throws ModbusException {
        modbusSlave = ModbusSlaveFactory.createTCPSlave(Modbus.DEFAULT_PORT, 1024, false);
        modbusSlave.addProcessImage(1, ProcessImageFactory.createPumpProcessImage(1, 3000, 3200));
        modbusSlave.addProcessImage(2, ProcessImageFactory.createPumpProcessImage(2, 1500, 1700));
    }

    public void run() throws ModbusException {
        this.modbusSlave.open();
        log.info("ModbusServer started");
    }

}
