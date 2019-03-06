module orders.common {
  exports org.gangel.orders.dto;
  exports org.gangel.orders.job;
  exports org.gangel.orders.rnd;
  requires jperfstat;
  requires transitive lombok;
  requires com.fasterxml.jackson.annotation;
}
