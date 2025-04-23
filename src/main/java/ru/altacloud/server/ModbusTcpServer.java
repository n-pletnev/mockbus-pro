package ru.altacloud.server;

import com.digitalpetri.modbus.slave.ModbusTcpSlave;
import com.digitalpetri.modbus.slave.ModbusTcpSlaveConfig;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.WriteBufferWaterMark;
import io.netty.channel.nio.NioEventLoopGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutionException;

public class ModbusTcpServer {

    private static final int PORT = 5020;
    private static final Logger log = LoggerFactory.getLogger(ModbusTcpServer.class);

    private final EventLoopGroup bossGroup = new NioEventLoopGroup(1); // Для приема соединений
    private final EventLoopGroup workerGroup = new NioEventLoopGroup((Runtime.getRuntime().availableProcessors() * 2) + 1); // Для обработки данных

    private final ModbusTcpSlaveConfig config = new ModbusTcpSlaveConfig.Builder()
            .setBootstrapConsumer(bootstrap -> {
                bootstrap.option(ChannelOption.SO_BACKLOG, 1000); //максимальная очередь подключений
                bootstrap.childOption(ChannelOption.TCP_NODELAY, true);
                bootstrap.childOption(ChannelOption.SO_KEEPALIVE, true);
                bootstrap.childOption(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000);
                bootstrap.option(ChannelOption.WRITE_BUFFER_WATER_MARK, new WriteBufferWaterMark(32 * 1024, 64 * 1024)); // Контроль буферов записи
                bootstrap.childOption(ChannelOption.SO_REUSEADDR, true);
                bootstrap.childOption(ChannelOption.SO_LINGER, 0); // Быстрое закрытие соединений
                bootstrap.childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT); // Оптимизация памяти
            })
            .setEventLoop(bossGroup)
            .setExecutor(workerGroup)
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
