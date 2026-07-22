package co.com.pragma.usecase.registertechnology;

import java.util.Map;

import co.com.pragma.model.common.FieldConstants;
import co.com.pragma.model.exceptions.constant.FunctionalMessageConstants;
import co.com.pragma.model.technology.Technology;
import co.com.pragma.model.technology.command.TechnologyCreateCommand;
import co.com.pragma.model.technology.exceptions.TechnologyAlreadyExistsException;
import co.com.pragma.model.technology.gateways.TechnologyRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class RegisterTechnologyUseCase {

        private final TechnologyRepository technologyRepository;

        public Mono<Technology> execute(TechnologyCreateCommand command) {
                return Mono.just(command)
                                .map(cmd -> Technology.builder()
                                                .name(cmd.name())
                                                .description(cmd.description())
                                                .build())
                                .flatMap(technology -> technologyRepository.existsByName(technology.getName().value())
                                                .flatMap(exists -> {
                                                        boolean nameExists = exists;
                                                        return nameExists
                                                                        ? Mono.error(new TechnologyAlreadyExistsException(
                                                                                        FunctionalMessageConstants.BUSINESS_VALIDATION_FAILED,
                                                                                        Map.of(FieldConstants.NAME,
                                                                                                        FunctionalMessageConstants.TECHNOLOGY_ALREADY_EXISTS)))
                                                                        : technologyRepository.save(technology);
                                                }));
        }
}
