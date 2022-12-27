package lab34

import java.net.URL
import java.io.BufferedInputStream
import java.io.FileOutputStream

import dev.inmo.tgbotapi.extensions.api.files.downloadFile
import io.ktor.client.*
import java.awt.Desktop
import java.lang.Thread.sleep
import java.net.ConnectException
import java.net.URI
import java.net.URLEncoder
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.sql.Connection
import java.sql.DriverManager
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ExecutionException

data class MovieData(
    val name: String,
    val tags: List<String>,
    val genre: String,
    val description: String,
    val year: Int
) {
}

fun searchMovies(
    connection: Connection,

    genre: String="NO_genre",
    tags: List<String> = listOf(),
    includeAllTags: Boolean= false,
    nameRegex: Regex? = null        // IMPORTANT: MUST BE REGEX FOR LOWERCASE
) : List<MovieData> {
    val tagList = tags.map {
        it.lowercase(Locale.getDefault())
    }
    // PLACEHOLDER LIST, TODO replace with database request
    val movies = listOf(
        MovieData(
            "The Green elephant",
            listOf("social","philosophical","masterpiece"),
            "Thriller",
            "Two men, one cell. A story about slow descending into madness.",
            1999
        ),
        MovieData(
            "Shrek",
            listOf("adventure","comedy","masterpiece","cartoon"),
            "Comedy",
            "Its Shrek. Please dont tell me you dont know about this one",
            2001
        ),
        MovieData(
            "Boku no pico",
            listOf("anime","philosophical"),
            "Romance",
            "The anime you asked about. Trust me bro",
            2006
        )
    )

    if (genre!="NO_genre") {
        val query = """
        SELECT name, gerne, description, year FROM movie
        WHERE gerne=$genre
    """.trimIndent()
        val res = connection.prepareStatement(query).executeQuery()

        return sequence<MovieData>() {
            while (res.next()) {
                yield(MovieData(
                    name = res.getNString(1),
                    genre = res.getNString(2),
                    description = res.getNString(3),
                    year = res.getInt(4),
                    tags = listOf()
                ))

            }
        }.toList()
    }

    return movies.filter {
        ((it.genre.lowercase(Locale.getDefault())==genre.lowercase(Locale.getDefault()))
          && (!includeAllTags || it.tags.containsAll(tagList))
          && (includeAllTags || tagList.isEmpty() || it.tags.any{ tag -> tagList.contains(tag)})
          && (nameRegex==null || nameRegex.matches(it.name.lowercase(Locale.getDefault())))
        )
    }
}

