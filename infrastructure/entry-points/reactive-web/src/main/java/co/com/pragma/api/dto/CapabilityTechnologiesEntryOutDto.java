package co.com.pragma.api.dto;

import java.util.List;

public record CapabilityTechnologiesEntryOutDto(Long capabilityId, List<TechnologySummaryOutDto> technologies) {
}
