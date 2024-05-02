package com.proto.calculator;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class CalculatorClient {
	public static void main(String[] args) throws InterruptedException {
		ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 50052).usePlaintext().build();
		doSum(channel);
	}

	private static void doSum(ManagedChannel channel) {
		System.out.println("enter doSum()");
		CalculatorServiceGrpc.CalculatorServiceBlockingStub stub = CalculatorServiceGrpc.newBlockingStub(channel);
		SumResponse response = stub.sum(SumRequest.newBuilder().setFirstNumber(1).setSecondNumber(3).build());
		System.out.println("sum 1+3 = " + response.getResult());
	}
}