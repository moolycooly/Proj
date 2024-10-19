package org.fintech
import KudagoClient
import News
import io.ktor.client.*
import org.fintech.services.KudagoClientWithWorkers
import org.slf4j.LoggerFactory
import org.yaml.snakeyaml.Yaml
import java.io.FileInputStream

fun ApplicationConfig(): Map<String, Any> {
    val inputStream = FileInputStream("kotlin-lesson-8/src/main/resources/application.yml")
    val yaml = Yaml()
    val config = yaml.load<Map<String, Any>>(inputStream)
    inputStream.close()
    return config
}
suspend fun main() {
    val logger = LoggerFactory.getLogger("main")
    val config = ApplicationConfig()
    val kudagoConfig = config["kudago"] as Map<*, *>

    val delay = kudagoConfig["delay"] as Int
    val maxAttempts = kudagoConfig["max-attempts"] as Int
    val maxThreads = kudagoConfig["max-threads-together"] as Int
    val httpClient = HttpClient()
    val kudagoClient = KudagoClient(maxAttempts,delay.toLong(),maxThreads,httpClient,100)

    val count : Int = 1000

    var start = System.currentTimeMillis()
    val newsList: List<News> = kudagoClient.getNews(count);
    kudagoClient.saveListNews("kotlin-lesson-8/news1.txt",newsList)
    var end = System.currentTimeMillis()
    logger.info("\nВремя выполнения без корутин: {}ms", end - start)


    val workersCount = kudagoConfig["workers-count"] as Int
    val kudagoClientWithWorkers : KudagoClientWithWorkers = KudagoClientWithWorkers(kudagoClient,workersCount, "kotlin-lesson-8/news2.txt")
    start = System.currentTimeMillis()
    kudagoClientWithWorkers.getNewsCoroutine(count)
    end = System.currentTimeMillis()
    logger.info("\nВремя выполнения с корутинами ${end-start} ms" +
            "\n Подробнее: " +
            "\n     Максимальное количество потоков в одном методе: $maxThreads " +
            "\n     Максимальное количество попыток на запрос: : ${maxAttempts}" +
            "\n     Количество воркеров: ${workersCount}")
}

