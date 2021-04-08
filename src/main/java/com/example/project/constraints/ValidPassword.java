package com.example.project.constraints;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = PasswordConstraintValidator.class)
@Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidPassword {
    String message() default "*Parola invalida!";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}