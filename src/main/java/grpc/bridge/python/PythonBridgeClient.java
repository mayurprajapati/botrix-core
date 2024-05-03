package grpc.bridge.python;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class PythonBridgeClient {
	private static ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 50052).usePlaintext().build();

	public static void resolveRecaptcha(String ip, int port) {
		var stub = PythonBridgeGrpc.newBlockingStub(channel);
		stub.resolveRecaptcha(DriverDebugAddressRequest.newBuilder().setIp(ip).setPort(port).build());
	}
}
