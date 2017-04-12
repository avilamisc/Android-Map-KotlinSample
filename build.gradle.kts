

import org.gradle.script.lang.kotlin.extra
import org.gradle.script.lang.kotlin.gradleScriptKotlin
import org.gradle.script.lang.kotlin.kotlinModule
import org.gradle.script.lang.kotlin.repositories

buildscript {
    //Temporary hack until Android plugin has proper support
    System.setProperty("com.android.build.gradle.overrideVersionCheck",  "true")

    val kotlin_version  = "1.1.1"
    extra.set("kotlin_version", "1.1.1")
    repositories {
        jcenter()
        gradleScriptKotlin()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:2.3.1")
        classpath("com.getkeepsafe.dexcount:dexcount-gradle-plugin:0.6.1")
        classpath(kotlinModule("gradle-plugin"))
    }
}

allprojects {
    val kotlin_version  = "1.1.1"
    repositories {
        jcenter()
		mavenLocal()
        gradleScriptKotlin()
    }
}