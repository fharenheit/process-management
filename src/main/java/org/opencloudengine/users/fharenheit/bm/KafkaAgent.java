package org.opencloudengine.users.fharenheit.bm;

import kafka.server.KafkaServer;

public class KafkaAgent {

    public static void start(KafkaServer server) {
        System.out.println("-----------------------------");
        System.out.println("Server State : " + server.config().hostName());
        System.out.println("-----------------------------");
    }

}
