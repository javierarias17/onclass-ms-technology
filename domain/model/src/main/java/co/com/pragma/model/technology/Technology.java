package co.com.pragma.model.technology;

import co.com.pragma.model.exceptions.FieldsValidationException;
import co.com.pragma.model.technology.valueobject.TechnologyDescription;
import co.com.pragma.model.technology.valueobject.TechnologyName;

import java.util.LinkedHashMap;
import java.util.Map;

public class Technology {

    private final Long id;
    private final TechnologyName name;
    private final TechnologyDescription description;

    private Technology(Builder builder) {
        this.id = builder.id;
        this.name = new TechnologyName(builder.name);
        this.description = new TechnologyDescription(builder.description);
    }

    public static Builder builder() {
        return new Builder();
    }

    public Long getId() {
        return id;
    }

    public TechnologyName getName() {
        return name;
    }

    public TechnologyDescription getDescription() {
        return description;
    }

    public static class Builder {

        private Long id;
        private String name;
        private String description;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Technology build() {
            Map<String, String> errors = new LinkedHashMap<>();

            TechnologyName.validate(name, errors);
            TechnologyDescription.validate(description, errors);

            if (!errors.isEmpty())
                throw new FieldsValidationException(errors);

            return new Technology(this);
        }
    }
}
