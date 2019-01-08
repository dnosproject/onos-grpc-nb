package org.onosproject.impl.packetOutService;

import io.grpc.stub.StreamObserver;
import org.onlab.osgi.DefaultServiceDirectory;
import org.onosproject.grpc.net.packet.models.OutboundPacketProtoOuterClass.OutboundPacketProto;
import org.onosproject.grpc.net.models.PacketOutServiceGrpc.PacketOutServiceImplBase;
import org.onosproject.grpc.net.models.ServicesProto.PacketOutStatus;
import org.onosproject.incubator.protobuf.models.net.packet.OutboundPacketProtoTranslator;
import org.onosproject.net.packet.OutboundPacket;
import org.onosproject.net.packet.PacketService;
import org.osgi.service.component.annotations.*;
import org.slf4j.Logger;

import static org.slf4j.LoggerFactory.getLogger;

@Component(immediate = true, service = PacketOutInterface.class)
public class PacketOutServiceImpl
        extends PacketOutServiceImplBase
        implements PacketOutInterface {
  private final Logger log = getLogger(getClass());

  @Reference(cardinality = ReferenceCardinality.MANDATORY)
  protected PacketService packetService;

  @Activate
  protected void activate() {

    log.info("Packetout Service has been activated");
  }

  @Deactivate
  protected void deactivate() {
    log.info("deactivated");
  }

  @Override
  public void emit(OutboundPacketProto request, StreamObserver<PacketOutStatus> responseObserver) {

    OutboundPacket outboundPacket = OutboundPacketProtoTranslator.translate(request);

    packetService = DefaultServiceDirectory.getService(PacketService.class);
    packetService.emit(outboundPacket);

    PacketOutStatus reply = PacketOutStatus.newBuilder().setStat(true).build();
    responseObserver.onNext(reply);
    responseObserver.onCompleted();
  }
}
