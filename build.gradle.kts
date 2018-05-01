import io.spring.gradle.dependencymanagement.dsl.DependencyManagementExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinPluginWrapper
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
    val kotlinVersion = "1.2.30"
    id("org.springframework.boot") version "2.0.1.RELEASE"
    id("org.jetbrains.kotlin.jvm") version kotlinVersion
    id("org.jetbrains.kotlin.plugin.spring") version kotlinVersion
    id("org.jetbrains.kotlin.plugin.jpa") version kotlinVersion
    id("io.spring.dependency-management") version "1.0.4.RELEASE"
}

extra["kotlin.version"] = plugins.getPlugin(KotlinPluginWrapper::class.java).kotlinPluginVersion

group = "io.gontrum.shorturl"

repositories {
    mavenCentral()
}

tasks {
    withType<KotlinCompile> {
        kotlinOptions {
            jvmTarget = "1.8"
            freeCompilerArgs = listOf("-Xjsr305=strict")
        }
    }

    "buildWithDevtools" {
        dependsOn("build")
        doFirst {
            withType<BootJar> {
                isExcludeDevtools = false
            }
        }
    }
}

dependencies {
    compile("org.springframework.boot:spring-boot-starter-web")
    compile("org.springframework.boot:spring-boot-starter-data-mongodb")
    compile("org.springframework.boot:spring-boot-starter-security")
    compile("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    compile("org.jetbrains.kotlin:kotlin-reflect")
    compile("com.fasterxml.jackson.module:jackson-module-kotlin")
    compile("com.fasterxml.jackson.module:jackson-module-kotlin:2.9.+")
    //compile("org.springframework.security:spring-security-jwt:1.0.9.RELEASE")
    //compile("org.springframework.security.oauth:spring-security-oauth2:2.1.0.RELEASE")
    compile("eu.bitwalker:UserAgentUtils:1.21")

    compile("com.google.guava:guava:25.0-jre")
    compile("cz.jirutka.spring:embedmongo-spring:1.3.1")
    compile("de.flapdoodle.embed:de.flapdoodle.embed.mongo:2.0.3")
    testCompile("io.cucumber:cucumber-java8:2.3.1")
    testCompile("io.cucumber:cucumber-spring:2.3.1")
    testCompile("io.cucumber:cucumber-junit:2.3.1")
    testCompile("com.natpryce:hamkrest:1.4.2.2")
    testCompile("com.nhaarman:mockito-kotlin:1.5.0")

    testCompile("org.springframework.boot:spring-boot-starter-test") {
        exclude(group = "org.mockito")
    }
}
