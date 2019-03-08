module orders.grpc.client {
  requires orders.common;
  requires orders.grpc.common;
  requires jperfstat;
  //requires javax.annotation.api;
  requires lombok;
  requires protobuf.java;
  requires grpc.netty;
  requires grpc.stub;
  requires commons.cli;
  requires io.netty.handler;
  requires io.netty.codec;
  requires slf4j.api;
  requires jcl.over.slf4j;
  requires jul.to.slf4j;
  requires log4j.over.slf4j;
  requires logback.classic;
  requires logback.core;
}