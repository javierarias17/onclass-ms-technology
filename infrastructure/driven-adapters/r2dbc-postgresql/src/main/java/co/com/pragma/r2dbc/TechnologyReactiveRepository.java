package co.com.pragma.r2dbc;

import co.com.pragma.r2dbc.entity.TechnologyEntity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface TechnologyReactiveRepository extends
        ReactiveCrudRepository<TechnologyEntity, Long>,
        ReactiveQueryByExampleExecutor<TechnologyEntity> {

    Mono<Boolean> existsByName(String name);

    @Query("SELECT id FROM technologies WHERE id IN (:technologyIds)")
    Flux<Long> findExistingIds(@Param("technologyIds") List<Long> technologyIds);

    @Query("DELETE FROM technologies WHERE id IN (:candidateIds) "
            + "AND id NOT IN (SELECT technology_id FROM capability_technologies WHERE technology_id IN (:candidateIds)) "
            + "RETURNING id")
    Flux<Long> deleteOrphaned(@Param("candidateIds") List<Long> candidateIds);
}
