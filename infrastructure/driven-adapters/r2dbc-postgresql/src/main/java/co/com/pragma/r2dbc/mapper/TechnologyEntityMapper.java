package co.com.pragma.r2dbc.mapper;

import co.com.pragma.model.technology.Technology;
import co.com.pragma.r2dbc.entity.TechnologyEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TechnologyEntityMapper {

    @Mapping(source = "name.value", target = "name")
    @Mapping(source = "description.value", target = "description")
    TechnologyEntity toEntity(Technology technology);

    default Technology toDomain(TechnologyEntity entity) {
        return entity == null ? null
                : Technology.builder()
                        .id(entity.getId())
                        .name(entity.getName())
                        .description(entity.getDescription())
                        .build();
    }
}
