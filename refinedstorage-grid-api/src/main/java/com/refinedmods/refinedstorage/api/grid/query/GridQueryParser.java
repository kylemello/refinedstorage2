package com.refinedmods.refinedstorage.api.grid.query;

import com.refinedmods.refinedstorage.api.grid.view.GridResource;
import com.refinedmods.refinedstorage.api.grid.view.GridView;

import java.util.function.BiPredicate;

import org.apiguardian.api.API;

@API(status = API.Status.STABLE, since = "2.0.0-milestone.1.0")
public interface GridQueryParser {
    BiPredicate<GridView, GridResource> parse(String query) throws GridQueryParserException;
}
