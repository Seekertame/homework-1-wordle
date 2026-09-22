pluginManagement {
    repositories {
        maven("https://mvn-mirror.gitverse.ru")
        gradlePluginPortal {
            content {
                includeModule("net.ltgt.gradle", "gradle-errorprone-plugin")
            }
        }
    }
    resolutionStrategy {
        eachPlugin {
            when (requested.id.id) {
                "com.diffplug.spotless" ->
                    useModule("com.diffplug.spotless:spotless-plugin-gradle:${requested.version}")
                "net.ltgt.errorprone" ->
                    useModule("net.ltgt.gradle:gradle-errorprone-plugin:${requested.version}")
            }
        }
    }
}

rootProject.name = "homework-1-wordle"

dependencyResolutionManagement {
    repositoriesMode = RepositoriesMode.FAIL_ON_PROJECT_REPOS
    repositories {
        maven("https://mvn-mirror.gitverse.ru")
    }
}
