package co.com.pragma.api;

import co.com.pragma.api.constants.PathVariableConstants;
import co.com.pragma.api.dto.CapabilityTechnologiesEntryOutDto;
import co.com.pragma.api.dto.CapabilityTechnologiesLookupInDto;
import co.com.pragma.api.dto.CapabilityTechnologiesLookupOutDto;
import co.com.pragma.api.dto.CapabilityTechnologyLinkInDto;
import co.com.pragma.api.dto.TechnologyExistenceInDto;
import co.com.pragma.api.dto.TechnologyExistenceOutDto;
import co.com.pragma.api.dto.TechnologyInDto;
import co.com.pragma.api.dto.TechnologySummaryOutDto;
import co.com.pragma.api.mapper.CapabilityTechnologyDtoMapper;
import co.com.pragma.api.mapper.TechnologyDtoMapper;
import co.com.pragma.model.technology.TechnologySummary;
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
                .map(technologyDtoMapper::toCommand)
                .flatMap(registerTechnologyUseCase::execute)
                .map(technologyDtoMapper::toResponse)
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
                .map(capabilityTechnologyDtoMapper::toCommand)
                .flatMap(linkCapabilityTechnologiesUseCase::execute)
                .map(capabilityTechnologyDtoMapper::toResponse)
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
        return serverRequest.bodyToMono(CapabilityTechnologiesLookupInDto.class)
                .defaultIfEmpty(new CapabilityTechnologiesLookupInDto(null))
                .flatMap(dto -> findTechnologiesByCapabilityIdsUseCase.execute(dto.capabilityIds()))
                .map(this::toLookupResponse)
                .flatMap(response -> ServerResponse.status(HttpStatus.OK).bodyValue(response));
    }

    private CapabilityTechnologiesLookupOutDto toLookupResponse(Map<Long, List<TechnologySummary>> technologiesByCapability) {
        List<CapabilityTechnologiesEntryOutDto> entries = technologiesByCapability.entrySet().stream()
                .map(entry -> new CapabilityTechnologiesEntryOutDto(entry.getKey(),
                        entry.getValue().stream()
                                .map(technology -> new TechnologySummaryOutDto(technology.id(), technology.name()))
                                .toList()))
                .toList();
        return new CapabilityTechnologiesLookupOutDto(entries);
    }
}
