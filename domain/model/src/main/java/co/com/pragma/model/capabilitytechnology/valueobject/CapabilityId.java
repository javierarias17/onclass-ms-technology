package co.com.pragma.model.capabilitytechnology.valueobject;

import co.com.pragma.model.common.FieldConstants;
import co.com.pragma.model.common.ValidationMessageConstants;
import co.com.pragma.model.common.validator.FieldValidator;
import co.com.pragma.model.exceptions.FieldsValidationException;

import java.util.LinkedHashMap;
import java.util.Map;

public record CapabilityId(Long value) {

    public CapabilityId {
        Map<String, String> errors = new LinkedHashMap<>();
        validate(value, errors);
        if (!errors.isEmpty())
            throw new FieldsValidationException(errors);
    }

    public static void validate(Long value, Map<String, String> errors) {
        FieldValidator.validateNotNull(value, FieldConstants.CAPABILITY_ID,
                ValidationMessageConstants.MSG_CAPABILITY_ID_REQUIRED, errors);
    }
}
