package co.com.pragma.usecase.checktechnologiesexistence;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import co.com.pragma.model.common.FieldConstants;
import co.com.pragma.model.common.ValidationMessageConstants;
import co.com.pragma.model.common.validator.FieldValidator;
import co.com.pragma.model.exceptions.FieldsValidationException;
import co.com.pragma.model.technology.gateways.TechnologyRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class CheckTechnologiesExistenceUseCase {

    private final TechnologyRepository technologyRepository;

    public Mono<List<Long>> execute(List<Long> technologyIds) {
        Map<String, String> errors = collectFieldFormatErrors(technologyIds);

        if (!errors.isEmpty())
            return Mono.error(new FieldsValidationException(errors));

        return technologyRepository.findMissingIds(technologyIds);
    }

    private Map<String, String> collectFieldFormatErrors(List<Long> technologyIds) {
        Map<String, String> errors = new LinkedHashMap<>();
        FieldValidator.validateNotEmpty(technologyIds, FieldConstants.TECHNOLOGY_IDS,
                ValidationMessageConstants.MSG_TECHNOLOGY_IDS_REQUIRED, errors);
        return errors;
    }
}
