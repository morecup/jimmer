plugins {
    `kotlin-dsl`
    kotlin("jvm") version "2.1.20" apply false
}

dependencies {
    api("org.jetbrains.kotlin:kotlin-gradle-plugin:2.1.20")
    api("org.jetbrains.dokka:dokka-gradle-plugin:2.0.0")
    api("com.vanniktech:gradle-maven-publish-plugin:0.31.0")
}
