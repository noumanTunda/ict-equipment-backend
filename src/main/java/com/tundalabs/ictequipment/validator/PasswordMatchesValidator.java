package com.tundalabs.ictequipment.validator;
import com.tundalabs.ictequipment.dto.ResetPasswordDto;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordMatchesValidator implements ConstraintValidator<PasswordMatches, ResetPasswordDto> {

    @Override
    public boolean isValid(ResetPasswordDto dto, ConstraintValidatorContext context) {
        if (dto.getNewPassword() == null || dto.getConfirmPassword() == null) {
            return false;
        }

        boolean isValid = dto.getNewPassword().equals(dto.getConfirmPassword());

        if (!isValid) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
                    .addPropertyNode("confirmPassword")
                    .addConstraintViolation();
        }

        return isValid;
    }
}
