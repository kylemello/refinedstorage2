package com.refinedmods.refinedstorage.api.autocrafting;

import com.refinedmods.refinedstorage.api.resource.ResourceKey;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class PatternRepositoryImpl implements PatternRepository {
    private final Set<Pattern> patterns = new HashSet<>();
    private final Set<Pattern> patternsView = Collections.unmodifiableSet(patterns);
    private final Set<ResourceKey> outputs = new HashSet<>();

    @Override
    public void add(final Pattern pattern) {
        patterns.add(pattern);
        outputs.addAll(pattern.getOutputResources());
    }

    @Override
    public void remove(final Pattern pattern) {
        patterns.remove(pattern);
        for (final ResourceKey output : pattern.getOutputResources()) {
            final boolean noOtherPatternHasThisOutput = patterns.stream()
                .noneMatch(otherPattern -> otherPattern.getOutputResources().contains(output));
            if (noOtherPatternHasThisOutput) {
                outputs.remove(output);
            }
        }
    }

    @Override
    public Set<ResourceKey> getOutputs() {
        return outputs;
    }

    @Override
    public Set<Pattern> getAll() {
        return patternsView;
    }
}
