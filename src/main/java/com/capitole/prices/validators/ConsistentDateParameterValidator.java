package com.capitole.prices.validators;

import com.capitole.prices.validators.anotations.ConsistentDateParameters;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.constraintvalidation.SupportedValidationTarget;
import javax.validation.constraintvalidation.ValidationTarget;
import java.time.LocalDateTime;

import static com.capitole.prices.utils.ConstantsUtils.ILEGAL_PARAMETER_DATE_ERROR;


@SupportedValidationTarget(ValidationTarget.PARAMETERS)
public class ConsistentDateParameterValidator implements ConstraintValidator<ConsistentDateParameters, Object[]> {

    @Override
    public boolean isValid(Object[] value, ConstraintValidatorContext context) {
        if (value[0] == null) {
            return false;
        }

        if (!(value[0] instanceof LocalDateTime)) {
            throw new IllegalArgumentException(ILEGAL_PARAMETER_DATE_ERROR);
        }else return true;

    }
}