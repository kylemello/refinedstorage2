package com.refinedmods.refinedstorage.api.autocrafting;

import java.util.Objects;
import java.util.UUID;

public abstract class AbstractPattern implements Pattern {
    private final UUID id;

    public AbstractPattern(final UUID id) {
        this.id = id;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final AbstractPattern that = (AbstractPattern) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
