package co.com.pragma.usecase.deleteorphanedtechnologiesforcapabilities;

import co.com.pragma.model.capabilitytechnology.gateways.CapabilityTechnologyRepository;
import co.com.pragma.model.exceptions.FieldsValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DeleteOrphanedTechnologiesForCapabilitiesUseCaseTest {

    private static final Long CAPABILITY_ID = 10L;
    private static final List<Long> CAPABILITY_IDS = List.of(CAPABILITY_ID);

    @Mock
    private CapabilityTechnologyRepository capabilityTechnologyRepository;

    private DeleteOrphanedTechnologiesForCapabilitiesUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new DeleteOrphanedTechnologiesForCapabilitiesUseCase(capabilityTechnologyRepository);
    }

    @Test
    void When_CapabilityIdsAreValid_Expect_CascadeDeleteToBeCalled() {
        // Arrange
        when(capabilityTechnologyRepository.deleteOrphanedTechnologiesForCapabilities(CAPABILITY_IDS))
                .thenReturn(Mono.empty());

        // Act & Assert
        StepVerifier.create(useCase.execute(CAPABILITY_IDS))
                .verifyComplete();

        verify(capabilityTechnologyRepository).deleteOrphanedTechnologiesForCapabilities(CAPABILITY_IDS);
    }

    @Test
    void Expect_ErrorToPropagate_When_RepositoryFails() {
        // Arrange: si technology-ms falla, no debe reportarse éxito
        RuntimeException failure = new RuntimeException("db unavailable");
        when(capabilityTechnologyRepository.deleteOrphanedTechnologiesForCapabilities(CAPABILITY_IDS))
                .thenReturn(Mono.error(failure));

        // Act & Assert
        StepVerifier.create(useCase.execute(CAPABILITY_IDS))
                .expectErrorMatches(error -> error == failure)
                .verify();
    }

    @Test
    void Expect_FieldsValidationException_When_CapabilityIdsIsNull() {
        // Act & Assert
        StepVerifier.create(useCase.execute(null))
                .expectError(FieldsValidationException.class)
                .verify();

        verify(capabilityTechnologyRepository, never()).deleteOrphanedTechnologiesForCapabilities(any());
    }

    @Test
    void Expect_FieldsValidationException_When_CapabilityIdsIsEmpty() {
        // Act & Assert
        StepVerifier.create(useCase.execute(List.of()))
                .expectError(FieldsValidationException.class)
                .verify();

        verify(capabilityTechnologyRepository, never()).deleteOrphanedTechnologiesForCapabilities(any());
    }
}
