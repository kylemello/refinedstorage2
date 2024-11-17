package com.refinedmods.refinedstorage.api.network.impl.autocrafting;

import com.refinedmods.refinedstorage.api.network.impl.node.patternprovider.PatternProviderNetworkNode;
import com.refinedmods.refinedstorage.api.network.node.container.NetworkNodeContainer;
import com.refinedmods.refinedstorage.network.test.fixtures.FakeTaskStatusProvider;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.refinedmods.refinedstorage.network.test.fixtures.ResourceFixtures.A;
import static org.assertj.core.api.Assertions.assertThat;

class AutocraftingNetworkComponentImplTest {
    private AutocraftingNetworkComponentImpl sut;

    @BeforeEach
    void setUp() {
        sut = new AutocraftingNetworkComponentImpl(new FakeTaskStatusProvider());
    }

    @Test
    void shouldAddPatternsFromPatternProvider() {
        // Arrange
        final PatternProviderNetworkNode provider = new PatternProviderNetworkNode(0, 5);
        provider.setPattern(1, new SimplePattern(A));

        final NetworkNodeContainer container = () -> provider;

        // Act
        sut.onContainerAdded(container);

        // Assert
        assertThat(sut.getOutputs()).usingRecursiveFieldByFieldElementComparator().containsExactly(A);
    }

    @Test
    void shouldRemovePatternsFromPatternProvider() {
        // Arrange
        final PatternProviderNetworkNode provider = new PatternProviderNetworkNode(0, 5);
        provider.setPattern(1, new SimplePattern(A));

        final NetworkNodeContainer container = () -> provider;
        sut.onContainerAdded(container);

        // Act
        sut.onContainerRemoved(container);

        // Assert
        assertThat(sut.getOutputs()).usingRecursiveFieldByFieldElementComparator().isEmpty();
    }

    @Test
    void shouldAddPatternManually() {
        // Arrange
        final SimplePattern pattern = new SimplePattern(A);

        // Act
        sut.add(pattern);

        // Assert
        assertThat(sut.getOutputs()).usingRecursiveFieldByFieldElementComparator().containsExactly(A);
    }

    @Test
    void shouldRemovePatternManually() {
        // Arrange
        final SimplePattern pattern = new SimplePattern(A);
        sut.add(pattern);

        // Act
        sut.remove(pattern);

        // Assert
        assertThat(sut.getOutputs()).usingRecursiveFieldByFieldElementComparator().isEmpty();
    }

    @Test
    void shouldStartTask() {
        sut.startTask(A, 10);
    }

    @Test
    void shouldGetPreview() {
        sut.getPreview(A, 10);
    }
}
