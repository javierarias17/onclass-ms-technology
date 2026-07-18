package co.com.pragma.r2dbc;

import co.com.pragma.r2dbc.entity.TechnologyEntity;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface TechnologyReactiveRepository extends
        ReactiveCrudRepository<TechnologyEntity, Long>,
        ReactiveQueryByExampleExecutor<TechnologyEntity> {

    Mono<Boolean> existsByName(String name);
}
