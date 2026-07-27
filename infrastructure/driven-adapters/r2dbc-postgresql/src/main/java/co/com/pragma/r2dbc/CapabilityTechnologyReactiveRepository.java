package co.com.pragma.r2dbc;

import co.com.pragma.r2dbc.entity.CapabilityTechnologyEntity;
import co.com.pragma.r2dbc.entity.CapabilityTechnologyNameProjection;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface CapabilityTechnologyReactiveRepository extends
        ReactiveCrudRepository<CapabilityTechnologyEntity, Long>,
        ReactiveQueryByExampleExecutor<CapabilityTechnologyEntity> {

    @Query("INSERT INTO capability_technologies (capability_id, technology_id) "
            + "VALUES (:capabilityId, :technologyId) "
            + "ON CONFLICT (capability_id, technology_id) DO NOTHING "
            + "RETURNING *")
    Mono<CapabilityTechnologyEntity> insertIgnoringConflict(@Param("capabilityId") Long capabilityId,
            @Param("technologyId") Long technologyId);

    Mono<Void> deleteByCapabilityId(Long capabilityId);

    @Query("SELECT DISTINCT technology_id FROM capability_technologies WHERE capability_id IN (:capabilityIds)")
    Flux<Long> findTechnologyIdsByCapabilityIds(@Param("capabilityIds") List<Long> capabilityIds);

    @Query("DELETE FROM capability_technologies WHERE capability_id IN (:capabilityIds)")
    Mono<Void> deleteByCapabilityIdIn(@Param("capabilityIds") List<Long> capabilityIds);

    @Query("SELECT ct.capability_id AS capability_id, t.id AS technology_id, t.name AS technology_name "
            + "FROM capability_technologies ct "
            + "JOIN technologies t ON t.id = ct.technology_id "
            + "WHERE ct.capability_id IN (:capabilityIds)")
    Flux<CapabilityTechnologyNameProjection> findTechnologiesByCapabilityIds(
            @Param("capabilityIds") List<Long> capabilityIds);
}
