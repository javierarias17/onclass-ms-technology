package co.com.pragma.usecase.registertechnology;

import co.com.pragma.model.technology.Technology;

final class TechnologyFactory {

    private TechnologyFactory() {
    }

    static Technology createValidTechnology() {
        return Technology.builder()
                .name(TechnologyCreateCommandFactory.VALID_NAME)
                .description(TechnologyCreateCommandFactory.VALID_DESCRIPTION)
                .build();
    }
}
