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
import org.onosproject.grpc.net.flow.instructions.models.InstructionProtoOuterClass.InstructionProto;
import org.onosproject.grpc.net.packet.models.OutboundPacketProtoOuterClass.OutboundPacketProto;
import org.onosproject.net.DeviceId;
import org.onosproject.net.PortNumber;
import org.onosproject.net.flow.DefaultTrafficTreatment;
import org.onosproject.net.flow.TrafficTreatment;
import org.onosproject.net.packet.DefaultOutboundPacket;
import org.onosproject.net.packet.OutboundPacket;

import java.nio.ByteBuffer;
import java.util.List;

/**
 * gRPC OutboundPacket message to org.onosproject.net.OutboundPacket conversion related utilities.
 */
public final class OutboundPacketProtoTranslator {


    public static OutboundPacketProto translate(OutboundPacket outboundPacket) {
        OutboundPacketProto.Builder outboundPacketBuilder =
                OutboundPacketProto.newBuilder();
        outboundPacketBuilder.setDeviceId(outboundPacket.sendThrough().toString());
        outboundPacketBuilder.setData(ByteString.copyFrom(outboundPacket.data()));


        return  outboundPacketBuilder.build();


    }

    public static OutboundPacket translate(OutboundPacketProto outboundPacketProto)
    {
        DeviceId deviceId = DeviceId.deviceId(outboundPacketProto.getDeviceId());
        TrafficTreatment.Builder trafficTreatmentBuilder = DefaultTrafficTreatment.builder();

        List<InstructionProto> instructionProtoList =
                outboundPacketProto.getTreatment().getAllInstructionsList();

        for(InstructionProto instructionProto: instructionProtoList)
        {
            switch (instructionProto.getType())
            {
                case OUTPUT:
                    trafficTreatmentBuilder.setOutput(PortNumber.portNumber(instructionProto.getPort().getPortNumber()));
            }
        }

        OutboundPacket outboundPacket = new
                DefaultOutboundPacket(deviceId, trafficTreatmentBuilder.build()
                , ByteBuffer.wrap(outboundPacketProto.getData().toByteArray()));

        return  outboundPacket;

    }

    private OutboundPacketProtoTranslator() {};
}
