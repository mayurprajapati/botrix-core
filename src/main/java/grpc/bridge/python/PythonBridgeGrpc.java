package grpc.bridge.python;

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
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.4.0)",
    comments = "Source: python_bridge.proto")
public final class PythonBridgeGrpc {

  private PythonBridgeGrpc() {}

  public static final String SERVICE_NAME = "PythonBridge";

  // Static method descriptors that strictly reflect the proto.
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static final io.grpc.MethodDescriptor<grpc.bridge.python.DriverDebugAddressRequest,
      grpc.bridge.python.CommonResponse> METHOD_RESOLVE_RECAPTCHA =
      io.grpc.MethodDescriptor.<grpc.bridge.python.DriverDebugAddressRequest, grpc.bridge.python.CommonResponse>newBuilder()
          .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
          .setFullMethodName(generateFullMethodName(
              "PythonBridge", "resolveRecaptcha"))
          .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              grpc.bridge.python.DriverDebugAddressRequest.getDefaultInstance()))
          .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              grpc.bridge.python.CommonResponse.getDefaultInstance()))
          .build();

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static PythonBridgeStub newStub(io.grpc.Channel channel) {
    return new PythonBridgeStub(channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static PythonBridgeBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    return new PythonBridgeBlockingStub(channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static PythonBridgeFutureStub newFutureStub(
      io.grpc.Channel channel) {
    return new PythonBridgeFutureStub(channel);
  }

  /**
   */
  public static abstract class PythonBridgeImplBase implements io.grpc.BindableService {

    /**
     */
    public void resolveRecaptcha(grpc.bridge.python.DriverDebugAddressRequest request,
        io.grpc.stub.StreamObserver<grpc.bridge.python.CommonResponse> responseObserver) {
      asyncUnimplementedUnaryCall(METHOD_RESOLVE_RECAPTCHA, responseObserver);
    }

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            METHOD_RESOLVE_RECAPTCHA,
            asyncUnaryCall(
              new MethodHandlers<
                grpc.bridge.python.DriverDebugAddressRequest,
                grpc.bridge.python.CommonResponse>(
                  this, METHODID_RESOLVE_RECAPTCHA)))
          .build();
    }
  }

  /**
   */
  public static final class PythonBridgeStub extends io.grpc.stub.AbstractStub<PythonBridgeStub> {
    private PythonBridgeStub(io.grpc.Channel channel) {
      super(channel);
    }

    private PythonBridgeStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected PythonBridgeStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new PythonBridgeStub(channel, callOptions);
    }

    /**
     */
    public void resolveRecaptcha(grpc.bridge.python.DriverDebugAddressRequest request,
        io.grpc.stub.StreamObserver<grpc.bridge.python.CommonResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(METHOD_RESOLVE_RECAPTCHA, getCallOptions()), request, responseObserver);
    }
  }

  /**
   */
  public static final class PythonBridgeBlockingStub extends io.grpc.stub.AbstractStub<PythonBridgeBlockingStub> {
    private PythonBridgeBlockingStub(io.grpc.Channel channel) {
      super(channel);
    }

    private PythonBridgeBlockingStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected PythonBridgeBlockingStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new PythonBridgeBlockingStub(channel, callOptions);
    }

    /**
     */
    public grpc.bridge.python.CommonResponse resolveRecaptcha(grpc.bridge.python.DriverDebugAddressRequest request) {
      return blockingUnaryCall(
          getChannel(), METHOD_RESOLVE_RECAPTCHA, getCallOptions(), request);
    }
  }

  /**
   */
  public static final class PythonBridgeFutureStub extends io.grpc.stub.AbstractStub<PythonBridgeFutureStub> {
    private PythonBridgeFutureStub(io.grpc.Channel channel) {
      super(channel);
    }

    private PythonBridgeFutureStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected PythonBridgeFutureStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new PythonBridgeFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<grpc.bridge.python.CommonResponse> resolveRecaptcha(
        grpc.bridge.python.DriverDebugAddressRequest request) {
      return futureUnaryCall(
          getChannel().newCall(METHOD_RESOLVE_RECAPTCHA, getCallOptions()), request);
    }
  }

  private static final int METHODID_RESOLVE_RECAPTCHA = 0;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final PythonBridgeImplBase serviceImpl;
    private final int methodId;

    MethodHandlers(PythonBridgeImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_RESOLVE_RECAPTCHA:
          serviceImpl.resolveRecaptcha((grpc.bridge.python.DriverDebugAddressRequest) request,
              (io.grpc.stub.StreamObserver<grpc.bridge.python.CommonResponse>) responseObserver);
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

  private static final class PythonBridgeDescriptorSupplier implements io.grpc.protobuf.ProtoFileDescriptorSupplier {
    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return grpc.bridge.python.PythonBridgeOuterClass.getDescriptor();
    }
  }

  private static volatile io.grpc.ServiceDescriptor serviceDescriptor;

  public static io.grpc.ServiceDescriptor getServiceDescriptor() {
    io.grpc.ServiceDescriptor result = serviceDescriptor;
    if (result == null) {
      synchronized (PythonBridgeGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new PythonBridgeDescriptorSupplier())
              .addMethod(METHOD_RESOLVE_RECAPTCHA)
              .build();
        }
      }
    }
    return result;
  }
}
