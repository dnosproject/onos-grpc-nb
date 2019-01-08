package org.onosproject.incubator.protobuf.models.net.link;

import org.onosproject.grpc.net.link.models.LinkEnumsProto;
import org.onosproject.grpc.net.link.models.LinkEventProto;
import org.onosproject.grpc.net.models.ConnectPointProtoOuterClass;
import org.onosproject.grpc.net.models.LinkProtoOuterClass;
import org.onosproject.net.link.LinkEvent;

public class LinkNotificationProtoTranslator {

    /**
     * Returns the specific Kafka Device Event Type for the corresponding ONOS
     * Device Event Type.
     *
     * @param event ONOS Device Event
     * @return Kafka Device Event Type
     */
    private static LinkEnumsProto.LinkEventTypeProto getProtoType(LinkEvent event) {
        LinkEnumsProto.LinkEventTypeProto generatedEventType = null;
        LinkEnumsProto.LinkEventTypeProto[] kafkaEvents = LinkEnumsProto.LinkEventTypeProto.values();
        for (LinkEnumsProto.LinkEventTypeProto linkEventType : kafkaEvents) {
            if (linkEventType.name().equals(event.type().name())) {
                generatedEventType = linkEventType;
            }
        }

        return generatedEventType;
    }



    public static LinkEventProto.LinkNotificationProto translate(LinkEvent linkEvent)
    {

        LinkEventProto.LinkNotificationProto notification = LinkEventProto.LinkNotificationProto.newBuilder()
                .setLinkEventType(getProtoType(linkEvent))
                .setLink(LinkProtoOuterClass.LinkProto.newBuilder()
                        .setState(LinkEnumsProto.LinkStateProto.ACTIVE
                                .valueOf(linkEvent.subject().state().name()))
                        .setType(LinkEnumsProto.LinkTypeProto.valueOf(linkEvent.subject().type().name()))
                        .setDst(ConnectPointProtoOuterClass.ConnectPointProto.newBuilder()
                                .setDeviceId(linkEvent.subject().dst()
                                        .deviceId().toString())
                                .setPortNumber(linkEvent.subject().dst().port()
                                        .toString()))
                        .setSrc(ConnectPointProtoOuterClass.ConnectPointProto.newBuilder()
                                .setDeviceId(linkEvent.subject().src()
                                        .deviceId().toString())
                                .setPortNumber(linkEvent.subject().src().port()
                                        .toString())))
                .build();

        return notification;

    }
}
