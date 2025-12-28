package com.github.imdmk.playtime.database.repository.ormlite;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class EntityMapperTest {

    private static class FakeMapper implements EntityMapper<String, Integer> {

        @Override
        public @NotNull String toEntity(@NotNull Integer domain) {
            return "E" + domain;
        }

        @Override
        public @NotNull Integer toDomain(String entity) {
            return Integer.parseInt(entity.substring(1));
        }
    }

    @Test
    void toEntityListShouldMapAllDomainObjects() {
        EntityMapper<String, Integer> mapper = new FakeMapper();

        List<Integer> domain = List.of(1, 2, 3);
        List<String> entities = mapper.toEntityList(domain);

        assertThat(entities).containsExactly("E1", "E2", "E3");
    }

    @Test
    void toDomainListShouldMapAllEntities() {
        EntityMapper<String, Integer> mapper = new FakeMapper();

        List<String> entities = List.of("E5", "E10", "E99");
        List<Integer> domain = mapper.toDomainList(entities);

        assertThat(domain).containsExactly(5, 10, 99);
    }

    @Test
    void toEntityListShouldHandleEmptyList() {
        EntityMapper<String, Integer> mapper = new FakeMapper();

        List<String> result = mapper.toEntityList(List.of());

        assertThat(result).isEmpty();
    }

    @Test
    void toDomainListShouldHandleEmptyList() {
        EntityMapper<String, Integer> mapper = new FakeMapper();

        List<Integer> result = mapper.toDomainList(List.of());

        assertThat(result).isEmpty();
    }
}

