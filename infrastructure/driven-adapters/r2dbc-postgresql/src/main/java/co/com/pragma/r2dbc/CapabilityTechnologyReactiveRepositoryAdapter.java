package co.com.pragma.r2dbc;

import co.com.pragma.model.capabilitytechnology.CapabilityTechnology;
import co.com.pragma.model.capabilitytechnology.gateways.CapabilityTechnologyRepository;
import co.com.pragma.r2dbc.entity.CapabilityTechnologyEntity;
import co.com.pragma.r2dbc.helper.ReactiveAdapterOperations;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

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
}
