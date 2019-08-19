package org.guirsouza;

public class NettyMain {

    public static void main(String[] args) throws Exception {

        System.out.println("Server up.");
        NettyServer server = new NettyServer(10000);
        server.start();
    }


}