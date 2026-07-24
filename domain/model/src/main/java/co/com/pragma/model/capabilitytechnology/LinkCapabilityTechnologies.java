package co.com.pragma.model.capabilitytechnology;

import co.com.pragma.model.capabilitytechnology.valueobject.CapabilityId;
import co.com.pragma.model.capabilitytechnology.valueobject.TechnologyIds;
import co.com.pragma.model.exceptions.FieldsValidationException;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class LinkCapabilityTechnologies {

    private final CapabilityId capabilityId;
    private final TechnologyIds technologyIds;

    private LinkCapabilityTechnologies(Builder builder) {
        this.capabilityId = new CapabilityId(builder.capabilityId);
        this.technologyIds = new TechnologyIds(builder.technologyIds);
    }

    public static Builder builder() {
        return new Builder();
    }

    public CapabilityId getCapabilityId() {
        return capabilityId;
    }

    public TechnologyIds getTechnologyIds() {
        return technologyIds;
    }

    public static class Builder {

        private Long capabilityId;
        private List<Long> technologyIds;

        public Builder capabilityId(Long capabilityId) {
            this.capabilityId = capabilityId;
            return this;
        }

        public Builder technologyIds(List<Long> technologyIds) {
            this.technologyIds = technologyIds;
            return this;
        }

        public LinkCapabilityTechnologies build() {
            Map<String, String> errors = new LinkedHashMap<>();

            CapabilityId.validate(capabilityId, errors);
            TechnologyIds.validate(technologyIds, errors);

            if (!errors.isEmpty())
                throw new FieldsValidationException(errors);

            return new LinkCapabilityTechnologies(this);
        }
    }
}
