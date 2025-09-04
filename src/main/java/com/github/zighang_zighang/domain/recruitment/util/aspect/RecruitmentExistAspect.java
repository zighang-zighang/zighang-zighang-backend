package com.github.zighang_zighang.domain.recruitment.util.aspect;

import com.github.zighang_zighang.domain.recruitment.exception.RecruitmentExceptions;
import com.github.zighang_zighang.domain.recruitment.repository.RecruitmentRepository;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.UUID;

@Aspect
@Component
@RequiredArgsConstructor
public class RecruitmentExistAspect {

    private final SpelExpressionParser parser;

    private final RecruitmentRepository recruitmentRepository;

    @Around("@annotation(recruitmentExist)")
    public Object guard(ProceedingJoinPoint joinPoint, RecruitmentExist recruitmentExist) throws Throwable {

        UUID recruitmentId = extractRecruitmentId(joinPoint, recruitmentExist.value());

        if (recruitmentRepository.findById(recruitmentId).isEmpty()) {

            throw RecruitmentExceptions.NOT_FOUND.toException();
        }

        return joinPoint.proceed();
    }

    private UUID extractRecruitmentId(ProceedingJoinPoint joinPoint, String expression) {

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        Parameter[] parameters = method.getParameters();
        Object[] args = joinPoint.getArgs();

        EvaluationContext context = new StandardEvaluationContext();

        for (int i = 0; i < parameters.length; i++) {

            context.setVariable(parameters[i].getName(), args[i]);
        }

        return (UUID) parser.parseExpression(expression).getValue(context);
    }
}