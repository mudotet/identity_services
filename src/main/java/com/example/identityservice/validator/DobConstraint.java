package com.example.identityservice.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {DobValidator.class})
public @interface DobConstraint {
  String message() default "DOB_MUST_BE_GREATER_THAN_18_YEARS_AGO";

  int min();

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
