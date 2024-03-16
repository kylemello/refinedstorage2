package com.refinedmods.refinedstorage2.api.network.impl.security;

import com.refinedmods.refinedstorage2.api.network.security.Operation;
import com.refinedmods.refinedstorage2.api.network.security.SecurityActor;
import com.refinedmods.refinedstorage2.api.network.security.SecurityNetworkComponent;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.assertj.core.api.Assertions.assertThat;

class SecurityNetworkComponentImplTest {
    SecurityNetworkComponent sut;

    @BeforeEach
    void setUp() {
        sut = new SecurityNetworkComponentImpl();
    }

    @EnumSource(TestPermissions.class)
    @ParameterizedTest
    void everythingIsAllowedByDefault(final TestPermissions permission) {
        // Act & assert
        assertThat(sut.isAllowed(permission, TestActors.X)).isTrue();
        assertThat(sut.isAllowed(permission, TestActors.Y)).isTrue();
    }

    enum TestPermissions implements Operation {
        A, B
    }

    enum TestActors implements SecurityActor {
        X, Y
    }
}
