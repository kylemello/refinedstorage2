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
    }

    @Test
    void shouldAddPattern() {
        // Act
        sut.add(new SimplePattern(FakeResources.A));

        // Assert
        assertThat(sut.getOutputs()).usingRecursiveFieldByFieldElementComparator().containsExactly(FakeResources.A);
    }

    @Test
    void shouldAddMultiplePatterns() {
        // Act
        sut.add(new SimplePattern(FakeResources.A));
        sut.add(new SimplePattern(FakeResources.B));

        // Assert
        assertThat(sut.getOutputs()).usingRecursiveFieldByFieldElementComparator().containsExactlyInAnyOrder(
            FakeResources.A,
            FakeResources.B
        );
    }

    @Test
    void shouldRemovePattern() {
        // Arrange
        final SimplePattern a = new SimplePattern(FakeResources.A);
        final SimplePattern b = new SimplePattern(FakeResources.B);

        sut.add(a);
        sut.add(b);

        // Act
        sut.remove(a);

        // Assert
        assertThat(sut.getOutputs()).usingRecursiveFieldByFieldElementComparator().containsExactly(FakeResources.B);
    }

    @Test
    void shouldRemoveMultiplePatterns() {
        // Arrange
        final SimplePattern a = new SimplePattern(FakeResources.A);
        final SimplePattern b = new SimplePattern(FakeResources.B);

        sut.add(a);
        sut.add(b);

        // Act
        sut.remove(a);
        sut.remove(b);

        // Assert
        assertThat(sut.getOutputs()).isEmpty();
    }

    @Test
    void shouldRemovePatternButNotRemoveOutputIfAnotherPatternStillHasThatOutput() {
        // Arrange
        final SimplePattern a = new SimplePattern(FakeResources.A);
        final SimplePattern b = new SimplePattern(FakeResources.B, FakeResources.A);

        sut.add(a);
        sut.add(b);

        // Act
        sut.remove(a);

        // Assert
        assertThat(sut.getOutputs()).usingRecursiveFieldByFieldElementComparator().containsExactlyInAnyOrder(
            FakeResources.A,
            FakeResources.B
        );
    }
}
