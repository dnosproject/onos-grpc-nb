/*
 * Copyright 2018-present Open Networking Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.onosproject.incubator.protobuf.models.net.packet;

import org.onosproject.grpc.net.packet.models.InboundPacketProtoOuterClass;
import org.onosproject.grpc.net.packet.models.OutboundPacketProtoOuterClass;
import org.onosproject.grpc.net.packet.models.PacketContextProtoOuterClass.PacketContextProto;
import org.onosproject.net.packet.DefaultOutboundPacket;
import org.onosproject.net.packet.DefaultPacketContext;
import org.onosproject.net.packet.OutboundPacket;
import org.onosproject.net.packet.PacketContext;

/**
 * gRPC PacketContext message to org.onosproject.net.PacketContext conversion related utilities.
 */
public final class PacketContextProtoTranslator {

    public static PacketContextProto translate(PacketContext context) {
        PacketContextProto.Builder packetContextBuilder = PacketContextProto.newBuilder();
        InboundPacketProtoOuterClass.InboundPacketProto inboundPacketProto =
                InboundPacketProtoTranslator.translate(context.inPacket());
        OutboundPacketProtoOuterClass.OutboundPacketProto outboundPacketProto =
                OutboundPacketProtoTranslator.translate(context.outPacket());
        packetContextBuilder.setInboundPacket(inboundPacketProto)
                .setOutboundPacket(outboundPacketProto)
                .setTime(context.time());

        return packetContextBuilder.build();
    }



    private PacketContextProtoTranslator() {};

}
