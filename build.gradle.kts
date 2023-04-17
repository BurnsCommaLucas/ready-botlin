import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jlleitschuh.gradle.ktlint.KtlintExtension

plugins {
    id("org.springframework.boot") version "2.7.10"
    id("io.spring.dependency-management") version "1.1.0"
    kotlin("jvm") version "1.8.20"
    kotlin("plugin.spring") version "1.8.20"
    id("org.jlleitschuh.gradle.ktlint") version "11.3.1"
}

group = "io.burnscommalucas"
version = "1.0.0"
java.sourceCompatibility = JavaVersion.VERSION_1_8

repositories {
    mavenCentral()
}

configure<KtlintExtension> {
    disabledRules.set(
        setOf(
            "final-newline",
            "multiline-if-else",
            "comment-spacing",
            "trailing-comma-on-declaration-site",
            "trailing-comma-on-call-site"
        )
    )
}

dependencies {
    api("org.springframework.boot:spring-boot-starter-data-mongodb-reactive")
//    implementation("org.springframework.data:spring-data-commons")
//    implementation("org.springframework.boot:spring-boot-starter-hateoas")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
    implementation("javax.annotation:javax.annotation-api:1.3.2")

    implementation("org.litote.kmongo:kmongo-reactor:4.9.0")
    implementation("com.discord4j:discord4j-core:3.2.4")

    testImplementation("io.mockk:mockk:1.13.4")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("io.projectreactor:reactor-test")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}
