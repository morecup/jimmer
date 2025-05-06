import com.vanniktech.maven.publish.JavaLibrary
import com.vanniktech.maven.publish.JavadocJar
import com.vanniktech.maven.publish.SonatypeHost
import org.babyfish.jimmer.MavenPublishConfig

plugins {
    `java-library`
//    id("publish-convention")
    id("com.vanniktech.maven.publish")
}

extensions.configure<JavaPluginExtension> {
    sourceCompatibility = JavaVersion.VERSION_1_8
    withSourcesJar()
    withJavadocJar()
}

tasks.test {
    useJUnitPlatform()
}
tasks.withType<JavaCompile>().configureEach {
    options.compilerArgs.add("-parameters")
}
tasks.withType<Javadoc>().configureEach {
    options.encoding = "UTF-8"
}


mavenPublishing {
    publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL)
    signAllPublications()

    coordinates(MavenPublishConfig.GROUP_ID, project.name, MavenPublishConfig.VERSION)

    configure(JavaLibrary(
        // configures the -javadoc artifact, possible values:
        // - `JavadocJar.None()` don't publish this artifact
        // - `JavadocJar.Empty()` publish an emprt jar
        // - `JavadocJar.Dokka("dokkaHtml")` when using Kotlin with Dokka, where `dokkaHtml` is the name of the Dokka task that should be used as input
        JavadocJar.None(),
//        javadocJar = JavadocJar.Dokka("dokkaHtml"),
        // whether to publish a sources jar
        sourcesJar = true,
    ))

    pom {
        name = "morecup"
        description = "base on ${project.version}"
        url = "https://github.com/morecup/jimmer"
        licenses {
            license {
                name = "Apache-2.0"
                url = "https://spdx.org/licenses/Apache-2.0.html"
            }
        }

        developers {
            developer {
                id = "morecup/jimmer"
                name = "morecup"
                url = "https://github.com/morecup"
            }
        }
        scm {
            url = "https://github.com/morecup/jimmer"
            connection = "scm:git:git@github.com:morecup/jimmer.git"
            developerConnection = "scm:git:ssh://git@github.com:morecup/jimmer.git"
        }
    }
}