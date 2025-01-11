rootProject.name = "CustomItems"

dependencyResolutionManagement {
    repositories {
        mavenLocal()
        mavenCentral()
        maven {
            url = uri("https://repo.not-null.co.uk/snapshots/")
        }
        maven {
            url = uri("https://repo.papermc.io/repository/maven-public/")
        }
    }
}

include("CustomItemsAPI")
