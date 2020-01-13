package com.arealvision.grpcdemo;

import com.arealvision.grpcdemo.proto.User;
import com.arealvision.grpcdemo.proto.userGrpc.userImplBase;

import java.util.logging.Logger;

public class UserServiceImpl extends userImplBase {
    private static final Logger logger = Logger.getLogger(UserServiceImpl.class.getName());

    @Override
    public void login(User.LoginRequest request, io.grpc.stub.StreamObserver<User.LoginResponds> responseObserver) {
        //super.login(request, responseObserver);
        String username = request.getUsername();
        String password = request.getPassword();
        logger.info(">> login userName:"+ username + " password:" + password);

        User.LoginResponds.Builder loginResponsBuilder = User.LoginResponds.newBuilder();
        loginResponsBuilder.setCode(0).setMessage("login from username:"+username + " password:" + password);

        responseObserver.onNext(loginResponsBuilder.build());
        responseObserver.onCompleted();
    }

    @Override
    public void logout(User.Empty request, io.grpc.stub.StreamObserver<User.LogoutResponds> responseObserver) {
        //super.logout(request, responseObserver);
        logger.info( ">> logout");
        User.LogoutResponds.Builder builder = User.LogoutResponds.newBuilder();
        builder.setCode(0).setMessage("logged out");

        responseObserver.onNext(builder.build());
        responseObserver.onCompleted();
    }
}
