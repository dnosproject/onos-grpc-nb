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

import com.google.protobuf.ByteString;
import org.onosproject.grpc.net.models.ConnectPointProtoOuterClass;
import org.onosproject.grpc.net.packet.models.InboundPacketProtoOuterClass.InboundPacketProto;
import org.onosproject.incubator.protobuf.models.net.ConnectPointProtoTranslator;
import org.onosproject.net.packet.InboundPacket;

/**
 * gRPC InboundPacket message to org.onosproject.net.InboundPacket conversion related utilities.
 */
public final class InboundPacketProtoTranslator {

    public static InboundPacketProto translate(InboundPacket inboundPacket) {

        ConnectPointProtoOuterClass.ConnectPointProto connectPointProto =
                ConnectPointProtoTranslator.translate(inboundPacket.receivedFrom());

        InboundPacketProto.Builder inboundPacketBuilder =
                InboundPacketProto.newBuilder();
        inboundPacketBuilder.setConnectPoint(connectPointProto)
                .setData(ByteString.copyFrom(inboundPacket.unparsed()));

        return inboundPacketBuilder.build();
    }

    private InboundPacketProtoTranslator() {};
}
