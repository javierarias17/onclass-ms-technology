package co.com.pragma.usecase.registertechnology;

import co.com.pragma.model.technology.TechnologyCreateCommand;

final class TechnologyCreateCommandFactory {

    static final String VALID_NAME = "Java";
    static final String VALID_DESCRIPTION = "Backend language";

    private TechnologyCreateCommandFactory() {
    }

    static TechnologyCreateCommand createValidCommand() {
        return new TechnologyCreateCommand(VALID_NAME, VALID_DESCRIPTION);
    }

    static TechnologyCreateCommand createCommandWithName(String name) {
        return new TechnologyCreateCommand(name, VALID_DESCRIPTION);
    }

    static TechnologyCreateCommand createCommandWithDescription(String description) {
        return new TechnologyCreateCommand(VALID_NAME, description);
    }
}
