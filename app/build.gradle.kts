/**
 * App 模块的构建配置文件
 * 
 * 这是应用模块最重要的配置文件，定义了：
 * 1. 应用的基本信息（包名、版本号等）
 * 2. 编译和运行的 Android SDK 版本
 * 3. 构建类型（Debug/Release）
 * 4. 项目依赖的第三方库
 */

// 应用插件：使这个模块成为一个 Android 应用模块
plugins {
    // 引用版本目录中定义的 Android 应用插件
    alias(libs.plugins.android.application)
}

// Android 配置块：所有 Android 相关的配置
android {
    // 命名空间：应用的唯一标识符，也是包名
    // 用于在 Google Play 中唯一标识你的应用
    namespace = "com.example.demo2"
    
    // 编译 SDK 版本：用于编译应用的 Android SDK 版本
    // 版本 36 对应 Android 14
    // 使用更高版本可以使用更多新 API
    compileSdk {
        version = release(36)
    }

    // 默认配置：应用的基本信息
    defaultConfig {
        // 应用 ID：必须全球唯一，发布后不能修改
        applicationId = "com.example.demo2"
        
        // 最低 SDK 版本：应用支持的最低 Android 版本
        // 24 对应 Android 7.0 (Nougat)
        // 低于此版本的设备无法安装此应用
        minSdk = 24
        
        // 目标 SDK 版本：应用针对的 Android 版本
        // 应该设置为最新的稳定版本
        targetSdk = 36
        
        // 版本号：整数，每次发布新版本时递增
        // Google Play 用这个判断版本新旧
        versionCode = 1
        
        // 版本名称：字符串，显示给用户看的版本号
        // 例如："1.0", "2.1.3" 等
        versionName = "1.0"

        // 测试运行器：用于运行 Android 仪器测试
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    // 构建类型：定义不同的构建变体
    buildTypes {
        // Release 构建类型：用于发布到应用商店的正式版本
        release {
            // 是否启用代码混淆和优化
            // false = 不混淆（方便调试）
            // true = 混淆代码（保护代码，减小体积）
            isMinifyEnabled = false
            
            // ProGuard 配置文件：定义混淆和优化规则
            proguardFiles(
                // Android SDK 提供的默认混淆规则
                getDefaultProguardFile("proguard-android-optimize.txt"),
                // 项目自定义的混淆规则
                "proguard-rules.pro"
            )
        }
        // Debug 构建类型会自动创建，用于开发调试
    }
    
    // Java 编译选项：设置 Java 版本兼容性
    compileOptions {
        // 源代码兼容性：代码使用的 Java 版本
        sourceCompatibility = JavaVersion.VERSION_11
        // 目标字节码兼容性：编译后的字节码版本
        targetCompatibility = JavaVersion.VERSION_11
    }
}

// 依赖配置：声明项目需要的第三方库
dependencies {
    // implementation：编译和运行时都需要的依赖
    // 这些库会被打包到最终的 APK 中
    
    // AppCompat：提供向后兼容的 Activity 和主题
    implementation(libs.appcompat)
    
    // Material Design：Google 的 Material Design 组件库
    // 提供按钮、卡片、对话框等 UI 组件
    implementation(libs.material)
    
    // Activity：AndroidX 的 Activity 组件
    implementation(libs.activity)
    
    // ConstraintLayout：强大的布局管理器
    // 可以创建复杂的响应式布局
    implementation(libs.constraintlayout)
    
    // RecyclerView：高性能列表组件
    // 用于显示新闻卡片列表
    implementation(libs.recyclerview)
    
    // CardView：卡片视图组件
    // 用于创建带阴影和圆角的卡片效果
    implementation(libs.cardview)
    
    // Retrofit：网络请求库
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.gson)
    
    // OkHttp：HTTP客户端
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging)
    
    // Gson：JSON解析库
    implementation(libs.gson)
    
    // Glide：图片加载库（用于加载网络图片）
    implementation(libs.glide)
    annotationProcessor("com.github.bumptech.glide:compiler:4.16.0")
    
    // ViewPager2：Fragment切换组件
    implementation("androidx.viewpager2:viewpager2:1.0.0")
    
    // SwipeRefreshLayout：下拉刷新组件
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")
    
    // testImplementation：仅在单元测试时需要的依赖
    // JUnit：Java 单元测试框架
    testImplementation(libs.junit)
    
    // androidTestImplementation：仅在 Android 仪器测试时需要的依赖
    // AndroidX JUnit：Android 测试扩展
    androidTestImplementation(libs.ext.junit)
    // Espresso：Android UI 测试框架
    androidTestImplementation(libs.espresso.core)
}