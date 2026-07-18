package co.com.pragma.api.mapper;

import co.com.pragma.api.dto.TechnologyInDto;
import co.com.pragma.api.dto.TechnologyOutDto;
import co.com.pragma.model.technology.Technology;
import co.com.pragma.model.technology.TechnologyCreateCommand;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TechnologyDtoMapper {

    TechnologyCreateCommand toCommand(TechnologyInDto technologyInDto);

    @Mapping(source = "name.value", target = "name")
    @Mapping(source = "description.value", target = "description")
    TechnologyOutDto toResponse(Technology technology);
}
