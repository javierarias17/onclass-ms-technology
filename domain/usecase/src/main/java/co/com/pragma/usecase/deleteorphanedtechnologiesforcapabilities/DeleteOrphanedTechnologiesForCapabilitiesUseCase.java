package co.com.pragma.usecase.deleteorphanedtechnologiesforcapabilities;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import co.com.pragma.model.capabilitytechnology.gateways.CapabilityTechnologyRepository;
import co.com.pragma.model.common.FieldConstants;
import co.com.pragma.model.common.ValidationMessageConstants;
import co.com.pragma.model.common.validator.FieldValidator;
import co.com.pragma.model.exceptions.FieldsValidationException;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class DeleteOrphanedTechnologiesForCapabilitiesUseCase {

    private final CapabilityTechnologyRepository capabilityTechnologyRepository;

    public Mono<Void> execute(List<Long> capabilityIds) {
        Map<String, String> errors = collectFieldFormatErrors(capabilityIds);

        if (!errors.isEmpty())
            return Mono.error(new FieldsValidationException(errors));

        return capabilityTechnologyRepository.deleteOrphanedTechnologiesForCapabilities(capabilityIds);
    }

    private Map<String, String> collectFieldFormatErrors(List<Long> capabilityIds) {
        Map<String, String> errors = new LinkedHashMap<>();
        FieldValidator.validateNotEmpty(capabilityIds, FieldConstants.CAPABILITY_IDS,
                ValidationMessageConstants.MSG_CAPABILITY_IDS_REQUIRED, errors);
        return errors;
    }
}
