package com.refinedmods.refinedstorage2.api.network.impl.security;

import com.refinedmods.refinedstorage2.api.network.node.NetworkNode;
import com.refinedmods.refinedstorage2.api.network.node.container.NetworkNodeContainer;
import com.refinedmods.refinedstorage2.api.network.security.Permission;
import com.refinedmods.refinedstorage2.api.network.security.SecurityActor;
import com.refinedmods.refinedstorage2.api.network.security.SecurityDecision;
import com.refinedmods.refinedstorage2.api.network.security.SecurityDecisionProvider;
import com.refinedmods.refinedstorage2.api.network.security.SecurityNetworkComponent;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SecurityNetworkComponentImplTest {
    SecurityNetworkComponent sut;

    @BeforeEach
    void setUp() {
        sut = new SecurityNetworkComponentImpl();
    }

    @Test
    void everythingIsAllowedByDefault() {
        // Act & assert
        sut.onContainerAdded(new TestContainer());
        assertThat(sut.isAllowed(TestPermissions.A, TestActors.X)).isTrue();
        assertThat(sut.isAllowed(TestPermissions.B, TestActors.Y)).isFalse();
        sut.onContainerRemoved(new TestContainer());
    }

    enum TestPermissions implements Permission {
        A, B
    }

    enum TestActors implements SecurityActor {
        X, Y
    }

    private static class TestContainer implements SecurityDecisionProvider, NetworkNodeContainer {
        @Override
        public SecurityDecision isAllowed(final Permission permission, final SecurityActor actor) {
            return permission == TestPermissions.A ? SecurityDecision.ALLOW : SecurityDecision.DENY;
        }

        @Override
        public NetworkNode getNode() {
            throw new RuntimeException();
        }
    }
}
