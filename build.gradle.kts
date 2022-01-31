import com.github.davidmc24.gradle.plugin.avro.GenerateAvroJavaTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "2.6.3"
    id("io.spring.dependency-management") version "1.0.11.RELEASE"
    id("com.github.davidmc24.gradle.plugin.avro") version "1.3.0"
    kotlin("jvm") version "1.6.10"
    kotlin("plugin.spring") version "1.6.10"
}

group = "com.github.edineipiovesan"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_11

repositories {
    mavenCentral()
    gradlePluginPortal()
    maven(url = uri("https://packages.confluent.io/maven/"))
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.springframework.kafka:spring-kafka")

    implementation(group = "org.apache.avro", name = "avro", version = "1.11.0")
    implementation(group = "io.confluent", name = "kafka-avro-serializer", version = "7.0.1")
    implementation(group = "io.confluent", name = "kafka-streams-avro-serde", version = "7.0.1")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.kafka:spring-kafka-test")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "11"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

val generateAvro = tasks.named("generateAvroJava", GenerateAvroJavaTask::class.java) {
    setSource("src/main/resources/avro")
    setOutputDir(File("src/main/java"))
}

tasks.withType<JavaCompile> {
    source(generateAvro)
}

avro {
    outputCharacterEncoding.set("UTF-8")
}

tasks.named("clean", Delete::class) {
    delete("src/main/java")
}