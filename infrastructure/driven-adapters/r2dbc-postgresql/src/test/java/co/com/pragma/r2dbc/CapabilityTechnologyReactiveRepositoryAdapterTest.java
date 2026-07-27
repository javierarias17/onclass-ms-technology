package co.com.pragma.r2dbc;

import co.com.pragma.model.capabilitytechnology.CapabilityTechnology;
import co.com.pragma.model.technology.exceptions.TechnologiesNotFoundException;
import co.com.pragma.r2dbc.entity.CapabilityTechnologyEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.dao.DataIntegrityViolationException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CapabilityTechnologyReactiveRepositoryAdapterTest {

    private static final Long CAPABILITY_ID = 10L;
    private static final List<Long> CAPABILITY_IDS = List.of(CAPABILITY_ID);
    private static final Long TECHNOLOGY_ID = 100L;

    @Mock
    private CapabilityTechnologyReactiveRepository repository;

    @Mock
    private ObjectMapper mapper;

    @Mock
    private TechnologyReactiveRepository technologyRepository;

    private CapabilityTechnologyReactiveRepositoryAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new CapabilityTechnologyReactiveRepositoryAdapter(repository, mapper, technologyRepository);
    }

    @Test
    void When_TechnologyIsNew_Expect_LinkToBeInserted() {
        // Arrange
        CapabilityTechnologyEntity entity = CapabilityTechnologyEntity.builder()
                .capabilityId(CAPABILITY_ID).technologyId(TECHNOLOGY_ID).build();
        CapabilityTechnology link = CapabilityTechnology.builder()
                .capabilityId(CAPABILITY_ID).technologyId(TECHNOLOGY_ID).build();

        when(repository.insertIgnoringConflict(CAPABILITY_ID, TECHNOLOGY_ID)).thenReturn(Mono.just(entity));
        when(mapper.map(entity, CapabilityTechnology.class)).thenReturn(link);

        // Act & Assert
        StepVerifier.create(adapter.saveAll(CAPABILITY_ID, List.of(TECHNOLOGY_ID)))
                .expectNextMatches(links -> links.size() == 1 && links.get(0).getTechnologyId().equals(TECHNOLOGY_ID))
                .verifyComplete();
    }

    @Test
    void Expect_TechnologiesNotFoundException_When_TechnologyWasPhysicallyDeletedBeforeInsert() {
        // Arrange: la tecnologia paso el chequeo de existencia previo, pero ya fue borrada
        // fisicamente (FK) para cuando este INSERT corre -> Postgres rechaza el INSERT con
        // una violacion de foreign key; se mapea al mismo error de negocio que ya usa el
        // rechazo de findMissingIds, en vez de dejar escapar la excepcion tecnica cruda.
        when(repository.insertIgnoringConflict(CAPABILITY_ID, TECHNOLOGY_ID)).thenReturn(Mono.error(
                new DataIntegrityViolationException("insert or update on table \"capability_technologies\" "
                        + "violates foreign key constraint")));

        // Act & Assert
        StepVerifier.create(adapter.saveAll(CAPABILITY_ID, List.of(TECHNOLOGY_ID)))
                .expectError(TechnologiesNotFoundException.class)
                .verify();
    }

    @Test
    void When_TechnologiesAreAffected_Expect_LinksAndOrphanedTechnologiesToBeDeletedInOrder() {
        // Arrange: primero se capturan las tecnologías afectadas, recién después se borran
        // los links, y al final se borran las tecnologías huérfanas.
        when(repository.findTechnologyIdsByCapabilityIds(CAPABILITY_IDS)).thenReturn(Flux.just(TECHNOLOGY_ID));
        when(repository.deleteByCapabilityIdIn(CAPABILITY_IDS)).thenReturn(Mono.empty());
        when(technologyRepository.deleteOrphaned(List.of(TECHNOLOGY_ID))).thenReturn(Flux.just(TECHNOLOGY_ID));

        // Act & Assert
        StepVerifier.create(adapter.deleteOrphanedTechnologiesForCapabilities(CAPABILITY_IDS))
                .verifyComplete();

        verify(repository).findTechnologyIdsByCapabilityIds(CAPABILITY_IDS);
        verify(repository).deleteByCapabilityIdIn(CAPABILITY_IDS);
        verify(technologyRepository).deleteOrphaned(List.of(TECHNOLOGY_ID));
    }

    @Test
    void When_NoTechnologiesAreAffected_Expect_OrphanDeleteToBeSkipped() {
        // Arrange: si la capacidad no tenía tecnologías, no hay nada que revisar más abajo
        when(repository.findTechnologyIdsByCapabilityIds(CAPABILITY_IDS)).thenReturn(Flux.empty());
        when(repository.deleteByCapabilityIdIn(CAPABILITY_IDS)).thenReturn(Mono.empty());

        // Act & Assert
        StepVerifier.create(adapter.deleteOrphanedTechnologiesForCapabilities(CAPABILITY_IDS))
                .verifyComplete();

        verify(technologyRepository, never()).deleteOrphaned(any());
    }
}
