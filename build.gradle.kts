/**
 * 项目级别的构建配置文件
 * 
 * 作用：
 * 1. 定义整个项目的构建配置
 * 2. 配置所有子模块（如 app 模块）共用的插件和依赖
 * 3. 这里的配置会影响项目中的所有模块
 * 
 * 注意：
 * - 这是顶层构建文件，不是某个具体模块的配置
 * - 修改后需要点击 Android Studio 的 "Sync Now" 同步
 */

// 插件配置块：声明项目需要使用的 Gradle 插件
plugins {
    // 引用 libs.versions.toml 中定义的 Android 应用插件
    // apply false 表示不在项目级别应用，而是在子模块中应用
    // 这个插件提供了构建 Android 应用所需的所有任务和配置
    alias(libs.plugins.android.application) apply false
}