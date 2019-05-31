package server;


import io.grpc.custom.database.CRUDServiceGrpc;
import io.grpc.custom.database.DatabaseProto;
import io.grpc.stub.StreamObserver;


public class CRUDServiceImpl extends CRUDServiceGrpc.CRUDServiceImplBase {

    @Override
    public void create( DatabaseProto.Register request, StreamObserver< DatabaseProto.Result > responseObserver ) {

    }


    @Override
    public void read( DatabaseProto.Register request, StreamObserver< DatabaseProto.Result > responseObserver ) {

    }


    @Override
    public void update( DatabaseProto.Register request, StreamObserver< DatabaseProto.Result > responseObserver ) {

    }


    @Override
    public void delete( DatabaseProto.Register request, StreamObserver< DatabaseProto.Result > responseObserver ) {

    }
}
