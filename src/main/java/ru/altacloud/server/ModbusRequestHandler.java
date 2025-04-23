package ru.altacloud.server;

import com.digitalpetri.modbus.requests.ReadHoldingRegistersRequest;
import com.digitalpetri.modbus.requests.WriteSingleRegisterRequest;
import com.digitalpetri.modbus.responses.ReadHoldingRegistersResponse;
import com.digitalpetri.modbus.responses.WriteSingleRegisterResponse;
import com.digitalpetri.modbus.slave.ServiceRequestHandler;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.altacloud.model.ModbusDevice;
import ru.altacloud.model.Register;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.IntStream;

public class ModbusRequestHandler implements ServiceRequestHandler {

    private static final Logger log = LoggerFactory.getLogger(ModbusRequestHandler.class);

    private final Map<Short, ModbusDevice<? extends Number>> deviceMap = new ConcurrentHashMap<>();

    public ModbusRequestHandler() {
        IntStream.range(1, 30).forEach(i -> deviceMap.put((short) i, ModbusDeviceFactory.createVihr450Pump(i)));
        IntStream.range(30, 60).forEach(i -> deviceMap.put((short) i, ModbusDeviceFactory.createPedrolloPump(i)));
        IntStream.range(60, 90).forEach(i -> deviceMap.put((short) i, ModbusDeviceFactory.createPumpingUnitPump(i)));
        IntStream.range(90, 140).forEach(i -> deviceMap.put((short) i, ModbusDeviceFactory.createBlower(i)));

        deviceMap.put((short) 141, ModbusDeviceFactory.createAirTemperatureSensorPool(141, 500));
        deviceMap.put((short) 142, ModbusDeviceFactory.createWaterTemperatureSensorPool(142, 500));

        IntStream.range(143, 193).forEach(i -> deviceMap.put((short) i, ModbusDeviceFactory.createDummy(i)));
        IntStream.range(194, 225).forEach(i -> deviceMap.put((short) i, ModbusDeviceFactory.createValve(i)));
        IntStream.range(225, 255).forEach(i -> deviceMap.put((short) i, ModbusDeviceFactory.createFlowmeter(i)));
    }

    @Override
    public void onReadHoldingRegisters(ServiceRequest<ReadHoldingRegistersRequest, ReadHoldingRegistersResponse> service) {
        ReadHoldingRegistersRequest request = service.getRequest();
        short slaveID = service.getUnitId();
        int start = request.getAddress();
        int quantity = request.getQuantity();

        Optional.ofNullable(deviceMap.get(slaveID)).ifPresent(device -> {
            ByteBuf registers = PooledByteBufAllocator.DEFAULT.buffer(request.getQuantity());
            try {
                device.multipleRead(start, quantity).forEach(register -> {
                    Number value = register.getValue();
                    if (value instanceof Integer) registers.writeShort(value.intValue());
                    if (value instanceof Float) registers.writeFloat(value.floatValue());
                });
            } catch (IllegalArgumentException e) {
                log.error("read register failed. slaveID: {} . error: {}", slaveID, e.getMessage());
            }
            service.sendResponse(new ReadHoldingRegistersResponse(registers));
            ReferenceCountUtil.release(request);
        });
    }

    @Override
    @SuppressWarnings("unchecked")
    public void onWriteSingleRegister(ServiceRequest<WriteSingleRegisterRequest, WriteSingleRegisterResponse> service) {
        short slaveID = service.getUnitId();
        WriteSingleRegisterRequest request = service.getRequest();
        int registerNumber = request.getAddress();
        int value = request.getValue();
        Optional.ofNullable(deviceMap.get(slaveID)).ifPresent(device -> {
            device.writeRegister(new Register(registerNumber, value));
            service.sendResponse(new WriteSingleRegisterResponse(registerNumber, value));
        });
    }
}
