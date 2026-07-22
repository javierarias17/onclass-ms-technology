package co.com.pragma.api.mapper;

import co.com.pragma.api.dto.CapabilityTechnologyLinkInDto;
import co.com.pragma.api.dto.CapabilityTechnologyLinkOutDto;
import co.com.pragma.model.capabilitytechnology.CapabilityTechnology;
import co.com.pragma.model.capabilitytechnology.command.LinkCapabilityTechnologiesCommand;
import org.mapstruct.Mapper;

import java.util.List;

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
}
