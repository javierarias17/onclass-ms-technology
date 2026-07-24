package co.com.pragma.api;

import co.com.pragma.api.constants.PathVariableConstants;
import co.com.pragma.api.dto.CapabilityTechnologyLinkInDto;
import co.com.pragma.api.dto.TechnologiesByCapabilityEntryOutDto;
import co.com.pragma.api.dto.TechnologiesByCapabilityInDto;
import co.com.pragma.api.dto.TechnologiesByCapabilityOutDto;
import co.com.pragma.api.dto.TechnologyExistenceInDto;
import co.com.pragma.api.dto.TechnologyExistenceOutDto;
import co.com.pragma.api.dto.TechnologyInDto;
import co.com.pragma.api.dto.TechnologySummaryOutDto;
import co.com.pragma.api.mapper.CapabilityTechnologyDtoMapper;
import co.com.pragma.api.mapper.TechnologyDtoMapper;
import co.com.pragma.model.technology.query.TechnologySummary;
import co.com.pragma.usecase.checktechnologiesexistence.CheckTechnologiesExistenceUseCase;
import co.com.pragma.usecase.deletecapabilitytechnologies.DeleteCapabilityTechnologiesUseCase;
import co.com.pragma.usecase.findtechnologiesbycapabilityids.FindTechnologiesByCapabilityIdsUseCase;
import co.com.pragma.usecase.linkcapabilitytechnologies.LinkCapabilityTechnologiesUseCase;
import co.com.pragma.usecase.registertechnology.RegisterTechnologyUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class Handler implements IHandlerDocs {

    private final RegisterTechnologyUseCase registerTechnologyUseCase;
    private final CheckTechnologiesExistenceUseCase checkTechnologiesExistenceUseCase;
    private final LinkCapabilityTechnologiesUseCase linkCapabilityTechnologiesUseCase;
    private final DeleteCapabilityTechnologiesUseCase deleteCapabilityTechnologiesUseCase;
    private final FindTechnologiesByCapabilityIdsUseCase findTechnologiesByCapabilityIdsUseCase;
    private final TechnologyDtoMapper technologyDtoMapper;
    private final CapabilityTechnologyDtoMapper capabilityTechnologyDtoMapper;

    @Override
    public Mono<ServerResponse> listenRegisterTechnology(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(TechnologyInDto.class)
                .defaultIfEmpty(new TechnologyInDto(null, null))
                .map(technologyDtoMapper::toTechnologyCreateCommand)
                .flatMap(registerTechnologyUseCase::execute)
                .map(technologyDtoMapper::toTechnologyOutDto)
                .flatMap(response -> ServerResponse.status(HttpStatus.CREATED).bodyValue(response));
    }

    @Override
    public Mono<ServerResponse> listenCheckTechnologiesExistence(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(TechnologyExistenceInDto.class)
                .defaultIfEmpty(new TechnologyExistenceInDto(null))
                .flatMap(dto -> checkTechnologiesExistenceUseCase.execute(dto.technologyIds()))
                .map(TechnologyExistenceOutDto::new)
                .flatMap(response -> ServerResponse.status(HttpStatus.OK).bodyValue(response));
    }

    @Override
    public Mono<ServerResponse> listenLinkCapabilityTechnologies(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(CapabilityTechnologyLinkInDto.class)
                .defaultIfEmpty(new CapabilityTechnologyLinkInDto(null, null))
                .map(capabilityTechnologyDtoMapper::toLinkCapabilityTechnologiesCommand)
                .flatMap(linkCapabilityTechnologiesUseCase::execute)
                .map(capabilityTechnologyDtoMapper::toCapabilityTechnologyLinkOutDto)
                .flatMap(response -> ServerResponse.status(HttpStatus.CREATED).bodyValue(response));
    }

    @Override
    public Mono<ServerResponse> listenDeleteCapabilityTechnologies(ServerRequest serverRequest) {
        return Mono.just(serverRequest.pathVariable(PathVariableConstants.CAPABILITY_ID))
                .flatMap(deleteCapabilityTechnologiesUseCase::execute)
                .then(ServerResponse.noContent().build());
    }

    @Override
    public Mono<ServerResponse> listenFindTechnologiesByCapabilityIds(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(TechnologiesByCapabilityInDto.class)
                .defaultIfEmpty(new TechnologiesByCapabilityInDto(null))
                .flatMap(dto -> findTechnologiesByCapabilityIdsUseCase.execute(dto.capabilityIds()))
                .map(this::toTechnologiesByCapabilityOutDto)
                .flatMap(response -> ServerResponse.status(HttpStatus.OK).bodyValue(response));
    }

    private TechnologiesByCapabilityOutDto toTechnologiesByCapabilityOutDto(Map<Long, List<TechnologySummary>> technologiesByCapability) {
        List<TechnologiesByCapabilityEntryOutDto> entries = technologiesByCapability.entrySet().stream()
                .map(entry -> new TechnologiesByCapabilityEntryOutDto(entry.getKey(),
                        entry.getValue().stream()
                                .map(technology -> new TechnologySummaryOutDto(technology.id(), technology.name()))
                                .toList()))
                .toList();
        return new TechnologiesByCapabilityOutDto(entries);
    }
}
