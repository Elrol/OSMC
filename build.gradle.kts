plugins {
    id("java")
    id("architectury-plugin") version("3.4-SNAPSHOT")
    id("dev.architectury.loom") version("1.13-SNAPSHOT")
    kotlin("jvm") version "2.2.0"
    id("maven-publish")
}

val minecraftVersion: String by project
val fabricAPIVersion: String by project
val fabricLoaderVersion: String by project
val yarnMappings: String by project
val osmcVersion: String by project
val cobblemonVersion: String by project

base {
    archivesName.set("${project.property("archives_base_name")}-${osmcVersion}")
}

architectury {
    platformSetupLoomIde()
    fabric()
}

loom {
    silentMojangMappingsLicense()

    mixin {
        defaultRefmapName.set("mixins.${project.name}.refmap.json")
    }
}

repositories {
    mavenCentral()
    maven(url = "https://maven.tomalbrc.de")
    maven(url = "https://maven.nucleoid.xyz")
    maven(url = "https://dl.cloudsmith.io/public/geckolib3/geckolib/maven/")
    maven(url = "https://maven.impactdev.net/repository/development/")
    maven(url = "https://oss.sonatype.org/content/repositories/snapshots")
    maven(url = "https://jitpack.io")
    maven(url = "https://repo.phoenix616.dev")
    maven(url = "https://maven.cobblemon.com")
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-metadata-jvm:2.2.0")
    minecraft("com.mojang:minecraft:${minecraftVersion}")
    mappings("net.fabricmc:yarn:${yarnMappings}:v2")

    modImplementation("net.fabricmc:fabric-loader:${fabricLoaderVersion}")
    modImplementation("net.fabricmc:fabric-language-kotlin:1.13.8+kotlin.2.3.0")
    modImplementation("net.fabricmc.fabric-api:fabric-api:${fabricAPIVersion}+${minecraftVersion}")
    modImplementation(fabricApi.module("fabric-command-api-v2", "0.104.0+1.21.1"))

    modImplementation("com.cobblemon:fabric:${cobblemonVersion}+${minecraftVersion}")

    implementation("net.objecthunter:exp4j:0.4.8")
}

tasks.processResources {
    inputs.property("version", project.version)
    inputs.property("minecraft_version", "${minecraftVersion}")
    inputs.property("loader_version", "${fabricLoaderVersion}")
    filteringCharset = "UTF-8"

    filesMatching("fabric.mod.json") {
        // FIX 2: Explicitly casting properties to String to avoid "Pair<String, Any?>" mismatch
        expand(
            "version" to project.version,
            "minecraft_version" to "${minecraftVersion}",
            "loader_version" to "${fabricLoaderVersion}"
        )
    }
}

val targetJavaVersion = 21
tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
    if (targetJavaVersion >= 10 || JavaVersion.current().isJava10Compatible) {
        options.release.set(targetJavaVersion)
    }
}

java {
    val javaVersion = JavaVersion.toVersion(targetJavaVersion)
    if (JavaVersion.current() < javaVersion) {
        toolchain.languageVersion.set(JavaLanguageVersion.of(targetJavaVersion))
    }
    withSourcesJar()
}

tasks.jar {
    from("LICENSE") {
        rename { "${it}_${base.archivesName.get()}" }
    }
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            artifactId = project.property("archives_base_name") as String
            from(components["java"])
        }
    }
}