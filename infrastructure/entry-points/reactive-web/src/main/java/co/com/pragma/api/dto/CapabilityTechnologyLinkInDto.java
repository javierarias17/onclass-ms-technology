package co.com.pragma.api.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record CapabilityTechnologyLinkInDto(Long capabilityId, List<Long> technologyIds) {
}
