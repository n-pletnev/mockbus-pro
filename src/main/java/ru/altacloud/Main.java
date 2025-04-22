package ru.altacloud;

import com.ghgande.j2mod.modbus.ModbusException;
import ru.altacloud.server.ModbusServer;

public class Main {
    public static void main(String[] args) {
        try {
            new ModbusServer().run();
        } catch (ModbusException e) {
            throw new RuntimeException(e);
        }
    }
}