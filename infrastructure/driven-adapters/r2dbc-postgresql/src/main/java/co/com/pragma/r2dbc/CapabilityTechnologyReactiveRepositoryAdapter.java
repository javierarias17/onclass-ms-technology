package co.com.pragma.r2dbc;

import co.com.pragma.model.capabilitytechnology.CapabilityTechnology;
import co.com.pragma.model.capabilitytechnology.gateways.CapabilityTechnologyRepository;
import co.com.pragma.model.technology.TechnologySummary;
import co.com.pragma.r2dbc.entity.CapabilityTechnologyEntity;
import co.com.pragma.r2dbc.entity.CapabilityTechnologyNameProjection;
import co.com.pragma.r2dbc.helper.ReactiveAdapterOperations;
import org.reactivecommons.utils.ObjectMapper;
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

    public CapabilityTechnologyReactiveRepositoryAdapter(CapabilityTechnologyReactiveRepository repository,
            ObjectMapper mapper) {
        super(repository, mapper, d -> mapper.map(d, CapabilityTechnology.class));
    }

    @Override
    @Transactional
    public Mono<List<CapabilityTechnology>> saveAll(Long capabilityId, List<Long> technologyIds) {
        return Flux.fromIterable(technologyIds)
                .flatMap(technologyId -> repository.insertIgnoringConflict(capabilityId, technologyId)
                        .map(this::toEntity)
                        .defaultIfEmpty(CapabilityTechnology.builder()
                                .capabilityId(capabilityId)
                                .technologyId(technologyId)
                                .build()))
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
}
