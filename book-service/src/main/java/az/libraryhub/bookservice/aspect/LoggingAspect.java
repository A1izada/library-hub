package az.libraryhub.bookservice.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {

    private static final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);

    @Before("execution(* az.libraryhub.bookservice.service.*.*(..))")
    public void logBefore(JoinPoint joinPoint) {
        logger.info("Çağırılır: {} | Parametrlər: {}",
                joinPoint.getSignature().toShortString(),
                joinPoint.getArgs());
    }

    @AfterReturning(pointcut = "execution(* az.libraryhub.bookservice.service.*.*(..))", returning = "result")
    public void logAfterReturning(JoinPoint joinPoint, Object result) {
        logger.info("Uğurla tamamlandı: {} | Nəticə: {}",
                joinPoint.getSignature().toShortString(),
                result);
    }

    @AfterThrowing(pointcut = "execution(* az.libraryhub.bookservice.service.*.*(..))", throwing = "exception")
    public void logAfterThrowing(JoinPoint joinPoint, Throwable exception) {
        logger.error("Xəta baş verdi: {} | Xəta: {}",
                joinPoint.getSignature().toShortString(),
                exception.getMessage());
    }
}

