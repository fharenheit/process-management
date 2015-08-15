package org.opencloudengine.users.fharenheit.aspectj;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;

import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanRegistrationException;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;

@Aspect
public class MonitorAspect {
    final ActorSystemMessages messages;

    public MonitorAspect() throws MalformedObjectNameException, NotCompliantMBeanException, InstanceAlreadyExistsException, MBeanRegistrationException {
        System.out.println("---------------------------------------");
        this.messages = new ActorSystemMessages();
        JMXEndpoint.start(messages);
    }

/*
    @Pointcut(value = "execution (* org.apache.spark.SparkContext.new(..)) && args(config)", argNames = "config")
    public void constructorPointcut(Object config) {}

    @Before(value = "constructorPointcut(config)", argNames = "config")
    public void message(Object config) {
        System.out.println("---------------------------------------");
        System.out.println(config.getClass().getName());
    }
*/
}