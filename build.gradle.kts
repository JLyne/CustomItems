import net.minecrell.pluginyml.bukkit.BukkitPluginDescription

plugins {
    java
    alias(libs.plugins.pluginYmlPaper)
}

group = "uk.co.notnull"
version = "1.0-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

dependencies {
    implementation(project(":CustomItemsAPI"))

    compileOnly(libs.paperApi)
    compileOnly(libs.jetbrainsAnnoations)
    paperLibrary(libs.messagesHelper)
}

tasks {
    compileJava {
        options.compilerArgs.addAll(listOf("-Xlint:all", "-Xlint:-processing"))
        options.encoding = "UTF-8"
    }

    // Include API in plugin jar
    jar {
        from(project(":CustomItemsAPI").sourceSets.main.get().output)
    }
}

paper {
    main = "uk.co.notnull.CustomItems.CustomItemsImpl"
    loader = "uk.co.notnull.CustomItems.CustomItemsLoader"
    generateLibrariesJson = true
    apiVersion = libs.versions.paperApi.get().replace(Regex("\\-R\\d.\\d-SNAPSHOT"), "")
    authors = listOf("Jim (AnEnragedPigeon)")
    description = "Custom Items"

    permissions {
        register("customitems.give") {
            description = "Access to /giveitem"
            default = BukkitPluginDescription.Permission.Default.OP
        }
        register("customitems.givepool") {
            description = "Access to /givepool"
            default = BukkitPluginDescription.Permission.Default.OP
        }
        register("customitems.grant") {
            description = "Access to /grantitem"
            default = BukkitPluginDescription.Permission.Default.OP
        }
        register("customitems.revoke") {
            description = "Access to /revoke"
            default = BukkitPluginDescription.Permission.Default.OP
        }
        register("customitems.reload") {
            description = "Access to /reload"
            default = BukkitPluginDescription.Permission.Default.OP
        }
        register("customitems.view") {
            description = "Access to /viewunclaimed"
            default = BukkitPluginDescription.Permission.Default.OP
        }
    }
}
