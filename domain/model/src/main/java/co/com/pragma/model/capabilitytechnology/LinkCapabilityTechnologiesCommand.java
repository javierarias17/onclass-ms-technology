package co.com.pragma.model.capabilitytechnology;

import java.util.List;

public record LinkCapabilityTechnologiesCommand(Long capabilityId, List<Long> technologyIds) {
}
