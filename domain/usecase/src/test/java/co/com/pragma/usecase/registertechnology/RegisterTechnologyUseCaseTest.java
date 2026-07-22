package co.com.pragma.usecase.registertechnology;

import co.com.pragma.model.exceptions.FieldsValidationException;
import co.com.pragma.model.technology.Technology;
import co.com.pragma.model.technology.command.TechnologyCreateCommand;
import co.com.pragma.model.technology.exceptions.TechnologyAlreadyExistsException;
import co.com.pragma.model.technology.gateways.TechnologyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RegisterTechnologyUseCaseTest {

    @Mock
    private TechnologyRepository technologyRepository;

    private RegisterTechnologyUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new RegisterTechnologyUseCase(technologyRepository);
    }

    @Test
    void When_TechnologyInformationIsValid_Expect_TechnologyToBeSavedSuccessfully() {
        // Arrange
        TechnologyCreateCommand command = TechnologyCreateCommandFactory.createValidCommand();
        Technology technology = TechnologyFactory.createValidTechnology();

        when(technologyRepository.existsByName(TechnologyCreateCommandFactory.VALID_NAME)).thenReturn(Mono.just(false));
        when(technologyRepository.save(any(Technology.class))).thenReturn(Mono.just(technology));

        // Act & Assert
        StepVerifier.create(useCase.execute(command))
                .expectNextMatches(saved -> saved.getName().value().equals(TechnologyCreateCommandFactory.VALID_NAME)
                        && saved.getDescription().value().equals(TechnologyCreateCommandFactory.VALID_DESCRIPTION))
                .verifyComplete();
    }

    @Test
    void Expect_TechnologyAlreadyExistsException_When_NameAlreadyExists() {
        // Arrange
        TechnologyCreateCommand command = TechnologyCreateCommandFactory.createValidCommand();

        when(technologyRepository.existsByName(TechnologyCreateCommandFactory.VALID_NAME)).thenReturn(Mono.just(true));

        // Act & Assert
        StepVerifier.create(useCase.execute(command))
                .expectError(TechnologyAlreadyExistsException.class)
                .verify();

        verify(technologyRepository, never()).save(any());
    }

    @Test
    void Expect_FieldsValidationException_When_NameIsBlank() {
        // Arrange
        TechnologyCreateCommand command = TechnologyCreateCommandFactory.createCommandWithName(" ");

        // Act & Assert
        StepVerifier.create(useCase.execute(command))
                .expectError(FieldsValidationException.class)
                .verify();

        verify(technologyRepository, never()).existsByName(any());
    }

    @Test
    void Expect_FieldsValidationException_When_NameExceedsMaxLength() {
        // Arrange
        TechnologyCreateCommand command = TechnologyCreateCommandFactory.createCommandWithName("a".repeat(51));

        // Act & Assert
        StepVerifier.create(useCase.execute(command))
                .expectError(FieldsValidationException.class)
                .verify();
    }

    @Test
    void Expect_FieldsValidationException_When_DescriptionIsBlank() {
        // Arrange
        TechnologyCreateCommand command = TechnologyCreateCommandFactory.createCommandWithDescription(" ");

        // Act & Assert
        StepVerifier.create(useCase.execute(command))
                .expectError(FieldsValidationException.class)
                .verify();
    }

    @Test
    void Expect_FieldsValidationException_When_DescriptionExceedsMaxLength() {
        // Arrange
        TechnologyCreateCommand command = TechnologyCreateCommandFactory.createCommandWithDescription("a".repeat(91));

        // Act & Assert
        StepVerifier.create(useCase.execute(command))
                .expectError(FieldsValidationException.class)
                .verify();
    }
}
