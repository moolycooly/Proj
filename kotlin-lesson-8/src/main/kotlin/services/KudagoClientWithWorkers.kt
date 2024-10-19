package org.fintech.services

import KudagoClient
import News
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import org.slf4j.LoggerFactory
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths


class KudagoClientWithWorkers(
    private val kudagoClient: KudagoClient,
    private val workersCount : Int,
    private val path : String = "news.txt"
) {
    private val logger = LoggerFactory.getLogger(KudagoClientWithWorkers::class.java)
    @OptIn(DelicateCoroutinesApi::class)
    private val threadPool = newFixedThreadPoolContext(workersCount,"KudagoClientWorker")
    private val channel = Channel<News>(capacity = Channel.UNLIMITED)
    private val pageSize: Int = kudagoClient.getPageSize()

    suspend fun getNewsCoroutine(count: Int = 100) = coroutineScope {
        val totalPagesCount : Int = (count + pageSize - 1) / pageSize
        val jobs = mutableListOf<Job>()
        for (i in 1..workersCount) {
            val job = launch(threadPool) {
                worker(i, totalPagesCount)
            }
            jobs.add(job)
        }
        val jobProcessor = launch {
            processor()
        }
        jobs.joinAll()
        channel.close()
        jobProcessor.join()
    }
    private suspend fun worker(count : Int, totalPagesCount: Int){
        for(i in count..totalPagesCount step workersCount) {
            kudagoClient.getNewsByPage(i,pageSize).forEach{
                channel.send(it)
            }
        }
    }
    private suspend fun processor() {
        try {
            if(Files.exists(Paths.get(path))) {
                logger.warn("Файл $path уже существует.")
            }
            val file = File(path)
            file.bufferedWriter().use { writer ->
                writer.write("Id,Title,Place ID,Description,Site Url,Favorites Count,Comments Count,Rating\n")
                for(news in channel) {
                    writer.write("${news.id},${news.title},${news.place?.id},${news.description},${news.siteUrl},${news.favoritesCount},${news.commentsCount},${news.rating}\n")
                }
            }
            logger.info("Новости успешно сохранены в $path")
        } catch (e: Exception) {
            logger.error("Не удалось сохранить новости: ${e.message}")
        }

    }

}