import grpc
from concurrent import futures
import temp_pb2
import temp_pb2_grpc


class CalculatorServicer(temp_pb2_grpc.CalculatorServiceServicer):
    def sum(self, request, context):
        result = request.first_number + request.second_number
        return temp_pb2.SumResponse(result=result)


def serve():
    server = grpc.server(futures.ThreadPoolExecutor(max_workers=10))
    temp_pb2_grpc.add_CalculatorServiceServicer_to_server(CalculatorServicer(), server)
    server.add_insecure_port("[::]:50052")
    server.start()
    server.wait_for_termination()


if __name__ == "__main__":
    serve()
