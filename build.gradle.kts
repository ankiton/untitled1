plugins {
    id("java")
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")

    implementation("org.json:json:20231013")

    // OkHttp для сетевых запросов
    implementation("com.squareup.okhttp3:okhttp:4.9.3")

    // Для тестирования на Kotlin
    testImplementation(kotlin("test"))

}

tasks.test {
    useJUnitPlatform()
}


