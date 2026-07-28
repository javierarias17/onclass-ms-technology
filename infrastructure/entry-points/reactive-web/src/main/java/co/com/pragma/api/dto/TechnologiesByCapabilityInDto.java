package co.com.pragma.api.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record TechnologiesByCapabilityInDto(List<Long> capabilityIds) {
}
