package co.com.pragma.model.capabilitytechnology.command;

import java.util.List;

public record LinkCapabilityTechnologiesCommand(Long capabilityId, List<Long> technologyIds) {
}
