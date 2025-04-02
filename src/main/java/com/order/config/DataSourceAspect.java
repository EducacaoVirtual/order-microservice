package com.order.config;

import com.order.domain.enums.DataSourceType;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Aspect
@Component
@Order(1)
public class DataSourceAspect {
    @Around("@annotation(transactional)")
    public Object around(ProceedingJoinPoint joinPoint, Transactional transactional) throws Throwable {
        try {
            if (transactional.readOnly()) {
                DataSourceContextHolder.setDataSourceType(DataSourceType.REPLICA);
            } else {
                DataSourceContextHolder.setDataSourceType(DataSourceType.MAIN);
            }

            return joinPoint.proceed();
        } finally {
            DataSourceContextHolder.clearDataSourceType();
        }
    }
}
