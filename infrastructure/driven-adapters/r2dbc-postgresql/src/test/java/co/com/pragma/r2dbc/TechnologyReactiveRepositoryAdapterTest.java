package co.com.pragma.r2dbc;

import co.com.pragma.r2dbc.mapper.TechnologyEntityMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.reactivecommons.utils.ObjectMapper;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.List;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TechnologyReactiveRepositoryAdapterTest {

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
