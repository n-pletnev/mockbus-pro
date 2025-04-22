package ru.altacloud.server;

import com.digitalpetri.modbus.slave.ModbusTcpSlave;
import com.digitalpetri.modbus.slave.ModbusTcpSlaveConfig;
import io.netty.channel.ChannelOption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;

public class ModbusTcpServer {

    private static final int PORT = 5020;
    private static final Logger log = LoggerFactory.getLogger(ModbusTcpServer.class);
    private final ModbusTcpSlaveConfig config = new ModbusTcpSlaveConfig.Builder()
            .setBootstrapConsumer(bootstrap -> {
                bootstrap.option(ChannelOption.SO_BACKLOG, 128); //максимальная очередь подключений
                bootstrap.childOption(ChannelOption.TCP_NODELAY, true);
                bootstrap.childOption(ChannelOption.SO_KEEPALIVE, true);
                bootstrap.childOption(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000);
            })
            .setExecutor(Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()))
            .build();
    private final ModbusTcpSlave slave = new ModbusTcpSlave(config);

    public void run() throws ExecutionException, InterruptedException {
        slave.setRequestHandler(new ModbusRequestHandler());
        slave.bind("", PORT)
                .whenComplete((server, t) -> {
                    if (t != null) {
                        log.error("Server Error", t);
                        System.exit(1);
                    }
                    log.info("ModbusTcp server already started. Listening on port {}", PORT);
                })
                .get();
    }

    public void stop() {
        slave.shutdown();
    }
}
