package co.com.pragma.api.dto;

import lombok.Builder;

@Builder
public record TechnologyInDto(String name, String description) {
}
