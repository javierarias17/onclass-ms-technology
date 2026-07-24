package co.com.pragma.api.mapper;

import co.com.pragma.api.dto.CapabilityTechnologyLinkInDto;
import co.com.pragma.api.dto.CapabilityTechnologyLinkOutDto;
import co.com.pragma.api.dto.TechnologiesByCapabilityEntryOutDto;
import co.com.pragma.api.dto.TechnologiesByCapabilityOutDto;
import co.com.pragma.api.dto.TechnologySummaryOutDto;
import co.com.pragma.model.capabilitytechnology.CapabilityTechnology;
import co.com.pragma.model.capabilitytechnology.command.LinkCapabilityTechnologiesCommand;
import co.com.pragma.model.technology.query.TechnologySummary;
import org.mapstruct.Mapper;

import java.util.List;
import java.util.Map;

@Mapper(componentModel = "spring")
public interface CapabilityTechnologyDtoMapper {

    LinkCapabilityTechnologiesCommand toLinkCapabilityTechnologiesCommand(CapabilityTechnologyLinkInDto capabilityTechnologyLinkInDto);

    default CapabilityTechnologyLinkOutDto toCapabilityTechnologyLinkOutDto(List<CapabilityTechnology> capabilityTechnologies) {
        if (capabilityTechnologies == null || capabilityTechnologies.isEmpty())
            return new CapabilityTechnologyLinkOutDto(null, List.of());

        return new CapabilityTechnologyLinkOutDto(capabilityTechnologies.get(0).getCapabilityId(),
                capabilityTechnologies.stream()
                        .map(CapabilityTechnology::getTechnologyId)
                        .toList());
    }

    default TechnologiesByCapabilityOutDto toTechnologiesByCapabilityOutDto(Map<Long, List<TechnologySummary>> technologiesByCapability) {
        List<TechnologiesByCapabilityEntryOutDto> entries = technologiesByCapability.entrySet().stream()
                .map(entry -> new TechnologiesByCapabilityEntryOutDto(entry.getKey(),
                        entry.getValue().stream()
                                .map(this::toTechnologySummaryOutDto)
                                .toList()))
                .toList();
        return new TechnologiesByCapabilityOutDto(entries);
    }

    default TechnologySummaryOutDto toTechnologySummaryOutDto(TechnologySummary technologySummary) {
        return new TechnologySummaryOutDto(technologySummary.id(), technologySummary.name());
    }
}
