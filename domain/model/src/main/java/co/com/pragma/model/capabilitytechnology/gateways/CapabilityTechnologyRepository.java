package co.com.pragma.model.capabilitytechnology.gateways;

import co.com.pragma.model.capabilitytechnology.CapabilityTechnology;
import co.com.pragma.model.technology.query.TechnologySummary;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

public interface CapabilityTechnologyRepository {
    Mono<List<CapabilityTechnology>> saveAll(Long capabilityId, List<Long> technologyIds);

    Mono<Void> deleteByCapabilityId(Long capabilityId);

    Mono<Map<Long, List<TechnologySummary>>> findTechnologiesByCapabilityIds(List<Long> capabilityIds);

    Mono<Void> deleteOrphanedTechnologiesForCapabilities(List<Long> capabilityIds);
}
