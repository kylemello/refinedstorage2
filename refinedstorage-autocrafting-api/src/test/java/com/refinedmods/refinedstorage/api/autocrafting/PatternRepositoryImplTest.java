package com.refinedmods.refinedstorage.api.autocrafting;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PatternRepositoryImplTest {
    private PatternRepositoryImpl sut;

    @BeforeEach
    void setUp() {
        sut = new PatternRepositoryImpl();
    }

    @Test
    void testDefaultState() {
        // Assert
        assertThat(sut.getOutputs()).isEmpty();
        assertThat(sut.getAll()).isEmpty();
    }

    @Test
    void shouldAddPattern() {
        // Act
        sut.add(new SimplePattern(ResourceFixtures.A));

        // Assert
        assertThat(sut.getOutputs()).usingRecursiveFieldByFieldElementComparator().containsExactly(ResourceFixtures.A);
        assertThat(sut.getAll()).usingRecursiveFieldByFieldElementComparator().containsExactly(
            new SimplePattern(ResourceFixtures.A)
        );
    }

    @Test
    void shouldAddMultiplePatterns() {
        // Act
        sut.add(new SimplePattern(ResourceFixtures.A));
        sut.add(new SimplePattern(ResourceFixtures.B));

        // Assert
        assertThat(sut.getOutputs()).usingRecursiveFieldByFieldElementComparator().containsExactlyInAnyOrder(
            ResourceFixtures.A,
            ResourceFixtures.B
        );
        assertThat(sut.getAll()).usingRecursiveFieldByFieldElementComparator().containsExactlyInAnyOrder(
            new SimplePattern(ResourceFixtures.A),
            new SimplePattern(ResourceFixtures.B)
        );
    }

    @Test
    void shouldRemovePattern() {
        // Arrange
        final SimplePattern a = new SimplePattern(ResourceFixtures.A);
        final SimplePattern b = new SimplePattern(ResourceFixtures.B);

        sut.add(a);
        sut.add(b);

        // Act
        sut.remove(a);

        // Assert
        assertThat(sut.getOutputs()).usingRecursiveFieldByFieldElementComparator().containsExactly(ResourceFixtures.B);
        assertThat(sut.getAll()).usingRecursiveFieldByFieldElementComparator().containsExactly(
            new SimplePattern(ResourceFixtures.B)
        );
    }

    @Test
    void shouldRemoveMultiplePatterns() {
        // Arrange
        final SimplePattern a = new SimplePattern(ResourceFixtures.A);
        final SimplePattern b = new SimplePattern(ResourceFixtures.B);

        sut.add(a);
        sut.add(b);

        // Act
        sut.remove(a);
        sut.remove(b);

        // Assert
        assertThat(sut.getOutputs()).isEmpty();
        assertThat(sut.getAll()).isEmpty();
    }

    @Test
    void shouldRemovePatternButNotRemoveOutputIfAnotherPatternStillHasThatOutput() {
        // Arrange
        final SimplePattern a = new SimplePattern(ResourceFixtures.A);
        final SimplePattern b = new SimplePattern(ResourceFixtures.B, ResourceFixtures.A);

        sut.add(a);
        sut.add(b);

        // Act
        sut.remove(a);

        // Assert
        assertThat(sut.getOutputs()).usingRecursiveFieldByFieldElementComparator().containsExactlyInAnyOrder(
            ResourceFixtures.A,
            ResourceFixtures.B
        );
        assertThat(sut.getAll()).usingRecursiveFieldByFieldElementComparator().containsExactly(
            new SimplePattern(ResourceFixtures.B, ResourceFixtures.A)
        );
    }
}
