plugins {
  id("java-library")
  `maven-publish`
  signing
}

group = "com.pkware.ahocorasick"
version = property("POM_VERSION") as String

dependencies {
  testImplementation("junit:junit:4.13.2")
}

repositories {
  mavenCentral()
}

java {
  withJavadocJar()
  withSourcesJar()
}

publishing {
  publications {
    register("java", MavenPublication::class.java) {
      artifactId = "aho-corasick"
      from(components["java"])
      pom {
        name.set("Aho-Corasick")
        packaging = "jar"
        description.set("The Aho-Corasick string searching algorithm.")
        organization {
          name.set("PKWARE, Inc.")
          url.set("https://www.pkware.com")
        }

        scm {
          connection.set("scm:git:git://github.com/pkware/aho-corasick.git")
          developerConnection.set("scm:git:ssh://github.com/pkware/aho-corasick.git")
          url.set("https://github.com/pkware/aho-corasick")
        }

        licenses {
          license {
            name.set("BSD License")
            distribution.set("repo")
            url.set("https://github.com/pkware/aho-corasick/blob/master/LICENSE")
          }
        }
      }
    }
  }

  repositories {
    maven {
      val releasesRepoUrl = uri("https://oss.sonatype.org/service/local/staging/deploy/maven2/")
      val snapshotsRepoUrl = uri("https://oss.sonatype.org/content/repositories/snapshots/")
      url = if (version.toString().endsWith("SNAPSHOT")) snapshotsRepoUrl else releasesRepoUrl
      credentials {
        username = getMavenCentralUsername()
        password = getMavenCentralPassword()
      }
    }
  }
}

signing {
  if (System.getenv("CI").toBoolean()) {
    val signingKey: String? by project
    val signingPassword: String? by project
    useInMemoryPgpKeys(signingKey, signingPassword)
  }

  // For local signing, follow these steps https://docs.gradle.org/current/userguide/signing_plugin.html#sec:signatory_credentials

  sign(publishing.publications["java"])
}

fun getMavenCentralUsername(): String = if (hasProperty("SONATYPE_NEXUS_USERNAME")) {
  property("SONATYPE_NEXUS_USERNAME") as String
} else {
  System.getenv("SONATYPE_NEXUS_USERNAME") ?: ""
}

fun getMavenCentralPassword(): String = if (hasProperty("SONATYPE_NEXUS_PASSWORD")) {
  property("SONATYPE_NEXUS_PASSWORD") as String
} else {
  System.getenv("SONATYPE_NEXUS_PASSWORD") ?: ""
}