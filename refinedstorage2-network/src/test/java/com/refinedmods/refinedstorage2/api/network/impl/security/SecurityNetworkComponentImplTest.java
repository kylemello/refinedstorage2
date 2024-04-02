package com.refinedmods.refinedstorage2.api.network.impl.security;

import com.refinedmods.refinedstorage2.api.network.impl.node.security.SecurityDecisionProviderProxyNetworkNode;
import com.refinedmods.refinedstorage2.api.network.node.NetworkNode;
import com.refinedmods.refinedstorage2.api.network.security.Permission;
import com.refinedmods.refinedstorage2.api.network.security.SecurityActor;
import com.refinedmods.refinedstorage2.api.network.security.SecurityNetworkComponent;
import com.refinedmods.refinedstorage2.api.network.security.SecurityPolicy;

import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SecurityNetworkComponentImplTest {
    SecurityNetworkComponent sut;
    SecurityDecisionProviderImpl securityDecisionProvider;

    @BeforeEach
    void setUp() {
        sut = new SecurityNetworkComponentImpl(policy(TestPermissions.ALLOW_BY_DEFAULT));
        securityDecisionProvider = new SecurityDecisionProviderImpl();
    }

    @Test
    void shouldUseDefaultPolicyIfNoSecurityDecisionProvidersArePresent() {
        // Act & assert
        assertThat(sut.isAllowed(TestPermissions.ALLOW_BY_DEFAULT, TestActors.A)).isTrue();
        assertThat(sut.isAllowed(TestPermissions.OTHER, TestActors.A)).isFalse();
    }

    @Test
    void shouldUseDefaultPolicyIfANotConfiguredSecurityDecisionProviderIsPresent() {
        // Arrange
        sut.onContainerAdded(() -> new SecurityDecisionProviderProxyNetworkNode(0, securityDecisionProvider));

        // Act & assert
        assertThat(sut.isAllowed(TestPermissions.ALLOW_BY_DEFAULT, TestActors.A)).isTrue();
        assertThat(sut.isAllowed(TestPermissions.OTHER, TestActors.A)).isFalse();
    }

    @Test
    void shouldUseDefaultPolicyIfAConfiguredSecurityDecisionProviderIsPresentForAnotherActor() {
        // Arrange
        sut.onContainerAdded(() -> new SecurityDecisionProviderProxyNetworkNode(0, securityDecisionProvider));
        securityDecisionProvider.setPolicy(TestActors.B, policy(TestPermissions.OTHER));

        // Act & assert
        assertThat(sut.isAllowed(TestPermissions.ALLOW_BY_DEFAULT, TestActors.A)).isTrue();
        assertThat(sut.isAllowed(TestPermissions.OTHER, TestActors.A)).isFalse();
    }

    @Test
    void shouldDenyPermissionIfAConfiguredSecurityDecisionProviderIsPresentForTheActor() {
        // Arrange
        sut.onContainerAdded(() -> new SecurityDecisionProviderProxyNetworkNode(0, securityDecisionProvider));
        securityDecisionProvider.setPolicy(TestActors.B, policy(TestPermissions.OTHER));

        // Act & assert
        assertThat(sut.isAllowed(TestPermissions.ALLOW_BY_DEFAULT, TestActors.B)).isFalse();
        assertThat(sut.isAllowed(TestPermissions.OTHER, TestActors.B)).isTrue();
    }

    @Test
    void shouldUseFirstSecurityDecisionProviderThatIsConfiguredForActor() {
        // Arrange
        sut.onContainerAdded(() -> new SecurityDecisionProviderProxyNetworkNode(0, new SecurityDecisionProviderImpl()
            .setPolicy(TestActors.A, policy(TestPermissions.OTHER2))));

        sut.onContainerAdded(() -> new SecurityDecisionProviderProxyNetworkNode(0, securityDecisionProvider));
        securityDecisionProvider.setPolicy(TestActors.A, policy(TestPermissions.ALLOW_BY_DEFAULT)); // will be ignored
        securityDecisionProvider.setPolicy(TestActors.B, policy(TestPermissions.OTHER));

        // Act & assert
        assertThat(sut.isAllowed(TestPermissions.ALLOW_BY_DEFAULT, TestActors.A)).isFalse();
        assertThat(sut.isAllowed(TestPermissions.OTHER, TestActors.A)).isFalse();
        assertThat(sut.isAllowed(TestPermissions.OTHER2, TestActors.A)).isTrue();

        assertThat(sut.isAllowed(TestPermissions.ALLOW_BY_DEFAULT, TestActors.B)).isFalse();
        assertThat(sut.isAllowed(TestPermissions.OTHER, TestActors.B)).isTrue();
        assertThat(sut.isAllowed(TestPermissions.OTHER2, TestActors.B)).isFalse();
    }

    @Test
    void shouldRemoveSecurityDecisionProvider() {
        // Arrange
        final NetworkNode node = new SecurityDecisionProviderProxyNetworkNode(0, securityDecisionProvider);
        sut.onContainerAdded(() -> node);
        securityDecisionProvider.setPolicy(TestActors.B, policy(TestPermissions.OTHER));

        // Act
        sut.onContainerRemoved(() -> node);
        sut.onContainerAdded(() -> new SecurityDecisionProviderProxyNetworkNode(0, new SecurityDecisionProviderImpl()
            .setPolicy(TestActors.B, policy(TestPermissions.OTHER2))));

        // Assert
        assertThat(sut.isAllowed(TestPermissions.ALLOW_BY_DEFAULT, TestActors.B)).isFalse();
        assertThat(sut.isAllowed(TestPermissions.OTHER, TestActors.B)).isFalse();
        assertThat(sut.isAllowed(TestPermissions.OTHER2, TestActors.B)).isTrue();
    }

    enum TestPermissions implements Permission {
        ALLOW_BY_DEFAULT, OTHER, OTHER2
    }

    enum TestActors implements SecurityActor {
        A, B
    }

    private SecurityPolicy policy(final Permission... permissions) {
        return new SecurityPolicy(Set.of(permissions));
    }
}
