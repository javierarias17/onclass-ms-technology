package co.com.pragma.api.dto;

import java.util.List;

public record CapabilityTechnologyLinkInDto(Long capabilityId, List<Long> technologyIds) {
}
