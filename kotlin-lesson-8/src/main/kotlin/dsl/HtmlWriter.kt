package org.fintech.dsl

import News
import java.time.Instant
import java.time.ZoneId

fun newsToHtml(newsList: List<News>) =
    html {
        head {
            title { +"Новости" }
        }
        body {
            h1 { +"Новости" }
            for (news in newsList) {
                h1 { +(news.title)!! }
                p {
                    b { +"Дата публикации: "}
                    +"${Instant.ofEpochSecond(news.publicationDate).atZone(ZoneId.systemDefault()).toLocalDate()}"
                }
                p {
                    b {+"Описание: "}
                    +(news.description ?: "Описание пока отсутствует.")
                }
                p {
                    b {+"Лайков: "}
                    +"${news.favoritesCount}"
                    p{}
                    b {+" Комментариев: "}
                    +"${news.commentsCount}"
                }
                p {
                    b {+"Рейтинг: "}
                    +"%.2f".format(news.rating)
                }
                a(href = news.siteUrl ?: "#") {
                    +"Источник"
                }
            }
        }
    }

