import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.fintech.dto.Place
import kotlin.math.exp

@Serializable
data class News(
    val id: Int?,
    val title: String?,
    val place: Place?,
    val description: String?,
    @SerialName("publication_date") val publicationDate: Long,
    @SerialName("site_url") val siteUrl: String?,
    @SerialName("favorites_count") val favoritesCount: Int,
    @SerialName("comments_count") val commentsCount: Int,
)
{
    val rating: Double by lazy {
        1 / (1 + exp(-(favoritesCount/ (commentsCount + 1).toDouble())))
    }
}
