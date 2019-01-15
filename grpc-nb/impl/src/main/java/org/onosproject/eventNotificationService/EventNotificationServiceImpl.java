package org.onosproject.impl.eventNotificationService;

import io.grpc.stub.StreamObserver;
import org.onosproject.grpc.net.link.models.LinkEventProto;
import org.onosproject.grpc.net.packet.models.PacketContextProtoOuterClass;
import org.onosproject.grpc.net.models.EventNotificationGrpc.EventNotificationImplBase;
import org.onosproject.grpc.net.models.ServicesProto;
import org.onosproject.grpc.net.models.ServicesProto.Notification;
import org.onosproject.grpc.net.models.ServicesProto.RegistrationRequest;
import org.onosproject.grpc.net.models.ServicesProto.RegistrationResponse;
import org.onosproject.grpc.net.models.ServicesProto.Topic;
import org.onosproject.incubator.protobuf.models.net.link.LinkNotificationProtoTranslator;
import org.onosproject.incubator.protobuf.models.net.packet.PacketContextProtoTranslator;
import org.onosproject.net.link.LinkEvent;
import org.onosproject.net.link.LinkListener;
import org.onosproject.net.link.LinkService;
import org.onosproject.net.packet.PacketContext;
import org.onosproject.net.packet.PacketProcessor;
import org.onosproject.net.packet.PacketService;
import org.osgi.service.component.annotations.*;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.slf4j.LoggerFactory.getLogger;

@Component(immediate = true, service = EventNotificationInterface.class)
public class EventNotificationServiceImpl extends EventNotificationImplBase
        implements EventNotificationInterface {

    protected static Map<String, StreamObserver<Notification>> observerMap = new HashMap<>();
    protected static Set<String> clientList = new HashSet<>();
    private final Logger log = getLogger(getClass());
    private final InternalPacketProcessor packetListener = new InternalPacketProcessor();
    private final LinkListener linkListener = new InternalLinkListener();
    @Reference(cardinality = ReferenceCardinality.MANDATORY)
    protected PacketService packetService;


    @Reference(cardinality = ReferenceCardinality.MANDATORY)
    protected LinkService linkService;



    ExecutorService executorService = Executors.newFixedThreadPool(1);



    @Activate
    protected void activate() {

        log.info("Event Notification Service has been activated");
        packetService.addProcessor(packetListener, PacketProcessor.director(11));
        linkService.addListener(linkListener);
    }

    @Deactivate
    protected void deactivate() {
        log.info("Packet Event Service has been deactivated");
        packetService.removeProcessor(packetListener);
        linkService.removeListener(linkListener);
    }

    @Override
    public void register(
            RegistrationRequest registrationRequest,
            StreamObserver<RegistrationResponse> observer) {

        log.info("registration request has been recevied");
        RegistrationResponse registrationResponse =
                RegistrationResponse.newBuilder()
                        .setClientId(registrationRequest.getClientId())
                        .setServerId("grpc-nb")
                        .build();

        clientList.add(registrationRequest.getClientId());
        observer.onNext(registrationResponse);
        observer.onCompleted();
    }



    @Override
    public void onEvent(Topic topic, StreamObserver<Notification> observer) {

        observerMap.put(topic.getClientId() + topic.getType(), observer);

    }

    class InternalPacketProcessor implements PacketProcessor {

        @Override
        public void process(PacketContext context) {

            if (context == null) {
                log.error("Packet context is null");
                return;
            }

            PacketContextProtoOuterClass.PacketContextProto packetContextProto =
                    PacketContextProtoTranslator.translate(context);

            for (String clientId : clientList) {
                String key = clientId + ServicesProto.topicType.PACKET_EVENT.toString();
                Notification notification =
                        Notification.newBuilder()
                                .setClientId(clientId)
                                .setPacketContext(packetContextProto)
                                .build();
                if (observerMap.containsKey(key)) {
                    Runnable runnable =
                            () -> {
                                observerMap.get(key).onNext(notification);
                            };

                    executorService.execute(runnable);



                }
            }
        }
    }
    private class InternalLinkListener implements LinkListener {

        @Override
        public void event(LinkEvent event) {

            LinkEventProto.LinkNotificationProto linkNotificationProto =
                    LinkNotificationProtoTranslator.translate(event);

            for (String clientId : clientList) {
                String key = clientId + ServicesProto.topicType.LINK_EVENT.toString();
                Notification notification =
                        Notification.newBuilder()
                                .setClientId(clientId)
                                .setLinkEvent(linkNotificationProto)
                                .build();
                if (observerMap.containsKey(key)) {
                    Runnable runnable =
                            () -> {
                                observerMap.get(key).onNext(notification);
                            };

                    //log.info(packetContextProto.getInboundPacket().getConnectPoint().getDeviceId());
                    executorService.execute(runnable);



                }
            }




        }
    }
}
