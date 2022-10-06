package be.optis.opticketapi.validators;

import be.optis.opticketapi.dtos.RegisterCredentials;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class PasswordsEqualConstraintValidator implements ConstraintValidator<PasswordsEqualConstraint, Object> {

    @Override
    public void initialize(PasswordsEqualConstraint arg0) {
    }

    @Override
    public boolean isValid(Object candidate, ConstraintValidatorContext arg1) {
        RegisterCredentials registerCredentials = (RegisterCredentials) candidate;
        if (registerCredentials.getPassword() == null || registerCredentials.getRepeatPassword() == null)
            return false;
        return registerCredentials.getPassword().equals(registerCredentials.getRepeatPassword());
    }
}