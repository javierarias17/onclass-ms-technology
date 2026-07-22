package co.com.pragma.api.dto;

import java.util.List;

public record TechnologiesByCapabilityEntryOutDto(Long capabilityId, List<TechnologySummaryOutDto> technologies) {
}
