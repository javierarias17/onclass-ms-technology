package co.com.pragma.usecase.deletecapabilitytechnologies;

import java.util.LinkedHashMap;
import java.util.Map;

import co.com.pragma.model.capabilitytechnology.gateways.CapabilityTechnologyRepository;
import co.com.pragma.model.common.FieldConstants;
import co.com.pragma.model.common.ValidationMessageConstants;
import co.com.pragma.model.common.validator.FieldValidator;
import co.com.pragma.model.exceptions.FieldsValidationException;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class DeleteCapabilityTechnologiesUseCase {

    private final CapabilityTechnologyRepository capabilityTechnologyRepository;

    public Mono<Void> execute(String capabilityId) {
        Map<String, String> errors = collectFieldFormatErrors(capabilityId);

        if (!errors.isEmpty())
            return Mono.error(new FieldsValidationException(errors));

        return capabilityTechnologyRepository.deleteByCapabilityId(Long.valueOf(capabilityId));
    }

    private Map<String, String> collectFieldFormatErrors(String capabilityId) {
        Map<String, String> errors = new LinkedHashMap<>();
        FieldValidator.validateNotBlank(capabilityId, FieldConstants.CAPABILITY_ID,
                ValidationMessageConstants.MSG_CAPABILITY_ID_REQUIRED, errors);
        FieldValidator.validateNumericFormat(capabilityId, FieldConstants.CAPABILITY_ID,
                ValidationMessageConstants.MSG_CAPABILITY_ID_MUST_BE_NUMERIC, errors);
        return errors;
    }
}
