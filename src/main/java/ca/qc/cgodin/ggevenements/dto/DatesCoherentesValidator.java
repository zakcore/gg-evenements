package ca.qc.cgodin.ggevenements.dto;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class DatesCoherentesValidator implements ConstraintValidator<DatesCoherentes, EvenementForm> {

    @Override
    public boolean isValid(EvenementForm form, ConstraintValidatorContext context) {
        if (form == null || form.getDateDebut() == null || form.getDateFin() == null) {
            return true; // les @NotNull des champs s'en chargent
        }
        if (form.getDateFin().isAfter(form.getDateDebut())) {
            return true;
        }
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
                .addPropertyNode("dateFin")
                .addConstraintViolation();
        return false;
    }
}
