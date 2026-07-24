package co.com.pragma.usecase.linkcapabilitytechnologies;

import java.util.List;
import java.util.Map;

import co.com.pragma.model.capabilitytechnology.CapabilityTechnology;
import co.com.pragma.model.capabilitytechnology.LinkCapabilityTechnologies;
import co.com.pragma.model.capabilitytechnology.command.LinkCapabilityTechnologiesCommand;
import co.com.pragma.model.capabilitytechnology.gateways.CapabilityTechnologyRepository;
import co.com.pragma.model.common.FieldConstants;
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
        return Mono.fromCallable(() -> LinkCapabilityTechnologies.builder()
                        .capabilityId(command.capabilityId())
                        .technologyIds(command.technologyIds())
                        .build())
                .flatMap(request -> technologyRepository.findMissingIds(request.getTechnologyIds().value())
                        .flatMap(missingIds -> missingIds.isEmpty()
                                ? capabilityTechnologyRepository.saveAll(request)
                                : Mono.error(new TechnologiesNotFoundException(
                                        FunctionalMessageConstants.BUSINESS_VALIDATION_FAILED,
                                        Map.of(FieldConstants.TECHNOLOGY_IDS,
                                                String.format(FunctionalMessageConstants.TECHNOLOGIES_NOT_FOUND, missingIds))))));
    }
}
