package org.opencloudengine.users.fharenheit.aspectj;

import kafka.server.KafkaConfig;
import kafka.utils.SystemTime;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

@Aspect
public class KafkaAspect {

    @Pointcut(value = "execution (* kafka.server.KafkaServer.*(..)) && args(config, time)", argNames = "joinPoint,config,time")
    public void kafkaServerConstruct(JoinPoint joinPoint, KafkaConfig config, SystemTime time) {

    }

    @After(value = "kafkaServerConstruct(joinPoint, config, time)", argNames = "joinPoint,config,time")
    public void message(JoinPoint joinPoint, KafkaConfig config, SystemTime time) {
        System.out.println("---------------------------------------");
        System.out.println(config.getClass().getName());
    }

/*
    @Around("execution(* kafka.server.KafkaServer.new(...)")
    public Object interceptAndLog(ProceedingJoinPoint invocation) throws Throwable {
        try {
            System.out.println(".....");
            return invocation.proceed();
        } catch (Exception e) {
            throw e;
        }
    }
*/

}
