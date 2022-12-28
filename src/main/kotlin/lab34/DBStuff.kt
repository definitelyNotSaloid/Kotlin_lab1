package lab34

import com.mysql.cj.QueryResult
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
import java.sql.ResultSet
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

    genre: String="NO_GENRE",
    tags: List<String> = listOf(),
    includeAllTags: Boolean= false,
    nameKeywords: List<String> = listOf()        // lowercase!!!
) : List<MovieData> {
    // PLACEHOLDER LIST, TODO replace with database request


    // processing genre-tags request
    val movies: ResultSet
    if (nameKeywords.isEmpty()) {
        val sb = StringBuilder()
        if (includeAllTags) {
            sb.append("""
                SELECT name, gerne, description, year, id FROM movie
                WHERE 
            """.trimIndent())

            if (genre!="NO_GENRE")
                sb.append(" gerne='$genre'")

            if (genre!="NO_GENRE" && tags.isNotEmpty())
                sb.append(" AND ")

            if (tags.isNotEmpty()) {
                sb.append(" id IN\n" +
                        "                (SELECT id FROM tags\n" +
                        "                WHERE LOWER(tag) in (")
                for (tag in tags) {
                    sb.append("'${tag.lowercase()}', ")
                }
                sb.deleteAt(sb.lastIndexOf(','))      //removing ,

                sb.append(")\nGROUP BY id\nHAVING COUNT(*)=${tags.count()})")
            }
        }
        else {
            sb.append("""
                SELECT name, gerne, description, year, id FROM movie
                WHERE 
            """.trimIndent())

            if (genre!="NO_GENRE")
                sb.append(" gerne='$genre'")

            if (genre!="NO_GENRE" && tags.isNotEmpty())
                sb.append(" AND ")

            if (tags.isNotEmpty()) {
                sb.append(" id IN\n" +
                        "                (SELECT DISTINCT id FROM tags\n" +
                        "                WHERE LOWER(tag) in (")

                for (tag in tags) {
                    sb.append(" '${tag.lowercase()}',")
                }
                sb.deleteAt(sb.lastIndexOf(','))      //removing ,
            }

            sb.append("))")
        }


        movies = connection.prepareStatement(sb.toString()).executeQuery()
    }
    else {
        val sb = StringBuilder()
        sb.append("SELECT name, gerne, description, year, id FROM movie\n" +
                "WHERE LOWER(name) LIKE '")

        for (kw in nameKeywords){
            sb.append("%$kw")
        }
        sb.append("%'")

        movies = connection.prepareStatement(sb.toString()).executeQuery()
    }

    return sequence<MovieData>() {
        while (movies.next()) {
            val tagList = mutableListOf<String>()
            val tagQ = connection.prepareStatement(
                "SELECT tag FROM tags\n" +
                        "WHERE id=${movies.getInt(5)}")
                .executeQuery()
            while (tagQ.next())
                tagList.add(tagQ.getNString(1))


            yield(MovieData(
                name = movies.getNString(1),
                genre = movies.getNString(2),
                description = movies.getNString(3),
                year = movies.getInt(4),
                tags = tagList
            ))

        }
    }.toList()
}

