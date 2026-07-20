package co.com.pragma.usecase.linkcapabilitytechnologies;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import co.com.pragma.model.capabilitytechnology.CapabilityTechnology;
import co.com.pragma.model.capabilitytechnology.LinkCapabilityTechnologiesCommand;
import co.com.pragma.model.capabilitytechnology.gateways.CapabilityTechnologyRepository;
import co.com.pragma.model.common.FieldConstants;
import co.com.pragma.model.common.ValidationMessageConstants;
import co.com.pragma.model.common.validator.FieldValidator;
import co.com.pragma.model.exceptions.FieldsValidationException;
import co.com.pragma.model.exceptions.constant.FunctionalMessageConstants;
import co.com.pragma.model.technology.exceptions.TechnologiesNotFoundException;
import co.com.pragma.model.technology.gateways.TechnologyRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class LinkCapabilityTechnologiesUseCase {

        private final TechnologyRepository technologyRepository;
        private final CapabilityTechnologyRepository capabilityTechnologyRepository;

        public Mono<List<CapabilityTechnology>> execute(LinkCapabilityTechnologiesCommand command) {
                Map<String, String> errors = validate(command);

                if (!errors.isEmpty())
                        return Mono.error(new FieldsValidationException(errors));

                return technologyRepository.findMissingIds(command.technologyIds())
                                .flatMap(missingIds -> missingIds.isEmpty()
                                                ? capabilityTechnologyRepository.saveAll(command.capabilityId(),
                                                                command.technologyIds())
                                                : Mono.error(new TechnologiesNotFoundException(
                                                                FunctionalMessageConstants.BUSINESS_VALIDATION_FAILED,
                                                                Map.of(FieldConstants.TECHNOLOGY_IDS,
                                                                                String.format(FunctionalMessageConstants.TECHNOLOGIES_NOT_FOUND,
                                                                                                missingIds)))));
        }

        private Map<String, String> validate(LinkCapabilityTechnologiesCommand command) {
                Map<String, String> errors = new LinkedHashMap<>();
                FieldValidator.validateNotNull(command.capabilityId(), FieldConstants.CAPABILITY_ID,
                                ValidationMessageConstants.MSG_CAPABILITY_ID_REQUIRED, errors);
                FieldValidator.validateNotEmpty(command.technologyIds(), FieldConstants.TECHNOLOGY_IDS,
                                ValidationMessageConstants.MSG_TECHNOLOGY_IDS_REQUIRED, errors);
                return errors;
        }

}
