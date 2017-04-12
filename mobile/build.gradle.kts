import com.android.build.gradle.AppExtension
import com.android.build.gradle.AppPlugin
import com.android.build.gradle.internal.dsl.BuildType
import com.android.builder.core.DefaultApiVersion
import com.android.builder.core.DefaultProductFlavor
import com.android.builder.model.ApiVersion

import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Project
import org.gradle.internal.impldep.bsh.commands.dir
import org.gradle.nativeplatform.BuildType

import org.gradle.script.lang.kotlin.gradleScriptKotlin
import org.gradle.script.lang.kotlin.kotlinModule
import org.gradle.script.lang.kotlin.repositories

import org.jetbrains.kotlin.gradle.plugin.KotlinAndroidPluginWrapper

apply {
    plugin<AppPlugin>()
    plugin<KotlinAndroidPluginWrapper>()
}

android {
    compileSdkVersion(25)
    buildToolsVersion("25.0.2")

    defaultConfigExtension {
        setMinSdkVersion(15)
        setTargetSdkVersion(24)

        versionCode = 1
        versionName = "1.0"
    }

    buildTypesExtension {
        release {
            isMinifyEnabled = false
            proguardFiles("proguard-rules.pro")
        }
    }
}


val play_version = "10.2.0"
val support_v = "25.2.0"

dependencies {
    compile(kotlinModule("stdlib"))
//	compile(fileTree(dir: 'libs', include: ['*.jar']))
	
    compile ("com.google.android.gms:play-services-base:$play_version")
    compile ("com.google.android.gms:play-services-maps:$play_version")
    compile ("com.google.android.gms:play-services-places:$play_version")
    compile ("com.google.android.gms:play-services-location:$play_version")
    compile ("com.google.code.gson:gson:2.8.0")
    compile ("com.android.support:support-annotations:$support_v")
    compile ("com.android.support:recyclerview-v7:$support_v")
    compile ("com.android.support:appcompat-v7:$support_v")
    compile ("com.android.support:design:$support_v")
    compile ("com.squareup.retrofit2:retrofit:2.1.0")
    compile ("com.squareup.retrofit2:converter-gson:2.1.0")
    compile ("com.android.support.constraint:constraint-layout:1.0.1")
    compile ("io.reactivex:rxandroid:1.2.1")
// Because RxAndroid releases are few and far between, it is recommended you also
// explicitly depend on RxJava"s latest version for bug fixes and new features.
    compile ("io.reactivex:rxjava:1.2.2")
    compile ("com.minimize.android:rxrecycler-adapter:1.2.2")
    compile ("com.google.maps.android:android-maps-utils:0.4+")
    compile ("com.github.bumptech.glide:glide:3.7.0")
    compile ("com.google.guava:guava:20.0")

//    compile files("libs/fesimplegeoprox.jar")
//    compile files("libs/simplelatlng-1.3.1.jar")
    compile ("org.jetbrains.kotlin:kotlin-stdlib-jre7:1.1.1")
}

repositories {
    gradleScriptKotlin()
    mavenCentral()
    jcenter()
}


/*
 * Extension functions to allow comfortable references
 * Taken from: https://github.com/gradle/gradle-script-kotlin/blob/master/samples/hello-android/build.gradle.kts
 */

fun Project.android(setup: AppExtension.() -> Unit) = the<AppExtension>().setup()

fun NamedDomainObjectContainer<BuildType>.release(setup: BuildType.() -> Unit) = findByName("release").setup()

fun AppExtension.defaultConfigExtension(setup: DefaultProductFlavor.() -> Unit) = defaultConfig.setup()

fun AppExtension.buildTypesExtension(setup: NamedDomainObjectContainer<BuildType>.() -> Unit) = buildTypes { it.setup() }

fun DefaultProductFlavor.setMinSdkVersion(value: Int) = setMinSdkVersion(value.asApiVersion())

fun DefaultProductFlavor.setTargetSdkVersion(value: Int) = setTargetSdkVersion(value.asApiVersion())

fun Int.asApiVersion(): ApiVersion = DefaultApiVersion.create(this)