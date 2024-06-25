cd src/main/resources
python -m grpc_tools.protoc -I. --python_out=..\..\..\pygrpc --grpc_python_out=..\..\..\pygrpc temp.proto
