package co.com.pragma.model.technology.gateways;

import co.com.pragma.model.technology.Technology;
import reactor.core.publisher.Mono;

public interface TechnologyRepository {
    Mono<Technology> save(Technology technology);

    Mono<Boolean> existsByName(String name);
}
