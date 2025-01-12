package dev.valente.user_service.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = SingleFieldNotNullValidator.class)
public @interface SingleFieldNotNull {
    String message() default "Apenas um campo pode ser não nulo.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
