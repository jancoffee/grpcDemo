package com.arealvision.grpcdemo;


import com.arealvision.grpcdemo.proto.User;
import com.arealvision.grpcdemo.proto.userGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;
import java.util.concurrent.Semaphore;
import java.util.logging.Logger;

public class Main {

    static Semaphore semaphore = new Semaphore(1);

    private static Logger logger = null;

    static Logger getLogger(){
        if (logger == null) {
            logger = Logger.getLogger(Main.class.getName());
        }
        return logger;
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        semaphore.acquire();
        startServerThread();

        //lock until server has started
        semaphore.acquire(); semaphore.release();

        userGrpc.userBlockingStub userBlockingStub = startClient();

        makeLoginRequest(userBlockingStub, "donald", "duck");
        makeLoginRequest(userBlockingStub, "mickey", "mouse");
        makeLoginRequest(userBlockingStub, "pluto", "dog");
    }

    private static void makeLoginRequest(userGrpc.userBlockingStub userBlockingStub, String login, String password) {
        User.LoginRequest.Builder loginReqBuild = User.LoginRequest.newBuilder();
        loginReqBuild.setUsername(login).setPassword(password);

        User.LoginResponds loginResponds = userBlockingStub.login(loginReqBuild.build());

        getLogger().info("login respons:"+loginResponds.getMessage());
    }

    private static userGrpc.userBlockingStub startClient() {
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 9090).usePlaintext().build();
        userGrpc.userBlockingStub userBlockingStub = userGrpc.newBlockingStub(channel);

        return userBlockingStub;
    }

    private static void startServerThread() {
        new Thread(() -> {
            try {
                startServer();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private static void startServer() throws IOException, InterruptedException {
        Server server = ServerBuilder.forPort(9090).addService(new UserServiceImpl()).build();
        server.start();
        getLogger().info("Server started on port:"+server.getPort());
        Thread.sleep(10000);
        semaphore.release();
        server.awaitTermination();
    }
}
