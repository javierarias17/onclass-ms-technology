package co.com.pragma.model.capabilitytechnology.gateways;

import co.com.pragma.model.capabilitytechnology.CapabilityTechnology;
import reactor.core.publisher.Mono;

import java.util.List;

public interface CapabilityTechnologyRepository {
    Mono<List<CapabilityTechnology>> saveAll(Long capabilityId, List<Long> technologyIds);
}
