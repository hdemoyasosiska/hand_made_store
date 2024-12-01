package com.example.hm_store.Services;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;


@Aspect
@Component
public class AspectClass {

    public final EmailService emailService = new EmailService();

    private static final Logger log = LoggerFactory.getLogger(AspectClass.class);

    @Pointcut("within(com.example.hm_store.Services.*Service)")
    public void checkAllLogs() {
    }

    @Before("checkAllLogs()")
    public void infoBeforeCheckAllLogs(JoinPoint joinPoint) {
        log.info("Начало выполнения кода "+ joinPoint.getSignature().getName());
    }


    @Pointcut("execution(public void com.example.hm_store.Services.*Service.saveItem(..))")
    public void sendEmailFromService(){}


    @After("sendEmailFromService()")
    public void reportSendingEmail(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();

        if (args != null && args.length > 0) {
            Object firstArgument = args[0];
            emailService.sendEmail("Object is saved\n" + firstArgument.toString());
        }
    }
    @After("checkAllLogs()")
    public void infoFromCheckAllLogs(JoinPoint joinPoint) {
        log.info("Конец выполнения кода " + joinPoint.getSignature().getName());
    }
}
