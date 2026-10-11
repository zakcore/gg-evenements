package ca.qc.cgodin.ggevenements.dto;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** Contrainte de classe : la date de fin doit être postérieure à la date de début (erreur rattachée à dateFin). */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = DatesCoherentesValidator.class)
public @interface DatesCoherentes {

    String message() default "La date de fin doit être postérieure à la date de début.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
