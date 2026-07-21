package co.com.pragma.usecase.findtechnologiesbycapabilityids;

import co.com.pragma.model.capabilitytechnology.gateways.CapabilityTechnologyRepository;
import co.com.pragma.model.exceptions.FieldsValidationException;
import co.com.pragma.model.technology.TechnologySummary;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FindTechnologiesByCapabilityIdsUseCaseTest {

    private static final Long CAPABILITY_ID_1 = 1L;
    private static final Long CAPABILITY_ID_2 = 2L;
    private static final List<Long> CAPABILITY_IDS = List.of(CAPABILITY_ID_1, CAPABILITY_ID_2);
    private static final Long TECHNOLOGY_ID_1 = 10L;
    private static final String TECHNOLOGY_NAME_1 = "Java";
    private static final Long TECHNOLOGY_ID_2 = 11L;
    private static final String TECHNOLOGY_NAME_2 = "Node.js";

    @Mock
    private CapabilityTechnologyRepository capabilityTechnologyRepository;

    private FindTechnologiesByCapabilityIdsUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new FindTechnologiesByCapabilityIdsUseCase(capabilityTechnologyRepository);
    }

    @Test
    void When_CapabilityIdsAreValid_Expect_TechnologiesGroupedByCapability() {
        // Arrange
        Map<Long, List<TechnologySummary>> expected = Map.of(
                CAPABILITY_ID_1, List.of(new TechnologySummary(TECHNOLOGY_ID_1, TECHNOLOGY_NAME_1)),
                CAPABILITY_ID_2, List.of(new TechnologySummary(TECHNOLOGY_ID_2, TECHNOLOGY_NAME_2)));
        when(capabilityTechnologyRepository.findTechnologiesByCapabilityIds(CAPABILITY_IDS))
                .thenReturn(Mono.just(expected));

        // Act & Assert
        StepVerifier.create(useCase.execute(CAPABILITY_IDS))
                .expectNext(expected)
                .verifyComplete();
    }

    @Test
    void Expect_FieldsValidationException_When_CapabilityIdsIsEmpty() {
        // Act & Assert
        StepVerifier.create(useCase.execute(List.of()))
                .expectError(FieldsValidationException.class)
                .verify();

        verify(capabilityTechnologyRepository, never()).findTechnologiesByCapabilityIds(anyListOfLong());
    }

    @Test
    void Expect_FieldsValidationException_When_CapabilityIdsIsNull() {
        // Act & Assert
        StepVerifier.create(useCase.execute(null))
                .expectError(FieldsValidationException.class)
                .verify();

        verify(capabilityTechnologyRepository, never()).findTechnologiesByCapabilityIds(anyListOfLong());
    }

    @SuppressWarnings("unchecked")
    private static List<Long> anyListOfLong() {
        return org.mockito.ArgumentMatchers.any(List.class);
    }
}
