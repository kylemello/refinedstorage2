package com.refinedmods.refinedstorage2.api.network.impl.security;

import com.refinedmods.refinedstorage2.api.network.impl.node.security.SecurityDecisionProviderProxyNetworkNode;
import com.refinedmods.refinedstorage2.api.network.security.Permission;
import com.refinedmods.refinedstorage2.api.network.security.SecurityActor;
import com.refinedmods.refinedstorage2.api.network.security.SecurityDecisionProvider;
import com.refinedmods.refinedstorage2.api.network.security.SecurityNetworkComponent;
import com.refinedmods.refinedstorage2.api.network.security.SecurityPolicy;

import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SecurityNetworkComponentImplTest {
    SecurityNetworkComponent sut;
    SecurityDecisionProviderImpl securityDecisionProvider;
    SecurityDecisionProviderProxyNetworkNode node;

    @BeforeEach
    void setUp() {
        sut = new SecurityNetworkComponentImpl(policy(TestPermissions.ALLOW_BY_DEFAULT));
        securityDecisionProvider = new SecurityDecisionProviderImpl();
        node = createNode(securityDecisionProvider);
    }

    @Test
    void shouldUseDefaultPolicyIfNoSecurityDecisionProvidersArePresent() {
        // Act & assert
        assertThat(sut.isAllowed(TestPermissions.ALLOW_BY_DEFAULT, TestActors.A)).isTrue();
        assertThat(sut.isAllowed(TestPermissions.OTHER, TestActors.A)).isFalse();
        assertThat(sut.isAllowed(TestPermissions.OTHER2, TestActors.A)).isFalse();

        assertThat(sut.isAllowed(TestPermissions.ALLOW_BY_DEFAULT, TestActors.B)).isTrue();
        assertThat(sut.isAllowed(TestPermissions.OTHER, TestActors.B)).isFalse();
        assertThat(sut.isAllowed(TestPermissions.OTHER2, TestActors.B)).isFalse();
    }

    @Test
    void shouldDenyAllIfAtLeastOneSecurityDecisionProviderIsPresent() {
        // Arrange
        sut.onContainerAdded(() -> node);

        // Act & assert
        assertThat(sut.isAllowed(TestPermissions.ALLOW_BY_DEFAULT, TestActors.A)).isFalse();
        assertThat(sut.isAllowed(TestPermissions.OTHER, TestActors.A)).isFalse();
        assertThat(sut.isAllowed(TestPermissions.OTHER2, TestActors.A)).isFalse();

        assertThat(sut.isAllowed(TestPermissions.ALLOW_BY_DEFAULT, TestActors.B)).isFalse();
        assertThat(sut.isAllowed(TestPermissions.OTHER, TestActors.B)).isFalse();
        assertThat(sut.isAllowed(TestPermissions.OTHER2, TestActors.B)).isFalse();
    }

    @Test
    void shouldUseDefaultPolicyIfAllSecurityDecisionProvidersAreInactive() {
        // Arrange
        sut.onContainerAdded(() -> createNode(new SecurityDecisionProviderImpl()
            .setDefaultPolicy(policy(TestPermissions.OTHER)), false));

        // Act & assert
        assertThat(sut.isAllowed(TestPermissions.ALLOW_BY_DEFAULT, TestActors.A)).isTrue();
        assertThat(sut.isAllowed(TestPermissions.OTHER, TestActors.A)).isFalse();
        assertThat(sut.isAllowed(TestPermissions.OTHER2, TestActors.A)).isFalse();

        assertThat(sut.isAllowed(TestPermissions.ALLOW_BY_DEFAULT, TestActors.B)).isTrue();
        assertThat(sut.isAllowed(TestPermissions.OTHER, TestActors.B)).isFalse();
        assertThat(sut.isAllowed(TestPermissions.OTHER2, TestActors.B)).isFalse();
    }

    @Test
    void shouldAllowOrDeny() {
        // Arrange
        securityDecisionProvider.setPolicy(TestActors.A, policy(TestPermissions.OTHER));
        sut.onContainerAdded(() -> node);

        // Act & assert
        assertThat(sut.isAllowed(TestPermissions.ALLOW_BY_DEFAULT, TestActors.A)).isFalse();
        assertThat(sut.isAllowed(TestPermissions.OTHER, TestActors.A)).isTrue();
        assertThat(sut.isAllowed(TestPermissions.OTHER2, TestActors.A)).isFalse();

        assertThat(sut.isAllowed(TestPermissions.ALLOW_BY_DEFAULT, TestActors.B)).isFalse();
        assertThat(sut.isAllowed(TestPermissions.OTHER, TestActors.B)).isFalse();
        assertThat(sut.isAllowed(TestPermissions.OTHER2, TestActors.B)).isFalse();
    }

    @Test
    void shouldOnlyAllowIfAllSecurityDecisionProvidersAllow() {
        // Arrange
        sut.onContainerAdded(() -> createNode(new SecurityDecisionProviderImpl()
            .setPolicy(TestActors.A, policy(TestPermissions.OTHER))
        ));

        sut.onContainerAdded(() -> createNode(new SecurityDecisionProviderImpl()
            .setPolicy(TestActors.A, policy(TestPermissions.OTHER2))
        ));

        sut.onContainerAdded(() -> createNode(new SecurityDecisionProviderImpl()
            .setPolicy(TestActors.B, policy(TestPermissions.OTHER))
        ));

        sut.onContainerAdded(() -> createNode(new SecurityDecisionProviderImpl()
            .setPolicy(TestActors.A, policy(TestPermissions.ALLOW_BY_DEFAULT))
            .setDefaultPolicy(policy(TestPermissions.OTHER, TestPermissions.OTHER2)), false));

        // Act & assert
        assertThat(sut.isAllowed(TestPermissions.OTHER, TestActors.A)).isFalse();
        assertThat(sut.isAllowed(TestPermissions.OTHER2, TestActors.A)).isFalse();

        assertThat(sut.isAllowed(TestPermissions.OTHER, TestActors.B)).isTrue();
        assertThat(sut.isAllowed(TestPermissions.OTHER2, TestActors.B)).isFalse();

        assertThat(sut.isAllowed(TestPermissions.OTHER, TestActors.C)).isFalse();
        assertThat(sut.isAllowed(TestPermissions.OTHER2, TestActors.C)).isFalse();
    }

    @Test
    void shouldUseDefaultPolicyOfSecurityDecisionProviderIfAllProvidersPassDecision() {
        // Arrange
        sut.onContainerAdded(() -> createNode(new SecurityDecisionProviderImpl()
            .setPolicy(TestActors.A, policy(TestPermissions.OTHER))
            .setDefaultPolicy(policy(TestPermissions.ALLOW_BY_DEFAULT))
        ));

        sut.onContainerAdded(() -> createNode(new SecurityDecisionProviderImpl()
            .setPolicy(TestActors.A, policy(TestPermissions.OTHER))
            .setDefaultPolicy(policy(TestPermissions.ALLOW_BY_DEFAULT, TestPermissions.OTHER2))
        ));

        sut.onContainerAdded(() -> createNode(new SecurityDecisionProviderImpl()
            .setPolicy(TestActors.C, policy(TestPermissions.OTHER))
        ));

        // Act & assert
        assertThat(sut.isAllowed(TestPermissions.ALLOW_BY_DEFAULT, TestActors.A)).isFalse();
        assertThat(sut.isAllowed(TestPermissions.OTHER, TestActors.A)).isTrue();
        assertThat(sut.isAllowed(TestPermissions.OTHER2, TestActors.A)).isFalse();

        assertThat(sut.isAllowed(TestPermissions.ALLOW_BY_DEFAULT, TestActors.B)).isTrue();
        assertThat(sut.isAllowed(TestPermissions.OTHER, TestActors.B)).isFalse();
        assertThat(sut.isAllowed(TestPermissions.OTHER2, TestActors.B)).isFalse();

        assertThat(sut.isAllowed(TestPermissions.ALLOW_BY_DEFAULT, TestActors.C)).isFalse();
        assertThat(sut.isAllowed(TestPermissions.OTHER, TestActors.C)).isTrue();
        assertThat(sut.isAllowed(TestPermissions.OTHER2, TestActors.C)).isFalse();
    }

    @Test
    void shouldRemoveContainer() {
        // Arrange
        sut.onContainerAdded(() -> createNode(new SecurityDecisionProviderImpl()
            .setDefaultPolicy(policy(TestPermissions.ALLOW_BY_DEFAULT))
        ));

        final var removedNode = createNode(new SecurityDecisionProviderImpl()
            .setDefaultPolicy(policy(TestPermissions.OTHER)));
        sut.onContainerAdded(() -> removedNode);

        // Act
        sut.onContainerRemoved(() -> removedNode);

        // Assert
        assertThat(sut.isAllowed(TestPermissions.ALLOW_BY_DEFAULT, TestActors.A)).isTrue();
    }

    @Test
    void shouldClearPolicies() {
        // Arrange
        sut.onContainerAdded(() -> node);
        securityDecisionProvider.setPolicy(TestActors.A, policy(TestPermissions.OTHER));
        securityDecisionProvider.setDefaultPolicy(policy(TestPermissions.OTHER2));

        // Act
        securityDecisionProvider.clearPolicies();

        // Assert
        assertThat(sut.isAllowed(TestPermissions.OTHER, TestActors.A)).isFalse();
        assertThat(sut.isAllowed(TestPermissions.OTHER2, TestActors.A)).isTrue();
    }

    private static SecurityDecisionProviderProxyNetworkNode createNode(final SecurityDecisionProvider provider) {
        return createNode(provider, true);
    }

    private static SecurityDecisionProviderProxyNetworkNode createNode(final SecurityDecisionProvider provider,
                                                                       final boolean active) {
        final var node = new SecurityDecisionProviderProxyNetworkNode(0, provider);
        node.setActive(active);
        return node;
    }

    enum TestPermissions implements Permission {
        ALLOW_BY_DEFAULT, OTHER, OTHER2
    }

    enum TestActors implements SecurityActor {
        A, B, C
    }

    private SecurityPolicy policy(final Permission... permissions) {
        return new SecurityPolicy(Set.of(permissions));
    }
}
