package co.com.pragma.model.technology.gateways;

import co.com.pragma.model.technology.Technology;
import reactor.core.publisher.Mono;

import java.util.List;

public interface TechnologyRepository {
    Mono<Technology> save(Technology technology);

    Mono<Boolean> existsByName(String name);

    Mono<List<Long>> findMissingIds(List<Long> technologyIds);
}
