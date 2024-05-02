package com.proto.calculator;

import io.grpc.stub.StreamObserver;

public class CalculatorServiceImpl extends CalculatorServiceGrpc.CalculatorServiceImplBase {
	@Override
	public void sum(SumRequest request, StreamObserver<SumResponse> responseObserver) {
		responseObserver.onNext(
				SumResponse.newBuilder().setResult(request.getFirstNumber() + request.getSecondNumber()).build());
		responseObserver.onCompleted();
	}
}