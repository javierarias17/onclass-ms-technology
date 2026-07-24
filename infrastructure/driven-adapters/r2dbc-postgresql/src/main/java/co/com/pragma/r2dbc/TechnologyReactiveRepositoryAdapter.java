package co.com.pragma.r2dbc;

import co.com.pragma.model.technology.Technology;
import co.com.pragma.model.technology.gateways.TechnologyRepository;
import co.com.pragma.r2dbc.entity.TechnologyEntity;
import co.com.pragma.r2dbc.helper.ReactiveAdapterOperations;
import co.com.pragma.r2dbc.mapper.TechnologyEntityMapper;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.List;

@Repository
public class TechnologyReactiveRepositoryAdapter extends ReactiveAdapterOperations<
        Technology,
        TechnologyEntity,
        Long,
        TechnologyReactiveRepository
        > implements TechnologyRepository {

    private final TechnologyEntityMapper technologyEntityMapper;

    public TechnologyReactiveRepositoryAdapter(TechnologyReactiveRepository repository, ObjectMapper mapper,
                                               TechnologyEntityMapper technologyEntityMapper) {
        super(repository, mapper, technologyEntityMapper::toDomain);
        this.technologyEntityMapper = technologyEntityMapper;
    }

    @Override
    protected TechnologyEntity toData(Technology technology) {
        return technologyEntityMapper.toEntity(technology);
    }

    @Override
    public Mono<Boolean> existsByName(String name) {
        return repository.existsByName(name);
    }

    @Override
    public Mono<List<Long>> findMissingIds(List<Long> technologyIds) {
        return repository.findExistingIds(technologyIds)
                .collectList()
                .map(existingIds -> technologyIds.stream()
                        .distinct()
                        .filter(id -> !existingIds.contains(id))
                        .toList());
    }
}
