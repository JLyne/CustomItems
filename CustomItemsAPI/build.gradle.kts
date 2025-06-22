plugins {
    java
	`maven-publish`
}

description = "CustomItems API"

dependencies {
	compileOnly(libs.paperApi)
}

publishing {
    publications {
        create<MavenPublication>("library") {
            from(components.getByName("java"))
            pom {
                description = "CustomItems API"
                url = "https://github.com/JLyne/CustomItems"
                developers {
                    developer {
                        id = "jim"
                        name = "James Lyne"
                    }
                }
                scm {
                    connection = "scm:git:git://github.com/JLyne/CustomItems.git"
                    developerConnection = "scm:git:ssh://github.com/JLyne/CustomItems.git"
                    url = "https://github.com/JLyne/CustomItems"
                }
            }
        }
    }
    repositories {
        maven {
            name = "notnull"
            credentials(PasswordCredentials::class)
            val releasesRepoUrl = uri("https://repo.not-null.co.uk/releases/") // gradle -Prelease publish
            val snapshotsRepoUrl = uri("https://repo.not-null.co.uk/snapshots/")
            url = if (project.hasProperty("release")) releasesRepoUrl else snapshotsRepoUrl
        }
    }
}
