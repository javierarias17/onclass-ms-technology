package co.com.pragma.r2dbc;

import co.com.pragma.model.capabilitytechnology.CapabilityTechnology;
import co.com.pragma.model.capabilitytechnology.LinkCapabilityTechnologies;
import co.com.pragma.model.capabilitytechnology.gateways.CapabilityTechnologyRepository;
import co.com.pragma.model.common.FieldConstants;
import co.com.pragma.model.exceptions.constant.FunctionalMessageConstants;
import co.com.pragma.model.technology.exceptions.TechnologiesNotFoundException;
import co.com.pragma.model.technology.query.TechnologySummary;
import co.com.pragma.r2dbc.entity.CapabilityTechnologyEntity;
import co.com.pragma.r2dbc.entity.CapabilityTechnologyNameProjection;
import co.com.pragma.r2dbc.helper.ReactiveAdapterOperations;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
public class CapabilityTechnologyReactiveRepositoryAdapter extends
        ReactiveAdapterOperations<CapabilityTechnology, CapabilityTechnologyEntity, Long, CapabilityTechnologyReactiveRepository>
        implements CapabilityTechnologyRepository {

    private final TechnologyReactiveRepository technologyRepository;

    public CapabilityTechnologyReactiveRepositoryAdapter(CapabilityTechnologyReactiveRepository repository,
            ObjectMapper mapper, TechnologyReactiveRepository technologyRepository) {
        super(repository, mapper, d -> mapper.map(d, CapabilityTechnology.class));
        this.technologyRepository = technologyRepository;
    }

    @Override
    @Transactional
    public Mono<List<CapabilityTechnology>> saveAll(LinkCapabilityTechnologies linkCapabilityTechnologies) {
        Long capabilityId = linkCapabilityTechnologies.getCapabilityId().value();
        List<Long> technologyIds = linkCapabilityTechnologies.getTechnologyIds().value();
        return Flux.fromIterable(technologyIds)
                .flatMap(technologyId -> repository.insertIgnoringConflict(capabilityId, technologyId)
                        .map(this::toEntity)
                        .defaultIfEmpty(CapabilityTechnology.builder()
                                .capabilityId(capabilityId)
                                .technologyId(technologyId)
                                .build())
                        // la tecnologia pudo haberse borrado fisicamente (FK) entre el chequeo de existencia previo y este INSERT;
                        .onErrorMap(DataIntegrityViolationException.class, ex -> new TechnologiesNotFoundException(
                                FunctionalMessageConstants.BUSINESS_VALIDATION_FAILED,
                                Map.of(FieldConstants.TECHNOLOGY_IDS, String.format(
                                        FunctionalMessageConstants.TECHNOLOGIES_NOT_FOUND, List.of(technologyId))))))
                .collectList();
    }

    @Override
    public Mono<Void> deleteByCapabilityId(Long capabilityId) {
        return repository.deleteByCapabilityId(capabilityId);
    }

    @Override
    public Mono<Map<Long, List<TechnologySummary>>> findTechnologiesByCapabilityIds(List<Long> capabilityIds) {
        return repository.findTechnologiesByCapabilityIds(capabilityIds)
                .collectMultimap(
                        CapabilityTechnologyNameProjection::getCapabilityId,
                        projection -> new TechnologySummary(projection.getTechnologyId(), projection.getTechnologyName()))
                .map(multimap -> multimap.entrySet().stream()
                        .collect(Collectors.toMap(Map.Entry::getKey, entry -> List.copyOf(entry.getValue()))));
    }

    @Override
    @Transactional
    public Mono<Void> deleteOrphanedTechnologiesForCapabilities(List<Long> capabilityIds) {
        return repository.findTechnologyIdsByCapabilityIds(capabilityIds)
                .collectList()
                .flatMap(affectedTechnologyIds -> repository.deleteByCapabilityIdIn(capabilityIds)
                        .then(affectedTechnologyIds.isEmpty()
                                ? Mono.empty()
                                : technologyRepository.deleteOrphaned(affectedTechnologyIds).then()));
    }

    

}
