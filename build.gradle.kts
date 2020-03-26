plugins { kotlin("jvm") version "1.3.71" }

group = "dev.lunarcoffee"
version = "0.1.0"

repositories {
    mavenCentral()
    jcenter()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))

    implementation("net.dv8tion:JDA:4.1.1_109")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8:1.3.3")

    implementation("org.reflections:reflections:0.9.10")

    implementation("org.yaml:snakeyaml:1.26")
    implementation("com.google.code.gson:gson:2.8.6")

    // User bot:

    implementation("org.litote.kmongo:kmongo-coroutine:3.12.2")
    implementation("club.minnced:discord-webhooks:0.2.0")
    implementation("com.github.kittinunf.fuel:fuel:2.2.1")
    implementation("com.github.kittinunf.fuel:fuel-coroutines:2.2.1")
    implementation("org.apache.commons:commons-math3:3.6.1")
}

tasks {
    compileKotlin { kotlinOptions.jvmTarget = "1.8" }
    compileTestKotlin { kotlinOptions.jvmTarget = "1.8" }
}
