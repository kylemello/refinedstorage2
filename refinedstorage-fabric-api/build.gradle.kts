plugins {
    id("refinedarchitect.fabric")
}

refinedarchitect {
    fabric()
}

dependencies {
    compileOnly(project(":refinedstorage-common-api"))
    api(libs.apiguardian)
}

base {
    archivesName.set("refinedstorage-fabric-api")
}
