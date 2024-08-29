plugins {
    id("refinedarchitect.neoforge")
}

refinedarchitect {
    modId = "refinedstorage"
    neoForge()
    gameTests()
    dataGeneration(project(":refinedstorage-common"))
    compileWithProject(project(":refinedstorage-common"))
    compileWithProject(project(":refinedstorage-common-api"))
    compileWithProject(project(":refinedstorage-neoforge-api"))
    compileWithProject(project(":refinedstorage-core-api"))
    compileWithProject(project(":refinedstorage-resource-api"))
    compileWithProject(project(":refinedstorage-storage-api"))
    compileWithProject(project(":refinedstorage-network-api"))
    compileWithProject(project(":refinedstorage-network"))
    compileWithProject(project(":refinedstorage-grid-api"))
    compileWithProject(project(":refinedstorage-autocrafting-api"))
    compileWithProject(project(":refinedstorage-query-parser"))
    publishing {
        maven = true
        // curseForge = "243076"
        // modrinth = "refined-storage"
    }
}

base {
    archivesName.set("refinedstorage-neoforge")
}
