package co.com.pragma.model.capabilitytechnology.gateways;

import co.com.pragma.model.capabilitytechnology.CapabilityTechnology;
import co.com.pragma.model.capabilitytechnology.LinkCapabilityTechnologies;
import co.com.pragma.model.technology.query.TechnologySummary;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

public interface CapabilityTechnologyRepository {
    Mono<List<CapabilityTechnology>> saveAll(LinkCapabilityTechnologies linkCapabilityTechnologies);

    Mono<Void> deleteByCapabilityId(Long capabilityId);

    Mono<Map<Long, List<TechnologySummary>>> findTechnologiesByCapabilityIds(List<Long> capabilityIds);
}
