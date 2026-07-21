package co.com.pragma.model.capabilitytechnology;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class CapabilityTechnology {
    private Long id;
    private Long capabilityId;
    private Long technologyId;
}
