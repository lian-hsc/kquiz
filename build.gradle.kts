import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  `maven-publish`
  kotlin("jvm") version "2.3.10"
}

group = "me.lian-hsc"
version = "1.0-SNAPSHOT"

repositories {
  mavenCentral()
  mavenLocal()
}

dependencies {
  implementation("tools.jackson.dataformat:jackson-dataformat-xml:3.1.3")
  implementation("me.lian-hsc.ktypst:backend-command:1.0-SNAPSHOT")
  implementation("me.lian-hsc.ktypst:structures:1.0-SNAPSHOT")
}

kotlin {
  jvmToolchain(21)
}

tasks {
  compileKotlin {
    compilerOptions.freeCompilerArgs.add("-Xannotation-default-target=param-property")
  }
}

publishing {
  repositories {
    mavenLocal()
  }

  publications {
    create<MavenPublication>("maven") {
      from(components["java"])

      artifactId = project.path
        .removePrefix(":")
        .replace(":", "-")
        .ifBlank { rootProject.name }
    }
  }
}