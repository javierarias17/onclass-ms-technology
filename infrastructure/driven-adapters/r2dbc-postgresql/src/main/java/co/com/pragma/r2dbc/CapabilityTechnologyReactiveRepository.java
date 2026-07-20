package co.com.pragma.r2dbc;

import co.com.pragma.r2dbc.entity.CapabilityTechnologyEntity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface CapabilityTechnologyReactiveRepository extends
        ReactiveCrudRepository<CapabilityTechnologyEntity, Long>,
        ReactiveQueryByExampleExecutor<CapabilityTechnologyEntity> {

    @Query("INSERT INTO capability_technologies (capability_id, technology_id) "
            + "VALUES (:capabilityId, :technologyId) "
            + "ON CONFLICT (capability_id, technology_id) DO NOTHING "
            + "RETURNING *")
    Mono<CapabilityTechnologyEntity> insertIgnoringConflict(@Param("capabilityId") Long capabilityId,
            @Param("technologyId") Long technologyId);
}
