package org.opencloudengine.users.fharenheit.aspectj;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

@Aspect
public class KafkaAspect {

    @Around("execution(* kafka.server.KafkaServer.new(...)")
    public Object interceptAndLog(ProceedingJoinPoint invocation) throws Throwable {
        try {
            System.out.println(".....");
            return invocation.proceed();
        } catch (Exception e) {
            throw e;
        }
    }

}
