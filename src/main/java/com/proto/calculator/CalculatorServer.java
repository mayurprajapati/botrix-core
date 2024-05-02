package com.proto.calculator;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;

import java.io.IOException;

import com.proto.calculator.CalculatorServiceGrpc.CalculatorServiceImplBase;

public class CalculatorServer {
	public static void main(String[] args) throws IOException, InterruptedException {
		int port = 50052;
		Server server = ServerBuilder.forPort(port).addService(new CalculatorServiceImpl()).build();
		server.start();
		System.out.println("server started. Listening on port : " + port);
		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			System.out.println("Received shutdown request.");
			server.shutdown();
			System.out.println("server stopped.");
		}));
		server.awaitTermination();
	}

	public static class CalculatorServiceImpl extends CalculatorServiceImplBase {
		@Override
		public void sum(SumRequest request, StreamObserver<SumResponse> responseObserver) {
			SumResponse res = SumResponse.newBuilder().setResult(request.getFirstNumber() + request.getSecondNumber())
					.build();
			responseObserver.onNext(res);
			responseObserver.onCompleted();
		}
	}
}