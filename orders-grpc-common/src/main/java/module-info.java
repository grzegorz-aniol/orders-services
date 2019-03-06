module orders.grpc.common {
  exports org.gangel.orders.proto;
  requires transitive lombok;
  requires javax.annotation.api;
  requires protobuf.java;
  requires grpc.stub;
  requires grpc.protobuf;
}
