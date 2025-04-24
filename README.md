
# Mockbus Pro

Mockbus Pro is a Modbus TCP server simulator that emulates various industrial devices for testing and development purposes. It creates virtual representations of equipment such as pumps, blowers, temperature sensors, valves, and flowmeters, each with realistic behavior and register mappings.

The server listens on port 5020 and supports standard Modbus TCP read and write operations. Each simulated device is assigned a specific Slave ID range and implements appropriate register structures with dynamic values that change over time to mimic real-world behavior.

This tool is useful for:
- Testing Modbus client applications without physical hardware
- Development and debugging of SCADA systems
- Training and educational purposes
- Simulating complex industrial environments

# Device Groups and Registers in ModbusRequestHandler

## Device Groups and SlaveID Ranges

| Device Type | SlaveID Range |
|-------------|---------------|
| Vihr450Pump | 1-29 |
| PedrolloPump | 30-59 |
| PumpingUnitPump | 60-89 |
| Blower | 90-139 |
| AirTemperatureSensorPool | 141 |
| WaterTemperatureSensorPool | 142 |
| DummyDevice | 143-192 |
| Valve | 194-224 |
| Flowmeter | 225-254 |

## Device Registers

### Pump (Vihr450Pump, PedrolloPump, PumpingUnitPump)

| Register Name | Register Number (ordinal) | Description |
|---------------|---------------------------|-------------|
| STATE | 0 | Device state (ON/OFF/AUTO) |
| MODE | 1 | Operating mode (ON/OFF/AUTO) |
| CURRENT_CONSUMPTION_A | 2 | Current consumption for phase A |
| CURRENT_CONSUMPTION_B | 3 | Current consumption for phase B |
| CURRENT_CONSUMPTION_C | 4 | Current consumption for phase C |
| OVERPRESSURE | 5 | Current overpressure value |
| SET_OVERPRESSURE_MAX | 6 | Maximum overpressure setting |
| SET_OVERPRESSURE_MIN | 7 | Minimum overpressure setting |
| SET_CURRENT_MAX | 8 | Maximum current setting |
| SET_CURRENT_MIN | 9 | Minimum current setting |
| SET_CURRENT_DELTA | 10 | Current delta setting |

### Blower

| Register Name | Register Number (ordinal) | Description |
|---------------|---------------------------|-------------|
| STATE | 0 | Device state (ON/OFF) |
| MODE | 1 | Operating mode (ON/OFF) |
| CURRENT_CONSUMPTION_A | 2 | Current consumption for phase A |
| CURRENT_CONSUMPTION_B | 3 | Current consumption for phase B |
| CURRENT_CONSUMPTION_C | 4 | Current consumption for phase C |
| UNDERPRESSURE | 5 | Current underpressure value |
| OVERPRESSURE | 6 | Current overpressure value |
| SET_OVERPRESSURE_MAX | 7 | Maximum overpressure setting |
| SET_OVERPRESSURE_MIN | 8 | Minimum overpressure setting |
| SET_UNDERPRESSURE_MAX | 9 | Maximum underpressure setting |
| SET_CURRENT_MAX | 10 | Maximum current setting |
| SET_CURRENT_MIN | 11 | Minimum current setting |
| SET_CURRENT_DELTA | 12 | Current delta setting |

### TemperatureSensorPool (Air and Water)

Each TemperatureSensorPool contains multiple temperature sensors (500 sensors for each pool).

| Register Number | Description |
|-----------------|-------------|
| 0 to 499 | Individual temperature sensors, each with its own register number |

Each sensor returns a temperature value calculated as `(baseTemperature + 100) * 10`, where:
- Water sensors have a base temperature of 18-21°C
- Air sensors have a base temperature of 23-28°C

### DummyDevice

| Register Name | Register Number (ordinal) | Description |
|---------------|---------------------------|-------------|
| STATE | 0 | Device state (ON/OFF) |
| MODE | 1 | Operating mode (ON/OFF) |

### Valve

| Register Name | Register Number (ordinal) | Description |
|---------------|---------------------------|-------------|
| STATE | 0 | Device state (ON/OFF) |
| MODE | 1 | Operating mode (ON/OFF) |
| OVERPRESSURE | 2 | Current overpressure value |
| SET_OVERPRESSURE_MAX | 3 | Maximum overpressure setting |
| SET_OVERPRESSURE_MIN | 4 | Minimum overpressure setting |
| SET_RUN_DURATION | 5 | Run duration in seconds (default: 40 minutes) |
| SET_PAUSE_DURATION | 6 | Pause duration in seconds (default: 20 minutes) |

### Flowmeter

| Register Name | Register Number (ordinal) | Description |
|---------------|---------------------------|-------------|
| ACCUMULATED_VOLUME | 0 | Accumulated volume in m³ |
| FLOAT_DELIMITER | 1 | Delimiter for float values (set to Float.MAX_VALUE) |
| FLOW_RATE | 2 | Current flow rate |

Note: Flowmeter is the only device that uses Float values for its registers, while all other devices use Integer values.