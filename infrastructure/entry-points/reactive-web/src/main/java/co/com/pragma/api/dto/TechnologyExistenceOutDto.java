package co.com.pragma.api.dto;

import java.util.List;

public record TechnologyExistenceOutDto(List<Long> missingIds) {
}
