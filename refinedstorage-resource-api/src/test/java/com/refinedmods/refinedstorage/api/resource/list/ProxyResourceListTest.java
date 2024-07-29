package com.refinedmods.refinedstorage.api.resource.list;

class ProxyResourceListTest extends AbstractResourceListTest {
    @Override
    protected ResourceList createList() {
        return new AbstractProxyResourceList(ResourceListImpl.create()) {
        };
    }
}
