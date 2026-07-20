package co.com.pragma.usecase.checktechnologiesexistence;

import co.com.pragma.model.exceptions.FieldsValidationException;
import co.com.pragma.model.technology.gateways.TechnologyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CheckTechnologiesExistenceUseCaseTest {

    private static final Long TECHNOLOGY_ID_1 = 1L;
    private static final Long TECHNOLOGY_ID_2 = 2L;
    private static final Long TECHNOLOGY_ID_3 = 3L;
    private static final Long MISSING_TECHNOLOGY_ID = 99L;

    @Mock
    private TechnologyRepository technologyRepository;

    private CheckTechnologiesExistenceUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new CheckTechnologiesExistenceUseCase(technologyRepository);
    }

    @Test
    void When_AllTechnologyIdsExist_Expect_EmptyMissingIdsList() {
        // Arrange
        List<Long> technologyIds = List.of(TECHNOLOGY_ID_1, TECHNOLOGY_ID_2, TECHNOLOGY_ID_3);

        when(technologyRepository.findMissingIds(technologyIds)).thenReturn(Mono.just(List.of()));

        // Act & Assert
        StepVerifier.create(useCase.execute(technologyIds))
                .expectNextMatches(List::isEmpty)
                .verifyComplete();
    }

    @Test
    void When_SomeTechnologyIdsDoNotExist_Expect_MissingIdsListToBeReturned() {
        // Arrange
        List<Long> technologyIds = List.of(TECHNOLOGY_ID_1, TECHNOLOGY_ID_2, MISSING_TECHNOLOGY_ID);
        List<Long> missingIds = List.of(MISSING_TECHNOLOGY_ID);

        when(technologyRepository.findMissingIds(technologyIds)).thenReturn(Mono.just(missingIds));

        // Act & Assert
        StepVerifier.create(useCase.execute(technologyIds))
                .expectNextMatches(result -> result.equals(missingIds))
                .verifyComplete();
    }

    @Test
    void Expect_FieldsValidationException_When_TechnologyIdsListIsEmpty() {
        // Act & Assert
        StepVerifier.create(useCase.execute(List.of()))
                .expectError(FieldsValidationException.class)
                .verify();

        verify(technologyRepository, never()).findMissingIds(anyList());
    }

    @Test
    void Expect_FieldsValidationException_When_TechnologyIdsListIsNull() {
        // Act & Assert
        StepVerifier.create(useCase.execute(null))
                .expectError(FieldsValidationException.class)
                .verify();

        verify(technologyRepository, never()).findMissingIds(anyList());
    }
}
