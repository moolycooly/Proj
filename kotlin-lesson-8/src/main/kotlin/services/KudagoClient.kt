
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import kotlinx.serialization.json.Json
import org.fintech.dto.NewsResponce
import org.slf4j.LoggerFactory
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId


class KudagoClient(

    private val maxAttempts: Int,
    private val delay: Long,
    private val maxThreads : Int,
    private val client: HttpClient,
    private val pageSize: Int = 100
) {
    private val logger = LoggerFactory.getLogger(KudagoClient::class.java)
    private val semaphore: Semaphore = Semaphore(maxThreads)

    suspend fun getNews(count: Int = 100): List<News> {
        try {
            val totalPagesCount: Int = (count + pageSize - 1) / pageSize
            val resultNews = mutableListOf<News>()
            for (page in 1..totalPagesCount) {
                resultNews.addAll(getNewsByPage(page, pageSize))
            }
            logger.info("Successfully got news")

            return resultNews.take(count)
        } catch (e: Exception) {
            logger.error("Failed to getNews: {}", e.message)
            return emptyList()
        }


    }

    suspend fun getNewsByPage(page: Int, pageSize: Int ): List<News> {
        semaphore.withPermit {
            for (attempt: Int in 1..maxAttempts) {
                logger.debug("Attempt {} to fetch news with page: {}", attempt, page)
                try {
                    val response: HttpResponse = client.get("https://kudago.com/public-api/v1.4/news/") {
                        parameter(
                            "fields",
                            "id,title,place,description,site_url,favorites_count,comments_count,publication_date"
                        )
                        parameter("order_by", "-publication_date")
                        parameter("location", "nsk")
                        parameter("page_size", pageSize)
                        parameter("text_format", "text")
                        parameter("expand", "place")
                        parameter("page", page)
                    }
                    val newsList = Json.decodeFromString<NewsResponce>(response.bodyAsText()).results
                    return newsList
                } catch (e: Exception) {
                    if (attempt == maxAttempts) {
                        logger.error("Too many attempts: Failure to getNewsByPage({}): {}", page, e.message)
                    }
                    Thread.sleep(delay)
                }

            }
            return emptyList()
        }
    }

    fun saveListNews(path: String, news: Collection<News>) {

        if(Files.exists(Paths.get(path))) {
            logger.warn("Файл $path уже существует.")
        }
        try {
            val file = File(path)
            file.bufferedWriter().use { writer ->
                writer.write("Id,Title,Place ID,Description,Site Url,Favorites Count,Comments Count,Rating\n")
                news.forEach {
                    writer.write("${it.id},${it.title},${it.place?.id},${it.description},${it.siteUrl},${it.favoritesCount},${it.commentsCount},${it.rating}\n")
                }
            }
            logger.info("Новости успешно сохранены в $path")
        } catch (e: Exception) {
            logger.error("Не удалось сохранить новости: ${e.message}")
        }

    }


    fun getPageSize() : Int{
        return this.pageSize
    }

}

fun List<News>.getMostRatedNews(count: Int, period: ClosedRange<LocalDate>): List<News>{
    val logger = LoggerFactory.getLogger("getMostRatedNews")
    logger.debug("Вход в функцию: '${::getMostRatedNews.name}'")

    try {
        val filteredNews = this
            .filter {
                val publicationDate = Instant.ofEpochSecond(it.publicationDate)
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate()
                publicationDate in period
            }
        return filteredNews
            .sortedByDescending {it.rating}
            .take(count);
    }
    finally {
        logger.debug("Выход из функции: '${::getMostRatedNews.name}'")
    }
}