COMPILE_DEPS = CORE_DEPS + KRYO + JACKSON + [
    "//core/store/dist:onos-core-dist",
    "//core/store/serializers:onos-core-serializers",
    "//core/protobuf/models:onos-core-protobuf-models",
    "//core/protobuf/models/proto:onos-core-protobuf-services-proto",
    "@io_grpc_grpc_java//core",
    "@io_grpc_grpc_java//netty",
    "@io_grpc_grpc_java//stub",
]

osgi_jar(
    deps = COMPILE_DEPS,
)
