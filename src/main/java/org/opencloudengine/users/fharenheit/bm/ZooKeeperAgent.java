package org.opencloudengine.users.fharenheit.bm;

import org.apache.zookeeper.server.ZooKeeperServer;

public class ZooKeeperAgent {

    public static void start(ZooKeeperServer server) {
        System.out.println("-----------------------------");
        System.out.println("Server State : " + server.getState());
        System.out.println("-----------------------------");
    }

}
