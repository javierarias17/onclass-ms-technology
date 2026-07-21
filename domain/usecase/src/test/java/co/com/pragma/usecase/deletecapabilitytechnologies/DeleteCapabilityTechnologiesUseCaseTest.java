package co.com.pragma.usecase.deletecapabilitytechnologies;

import co.com.pragma.model.capabilitytechnology.gateways.CapabilityTechnologyRepository;
import co.com.pragma.model.exceptions.FieldsValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DeleteCapabilityTechnologiesUseCaseTest {

    private static final String CAPABILITY_ID = "10";
    private static final Long CAPABILITY_ID_VALUE = 10L;
    private static final String BLANK_CAPABILITY_ID = "  ";
    private static final String NOT_NUMERIC_CAPABILITY_ID = "1022-";

    @Mock
    private CapabilityTechnologyRepository capabilityTechnologyRepository;

    private DeleteCapabilityTechnologiesUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new DeleteCapabilityTechnologiesUseCase(capabilityTechnologyRepository);
    }

    @Test
    void When_CapabilityIdIsValid_Expect_LinksToBeDeleted() {
        // Arrange
        when(capabilityTechnologyRepository.deleteByCapabilityId(CAPABILITY_ID_VALUE)).thenReturn(Mono.empty());

        // Act & Assert
        StepVerifier.create(useCase.execute(CAPABILITY_ID))
                .verifyComplete();

        verify(capabilityTechnologyRepository).deleteByCapabilityId(CAPABILITY_ID_VALUE);
    }

    @Test
    void Expect_FieldsValidationException_When_CapabilityIdIsNull() {
        // Act & Assert
        StepVerifier.create(useCase.execute(null))
                .expectError(FieldsValidationException.class)
                .verify();

        verify(capabilityTechnologyRepository, never()).deleteByCapabilityId(anyLong());
    }

    @Test
    void Expect_FieldsValidationException_When_CapabilityIdIsBlank() {
        // Act & Assert
        StepVerifier.create(useCase.execute(BLANK_CAPABILITY_ID))
                .expectError(FieldsValidationException.class)
                .verify();

        verify(capabilityTechnologyRepository, never()).deleteByCapabilityId(anyLong());
    }

    @Test
    void Expect_FieldsValidationException_When_CapabilityIdIsNotNumeric() {
        // Act & Assert
        StepVerifier.create(useCase.execute(NOT_NUMERIC_CAPABILITY_ID))
                .expectError(FieldsValidationException.class)
                .verify();

        verify(capabilityTechnologyRepository, never()).deleteByCapabilityId(anyLong());
    }
}
