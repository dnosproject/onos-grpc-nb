package org.onosproject.grpcserver;

import io.grpc.Server;
import io.grpc.netty.NettyServerBuilder;
import org.onlab.util.Tools;
import org.onosproject.cfg.ComponentConfigService;
import org.onosproject.impl.eventNotificationService.EventNotificationServiceImpl;
import org.onosproject.impl.packetOutService.PacketOutServiceImpl;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Dictionary;

import static org.onosproject.grpcserver.OsgiPropertyConstants.GRPC_PORT;
import static org.onosproject.grpcserver.OsgiPropertyConstants.GRPC_PORT_DEFAULT;

@Component(immediate = true,
        service = GrpcInterface.class,
        property = {GRPC_PORT + ":Integer=" + GRPC_PORT_DEFAULT})
public class GrpcServer implements GrpcInterface {

  private final Logger log = LoggerFactory.getLogger(getClass());

  @Reference(cardinality = ReferenceCardinality.MANDATORY)
  protected ComponentConfigService cfgService;

  protected Server server;
  private int grpcPort = GRPC_PORT_DEFAULT;
  private InternalGrpcServer grpcServer;

  @Activate
  protected void activate() {

    cfgService.registerProperties(getClass());
    grpcServer = new InternalGrpcServer();

    try {
      grpcServer.start();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Deactivate
  protected void deactivate() {

    cfgService.unregisterProperties(getClass(), false);
    grpcServer.stop();
  }

    @Modified
    public void modified(ComponentContext context) {
        readComponentConfiguration(context);

    }

    private void readComponentConfiguration(ComponentContext context) {
        Dictionary<?, ?> properties = context.getProperties();

        grpcPort = Tools.getIntegerProperty(properties, GRPC_PORT, GRPC_PORT_DEFAULT);
        log.info("Configured. GRPC port is configured to {} ", grpcPort);

    }

  private class InternalGrpcServer {

    private void start() throws IOException {

      server =
          NettyServerBuilder.forPort(grpcPort)
              .addService(new PacketOutServiceImpl())
                  .addService(new EventNotificationServiceImpl())
              //.addService(new PacketEventServiceImpl())
              .build()
              .start();
    }

    private void stop() {
      if (server != null) {
        server.shutdown();
      }
    }

    private void blockUntilShutdown() throws InterruptedException {
      if (server != null) {
        server.awaitTermination();
      }
    }
  }
}
