package co.com.pragma.r2dbc;

import co.com.pragma.model.capabilitytechnology.CapabilityTechnology;
import co.com.pragma.model.capabilitytechnology.LinkCapabilityTechnologies;
import co.com.pragma.model.common.FieldConstants;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CapabilityTechnologyReactiveRepositoryAdapterTest {

    private static final Long CAPABILITY_ID = 10L;
    private static final List<Long> CAPABILITY_IDS = List.of(CAPABILITY_ID);
    private static final Long TECHNOLOGY_ID = 100L;
    private static final Long OTHER_TECHNOLOGY_ID_1 = 101L;
    private static final Long OTHER_TECHNOLOGY_ID_2 = 102L;
    private static final List<Long> TECHNOLOGY_IDS = List.of(TECHNOLOGY_ID, OTHER_TECHNOLOGY_ID_1, OTHER_TECHNOLOGY_ID_2);

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
        LinkCapabilityTechnologies request = LinkCapabilityTechnologies.builder()
                .capabilityId(CAPABILITY_ID).technologyIds(TECHNOLOGY_IDS).build();

        for (Long technologyId : TECHNOLOGY_IDS) {
            CapabilityTechnologyEntity entity = CapabilityTechnologyEntity.builder()
                    .capabilityId(CAPABILITY_ID).technologyId(technologyId).build();
            CapabilityTechnology link = CapabilityTechnology.builder()
                    .capabilityId(CAPABILITY_ID).technologyId(technologyId).build();
            when(repository.insertIgnoringConflict(CAPABILITY_ID, technologyId)).thenReturn(Mono.just(entity));
            when(mapper.map(entity, CapabilityTechnology.class)).thenReturn(link);
        }

        // Act & Assert
        StepVerifier.create(adapter.saveAll(request))
                .expectNextMatches(links -> links.size() == TECHNOLOGY_IDS.size()
                        && links.stream().anyMatch(link -> link.getTechnologyId().equals(TECHNOLOGY_ID)))
                .verifyComplete();
    }

    @Test
    void Expect_TechnologiesNotFoundException_When_TechnologyWasPhysicallyDeletedBeforeInsert() {
        // Arrange: OTHER_TECHNOLOGY_ID_1 paso el chequeo de existencia previo, pero ya fue
        // borrada fisicamente (FK) para cuando este INSERT corre -> Postgres rechaza SOLO ese
        // INSERT con una violacion de foreign key; el error debe reportar unicamente ese id,
        // no toda la lista de technologyIds que llego por parametro (las demas si existen).
        LinkCapabilityTechnologies request = LinkCapabilityTechnologies.builder()
                .capabilityId(CAPABILITY_ID).technologyIds(TECHNOLOGY_IDS).build();

        // lenient: flatMap cancela las suscripciones restantes en cuanto una falla, asi que
        // no siempre se invocan los tres inserts antes de que la cadena se corte
        lenient().when(repository.insertIgnoringConflict(CAPABILITY_ID, TECHNOLOGY_ID)).thenReturn(Mono.just(
                CapabilityTechnologyEntity.builder().capabilityId(CAPABILITY_ID).technologyId(TECHNOLOGY_ID).build()));
        lenient().when(mapper.map(any(CapabilityTechnologyEntity.class), eq(CapabilityTechnology.class)))
                .thenReturn(CapabilityTechnology.builder().capabilityId(CAPABILITY_ID).technologyId(TECHNOLOGY_ID).build());
        lenient().when(repository.insertIgnoringConflict(CAPABILITY_ID, OTHER_TECHNOLOGY_ID_1)).thenReturn(Mono.error(
                new DataIntegrityViolationException("insert or update on table \"capability_technologies\" "
                        + "violates foreign key constraint")));
        lenient().when(repository.insertIgnoringConflict(CAPABILITY_ID, OTHER_TECHNOLOGY_ID_2)).thenReturn(Mono.just(
                CapabilityTechnologyEntity.builder().capabilityId(CAPABILITY_ID).technologyId(OTHER_TECHNOLOGY_ID_2).build()));

        // Act & Assert
        StepVerifier.create(adapter.saveAll(request))
                .expectErrorMatches(error -> error instanceof TechnologiesNotFoundException notFound
                        && notFound.getErrors().get(FieldConstants.TECHNOLOGY_IDS).contains(OTHER_TECHNOLOGY_ID_1.toString())
                        && !notFound.getErrors().get(FieldConstants.TECHNOLOGY_IDS).contains(TECHNOLOGY_ID.toString())
                        && !notFound.getErrors().get(FieldConstants.TECHNOLOGY_IDS).contains(OTHER_TECHNOLOGY_ID_2.toString()))
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
