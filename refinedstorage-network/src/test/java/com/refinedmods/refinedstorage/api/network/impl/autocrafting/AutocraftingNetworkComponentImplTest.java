package com.refinedmods.refinedstorage.api.network.impl.autocrafting;

import com.refinedmods.refinedstorage.api.autocrafting.Pattern;
import com.refinedmods.refinedstorage.api.autocrafting.TaskId;
import com.refinedmods.refinedstorage.api.autocrafting.status.TaskStatus;
import com.refinedmods.refinedstorage.api.autocrafting.status.TaskStatusListener;
import com.refinedmods.refinedstorage.api.network.autocrafting.PatternListener;
import com.refinedmods.refinedstorage.api.network.impl.node.patternprovider.PatternProviderNetworkNode;
import com.refinedmods.refinedstorage.api.network.node.container.NetworkNodeContainer;
import com.refinedmods.refinedstorage.network.test.fixtures.FakeTaskStatusProvider;

import java.util.UUID;

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
    void temporaryCoverage() {
        final PatternListener listener = new PatternListener() {
            @Override
            public void onAdded(final Pattern pattern) {
                // no op
            }

            @Override
            public void onRemoved(final Pattern pattern) {
                // no op
            }
        };
        sut.addListener(listener);
        sut.removeListener(listener);
        final TaskStatusListener listener2 = new TaskStatusListener() {
            @Override
            public void taskStatusChanged(final TaskStatus status) {
                // no op
            }

            @Override
            public void taskRemoved(final TaskId id) {
                // no op
            }

            @Override
            public void taskAdded(final TaskStatus status) {
                // no op
            }
        };
        sut.addListener(listener2);
        sut.removeListener(listener2);
        sut.getStatuses();
        sut.cancel(new TaskId(UUID.randomUUID()));
        sut.cancelAll();
        sut.testUpdate();
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
