package co.com.pragma.r2dbc;

import co.com.pragma.model.technology.Technology;
import co.com.pragma.model.technology.exceptions.TechnologyAlreadyExistsException;
import co.com.pragma.r2dbc.entity.TechnologyEntity;
import co.com.pragma.r2dbc.mapper.TechnologyEntityMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.dao.DuplicateKeyException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TechnologyReactiveRepositoryAdapterTest {

    private static final String VALID_NAME = "Java";
    private static final String VALID_DESCRIPTION = "Lenguaje de programacion";

    @Mock
    private TechnologyReactiveRepository repository;

    @Mock
    private ObjectMapper mapper;

    @Mock
    private TechnologyEntityMapper technologyEntityMapper;

    private TechnologyReactiveRepositoryAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new TechnologyReactiveRepositoryAdapter(repository, mapper, technologyEntityMapper);
    }

    @Test
    void Expect_TechnologyAlreadyExistsException_When_ConcurrentInsertViolatesUniqueIndex() {
        // Arrange: dos requests concurrentes con el mismo nombre pasan el chequeo previo
        // (existsByName) y ambas intentan el INSERT; la segunda choca contra el indice
        // unico en Postgres.
        Technology technology = Technology.builder()
                .name(VALID_NAME).description(VALID_DESCRIPTION).build();
        TechnologyEntity entity = TechnologyEntity.builder()
                .name(VALID_NAME).description(VALID_DESCRIPTION).build();

        when(technologyEntityMapper.toEntity(technology)).thenReturn(entity);
        when(repository.save(entity)).thenReturn(Mono.error(
                new DuplicateKeyException("duplicate key value violates unique constraint")));

        // Act & Assert
        StepVerifier.create(adapter.save(technology))
                .expectError(TechnologyAlreadyExistsException.class)
                .verify();
    }

    @Test
    void When_AllTechnologyIdsExist_Expect_EmptyMissingIdsList() {
        // Arrange
        List<Long> technologyIds = List.of(1L, 2L);

        when(repository.findExistingIds(technologyIds)).thenReturn(Flux.just(1L, 2L));

        // Act & Assert
        StepVerifier.create(adapter.findMissingIds(technologyIds))
                .expectNextMatches(List::isEmpty)
                .verifyComplete();
    }

    @Test
    void When_SomeTechnologyIdsDoNotExist_Expect_MissingIdsListToBeReturned() {
        // Arrange
        List<Long> technologyIds = List.of(1L, 2L, 99L);

        when(repository.findExistingIds(technologyIds)).thenReturn(Flux.just(1L, 2L));

        // Act & Assert
        StepVerifier.create(adapter.findMissingIds(technologyIds))
                .expectNextMatches(missingIds -> missingIds.equals(List.of(99L)))
                .verifyComplete();
    }
}
