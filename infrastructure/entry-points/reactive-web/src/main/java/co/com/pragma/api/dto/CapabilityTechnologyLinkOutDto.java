package co.com.pragma.api.dto;

import java.util.List;

public record CapabilityTechnologyLinkOutDto(Long capabilityId, List<Long> technologyIds) {
}
