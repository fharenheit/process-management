package org.opencloudengine.users.fharenheit.bm;

import kafka.server.KafkaServer;
import org.opencloudengine.users.fharenheit.SystemUtils;

import javax.management.*;
import java.lang.management.ManagementFactory;
import java.util.Iterator;
import java.util.Set;

public class KafkaAgent {

    public static void start(KafkaServer server) throws IntrospectionException, InstanceNotFoundException, ReflectionException, AttributeNotFoundException, MBeanException {
        System.out.println("-----------------------------");
        System.out.println("Kafka Server : " + server.loggerName());
        System.out.println("-----------------------------");

        String pid = SystemUtils.getPid();
        MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
        Set<ObjectInstance> mbeans = mbs.queryMBeans(null, null);

        Iterator<ObjectInstance> iterator = mbeans.iterator();

        while (iterator.hasNext()) {
            ObjectInstance mbean = iterator.next();
            System.out.printf("==============================================");
            System.out.println("Class Name:\t" + mbean.getClassName());
            System.out.println("Object Name:\t" + mbean.getObjectName());

            final MBeanAttributeInfo[] attributes = mbs.getMBeanInfo(mbean.getObjectName()).getAttributes();
            for (final MBeanAttributeInfo attribute : attributes) {
                Object value = mbs.getAttribute(mbean.getObjectName(), attribute.getName());
                System.out.printf("Name : " + attribute.getName());
                System.out.printf("Value : " + value);
                System.out.printf("----------------------------------------");
            }

        }

    }
}
