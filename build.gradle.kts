import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  kotlin("jvm") version "2.3.10"
}

group = "me.lian-hsc"
version = "1.0-SNAPSHOT"

repositories {
  mavenCentral()
}

dependencies {
  implementation("tools.jackson.dataformat:jackson-dataformat-xml:3.1.3")
}

kotlin {
  jvmToolchain(21)
}

tasks {
  compileKotlin {
    compilerOptions.freeCompilerArgs.add("-Xannotation-default-target=param-property")
  }
}
