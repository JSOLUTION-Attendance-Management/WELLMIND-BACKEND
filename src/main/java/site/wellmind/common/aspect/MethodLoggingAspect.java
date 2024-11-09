package site.wellmind.common.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.springframework.stereotype.Component;

/**
 * MethodLoggingAspect
 * <p>서비스나 컨트롤러의 메서드 수준에서 애플리케이션의 비즈니스 로직에 대한 자세한 로깅</p>
 * @author Yuri Seok(tjrdbfl)
 * @version 1.0
 * @since 2024-11-09
 */
@Slf4j
@Aspect
@Component
public class MethodLoggingAspect {
    @Before("execution(* site.wellmind.user.controller..*(..)) " +
            "|| execution(* site.wellmind.user.service..*(..)) " +
            "|| execution(* site.wellmind.transfer.controller..*(..)) " +
            "|| execution(* site.wellmind.transfer.service..*(..)) " +
            "|| execution(* site.wellmind.ai.controller..*(..)) " +
            "|| execution(* site.wellmind.ai.service..*(..)) " +
            "|| execution(* site.wellmind.attend.controller..*(..)) " +
            "|| execution(* site.wellmind.attend.service..*(..)) " +
            "|| execution(* site.wellmind.log.controller..*(..)) " +
            "|| execution(* site.wellmind.log.service..*(..)) "
    )
    public void logMethodEntry(JoinPoint joinPoint){
        log.info("Entering method : {} with arguments : {}",joinPoint.getSignature(),joinPoint.getArgs());
    }
    @AfterReturning(pointcut = "execution(* site.wellmind.user.controller..*(..)) " +
            "|| execution(* site.wellmind.user.service..*(..)) " +
            "|| execution(* site.wellmind.transfer.controller..*(..)) " +
            "|| execution(* site.wellmind.transfer.service..*(..)) " +
            "|| execution(* site.wellmind.ai.controller..*(..)) " +
            "|| execution(* site.wellmind.ai.service..*(..)) " +
            "|| execution(* site.wellmind.attend.controller..*(..)) " +
            "|| execution(* site.wellmind.attend.service..*(..)) " +
            "|| execution(* site.wellmind.log.controller..*(..)) " +
            "|| execution(* site.wellmind.log.service..*(..)) "
            , returning = "result")
    public void logMethodExit(JoinPoint joinPoint, Object result) {
        log.info("Exiting method: {} with result: {}", joinPoint.getSignature(), result);
    }

    @AfterThrowing(pointcut = "execution(* site.wellmind.user.controller..*(..)) " +
            "|| execution(* site.wellmind.user.service..*(..)) " +
            "|| execution(* site.wellmind.transfer.controller..*(..)) " +
            "|| execution(* site.wellmind.transfer.service..*(..)) " +
            "|| execution(* site.wellmind.ai.controller..*(..)) " +
            "|| execution(* site.wellmind.ai.service..*(..)) " +
            "|| execution(* site.wellmind.attend.controller..*(..)) " +
            "|| execution(* site.wellmind.attend.service..*(..)) " +
            "|| execution(* site.wellmind.log.controller..*(..)) " +
            "|| execution(* site.wellmind.log.service..*(..)) "
            , throwing = "exception")
    public void logMethodException(JoinPoint joinPoint, Throwable exception) {
        log.error("Exception in method: {} with message: {}", joinPoint.getSignature(), exception.getMessage());
    }
}




