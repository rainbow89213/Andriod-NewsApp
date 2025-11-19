/**
 * Gradle 项目设置文件
 * 
 * 作用：
 * 1. 配置插件和依赖的下载仓库
 * 2. 定义项目名称
 * 3. 声明项目包含的模块
 */

// 插件管理配置：定义从哪里下载 Gradle 插件
pluginManagement {
    repositories {
        // Google 的 Maven 仓库：包含 Android 相关的插件
        google {
            content {
                // 只从 Google 仓库下载 com.android.* 开头的插件
                includeGroupByRegex("com\\.android.*")
                // 只从 Google 仓库下载 com.google.* 开头的插件
                includeGroupByRegex("com\\.google.*")
                // 只从 Google 仓库下载 androidx.* 开头的插件
                includeGroupByRegex("androidx.*")
            }
        }
        // Maven 中央仓库：包含大部分开源库
        mavenCentral()
        // Gradle 插件门户：Gradle 官方插件仓库
        gradlePluginPortal()
    }
}

// 依赖解析管理：定义从哪里下载项目依赖库
dependencyResolutionManagement {
    // 强制所有依赖必须在这里声明的仓库中查找
    // 如果在子项目中声明仓库会报错，确保依赖来源统一管理
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        // Google Maven 仓库：包含 AndroidX、Material Design 等库
        google()
        // Maven 中央仓库：包含大部分第三方开源库
        mavenCentral()
    }
}

// 定义项目的根名称，会显示在 Android Studio 的项目面板中
rootProject.name = "demo2"

// 声明项目包含的模块
// ":app" 表示包含 app 目录作为一个模块
// 如果有多个模块，可以继续添加，如：include(":app", ":library")
include(":app")
 