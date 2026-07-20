package co.com.pragma.usecase.linkcapabilitytechnologies;

import co.com.pragma.model.capabilitytechnology.CapabilityTechnology;
import co.com.pragma.model.capabilitytechnology.LinkCapabilityTechnologiesCommand;
import co.com.pragma.model.capabilitytechnology.gateways.CapabilityTechnologyRepository;
import co.com.pragma.model.exceptions.FieldsValidationException;
import co.com.pragma.model.technology.exceptions.TechnologiesNotFoundException;
import co.com.pragma.model.technology.gateways.TechnologyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LinkCapabilityTechnologiesUseCaseTest {

    private static final Long LINK_ID = 1L;
    private static final Long CAPABILITY_ID = 10L;
    private static final Long TECHNOLOGY_ID_1 = 1L;
    private static final Long TECHNOLOGY_ID_2 = 2L;
    private static final Long TECHNOLOGY_ID_3 = 3L;
    private static final Long MISSING_TECHNOLOGY_ID = 99L;

    @Mock
    private TechnologyRepository technologyRepository;

    @Mock
    private CapabilityTechnologyRepository capabilityTechnologyRepository;

    private LinkCapabilityTechnologiesUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new LinkCapabilityTechnologiesUseCase(technologyRepository, capabilityTechnologyRepository);
    }

    @Test
    void When_AllTechnologiesExist_Expect_LinksToBeSaved() {
        // Arrange
        LinkCapabilityTechnologiesCommand command = new LinkCapabilityTechnologiesCommand(CAPABILITY_ID,
                List.of(TECHNOLOGY_ID_1, TECHNOLOGY_ID_2, TECHNOLOGY_ID_3));
        List<CapabilityTechnology> savedLinks = List.of(
                CapabilityTechnology.builder().id(LINK_ID).capabilityId(CAPABILITY_ID).technologyId(TECHNOLOGY_ID_1).build());

        when(technologyRepository.findMissingIds(command.technologyIds())).thenReturn(Mono.just(List.of()));
        when(capabilityTechnologyRepository.saveAll(command.capabilityId(), command.technologyIds()))
                .thenReturn(Mono.just(savedLinks));

        // Act & Assert
        StepVerifier.create(useCase.execute(command))
                .expectNextMatches(result -> result.equals(savedLinks))
                .verifyComplete();
    }

    @Test
    void Expect_TechnologiesNotFoundException_When_SomeTechnologiesDoNotExist() {
        // Arrange
        LinkCapabilityTechnologiesCommand command = new LinkCapabilityTechnologiesCommand(CAPABILITY_ID,
                List.of(TECHNOLOGY_ID_1, MISSING_TECHNOLOGY_ID));

        when(technologyRepository.findMissingIds(command.technologyIds())).thenReturn(Mono.just(List.of(MISSING_TECHNOLOGY_ID)));

        // Act & Assert
        StepVerifier.create(useCase.execute(command))
                .expectError(TechnologiesNotFoundException.class)
                .verify();

        verify(capabilityTechnologyRepository, never()).saveAll(any(), anyList());
    }

    @Test
    void Expect_FieldsValidationException_When_CapabilityIdIsNull() {
        // Arrange
        LinkCapabilityTechnologiesCommand command = new LinkCapabilityTechnologiesCommand(null,
                List.of(TECHNOLOGY_ID_1, TECHNOLOGY_ID_2));

        // Act & Assert
        StepVerifier.create(useCase.execute(command))
                .expectError(FieldsValidationException.class)
                .verify();

        verify(technologyRepository, never()).findMissingIds(anyList());
    }

    @Test
    void Expect_FieldsValidationException_When_TechnologyIdsIsEmpty() {
        // Arrange
        LinkCapabilityTechnologiesCommand command = new LinkCapabilityTechnologiesCommand(CAPABILITY_ID, List.of());

        // Act & Assert
        StepVerifier.create(useCase.execute(command))
                .expectError(FieldsValidationException.class)
                .verify();

        verify(technologyRepository, never()).findMissingIds(anyList());
    }
}
