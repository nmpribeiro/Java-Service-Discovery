package com.example.server;

import static io.grpc.stub.ClientCalls.asyncUnaryCall;
import static io.grpc.stub.ClientCalls.asyncServerStreamingCall;
import static io.grpc.stub.ClientCalls.asyncClientStreamingCall;
import static io.grpc.stub.ClientCalls.asyncBidiStreamingCall;
import static io.grpc.stub.ClientCalls.blockingUnaryCall;
import static io.grpc.stub.ClientCalls.blockingServerStreamingCall;
import static io.grpc.stub.ClientCalls.futureUnaryCall;
import static io.grpc.MethodDescriptor.generateFullMethodName;
import static io.grpc.stub.ServerCalls.asyncUnaryCall;
import static io.grpc.stub.ServerCalls.asyncServerStreamingCall;
import static io.grpc.stub.ServerCalls.asyncClientStreamingCall;
import static io.grpc.stub.ServerCalls.asyncBidiStreamingCall;
import static io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall;
import static io.grpc.stub.ServerCalls.asyncUnimplementedStreamingCall;

/**
 * <pre>
 * 4. service, unary request/response
 * </pre>
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.7.0)",
    comments = "Source: ExampleService.proto")
public final class GreetingServiceGrpc {

  private GreetingServiceGrpc() {}

  public static final String SERVICE_NAME = "GreetingService";

  // Static method descriptors that strictly reflect the proto.
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static final io.grpc.MethodDescriptor<com.example.server.HelloRequest,
      com.example.server.HelloResponse> METHOD_GREETING =
      io.grpc.MethodDescriptor.<com.example.server.HelloRequest, com.example.server.HelloResponse>newBuilder()
          .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
          .setFullMethodName(generateFullMethodName(
              "GreetingService", "greeting"))
          .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              com.example.server.HelloRequest.getDefaultInstance()))
          .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              com.example.server.HelloResponse.getDefaultInstance()))
          .setSchemaDescriptor(new GreetingServiceMethodDescriptorSupplier("greeting"))
          .build();

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static GreetingServiceStub newStub(io.grpc.Channel channel) {
    return new GreetingServiceStub(channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static GreetingServiceBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    return new GreetingServiceBlockingStub(channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static GreetingServiceFutureStub newFutureStub(
      io.grpc.Channel channel) {
    return new GreetingServiceFutureStub(channel);
  }

  /**
   * <pre>
   * 4. service, unary request/response
   * </pre>
   */
  public static abstract class GreetingServiceImplBase implements io.grpc.BindableService {

    /**
     */
    public void greeting(com.example.server.HelloRequest request,
        io.grpc.stub.StreamObserver<com.example.server.HelloResponse> responseObserver) {
      asyncUnimplementedUnaryCall(METHOD_GREETING, responseObserver);
    }

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            METHOD_GREETING,
            asyncUnaryCall(
              new MethodHandlers<
                com.example.server.HelloRequest,
                com.example.server.HelloResponse>(
                  this, METHODID_GREETING)))
          .build();
    }
  }

  /**
   * <pre>
   * 4. service, unary request/response
   * </pre>
   */
  public static final class GreetingServiceStub extends io.grpc.stub.AbstractStub<GreetingServiceStub> {
    private GreetingServiceStub(io.grpc.Channel channel) {
      super(channel);
    }

    private GreetingServiceStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected GreetingServiceStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new GreetingServiceStub(channel, callOptions);
    }

    /**
     */
    public void greeting(com.example.server.HelloRequest request,
        io.grpc.stub.StreamObserver<com.example.server.HelloResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(METHOD_GREETING, getCallOptions()), request, responseObserver);
    }
  }

  /**
   * <pre>
   * 4. service, unary request/response
   * </pre>
   */
  public static final class GreetingServiceBlockingStub extends io.grpc.stub.AbstractStub<GreetingServiceBlockingStub> {
    private GreetingServiceBlockingStub(io.grpc.Channel channel) {
      super(channel);
    }

    private GreetingServiceBlockingStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected GreetingServiceBlockingStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new GreetingServiceBlockingStub(channel, callOptions);
    }

    /**
     */
    public com.example.server.HelloResponse greeting(com.example.server.HelloRequest request) {
      return blockingUnaryCall(
          getChannel(), METHOD_GREETING, getCallOptions(), request);
    }
  }

  /**
   * <pre>
   * 4. service, unary request/response
   * </pre>
   */
  public static final class GreetingServiceFutureStub extends io.grpc.stub.AbstractStub<GreetingServiceFutureStub> {
    private GreetingServiceFutureStub(io.grpc.Channel channel) {
      super(channel);
    }

    private GreetingServiceFutureStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected GreetingServiceFutureStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new GreetingServiceFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.example.server.HelloResponse> greeting(
        com.example.server.HelloRequest request) {
      return futureUnaryCall(
          getChannel().newCall(METHOD_GREETING, getCallOptions()), request);
    }
  }

  private static final int METHODID_GREETING = 0;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final GreetingServiceImplBase serviceImpl;
    private final int methodId;

    MethodHandlers(GreetingServiceImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_GREETING:
          serviceImpl.greeting((com.example.server.HelloRequest) request,
              (io.grpc.stub.StreamObserver<com.example.server.HelloResponse>) responseObserver);
          break;
        default:
          throw new AssertionError();
      }
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public io.grpc.stub.StreamObserver<Req> invoke(
        io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        default:
          throw new AssertionError();
      }
    }
  }

  private static abstract class GreetingServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    GreetingServiceBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return com.example.server.ExampleService.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("GreetingService");
    }
  }

  private static final class GreetingServiceFileDescriptorSupplier
      extends GreetingServiceBaseDescriptorSupplier {
    GreetingServiceFileDescriptorSupplier() {}
  }

  private static final class GreetingServiceMethodDescriptorSupplier
      extends GreetingServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final String methodName;

    GreetingServiceMethodDescriptorSupplier(String methodName) {
      this.methodName = methodName;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.MethodDescriptor getMethodDescriptor() {
      return getServiceDescriptor().findMethodByName(methodName);
    }
  }

  private static volatile io.grpc.ServiceDescriptor serviceDescriptor;

  public static io.grpc.ServiceDescriptor getServiceDescriptor() {
    io.grpc.ServiceDescriptor result = serviceDescriptor;
    if (result == null) {
      synchronized (GreetingServiceGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new GreetingServiceFileDescriptorSupplier())
              .addMethod(METHOD_GREETING)
              .build();
        }
      }
    }
    return result;
  }
}
