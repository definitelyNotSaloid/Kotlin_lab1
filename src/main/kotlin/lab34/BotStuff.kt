package lab34

import dev.inmo.tgbotapi.extensions.api.send.reply
import dev.inmo.tgbotapi.extensions.api.send.sendTextMessage
import dev.inmo.tgbotapi.extensions.api.telegramBot
import dev.inmo.tgbotapi.extensions.behaviour_builder.BehaviourContext
import dev.inmo.tgbotapi.extensions.behaviour_builder.buildBehaviourWithLongPolling
import dev.inmo.tgbotapi.extensions.behaviour_builder.expectations.waitText
import dev.inmo.tgbotapi.extensions.behaviour_builder.triggers_handling.onCommand
import dev.inmo.tgbotapi.extensions.behaviour_builder.triggers_handling.onCommandWithArgs
import dev.inmo.tgbotapi.extensions.utils.types.buttons.replyKeyboard
import dev.inmo.tgbotapi.requests.send.SendTextMessage
import dev.inmo.tgbotapi.types.buttons.reply.simpleReplyButton
import dev.inmo.tgbotapi.types.message.abstracts.CommonMessage
import dev.inmo.tgbotapi.types.message.content.TextContent
import dev.inmo.tgbotapi.utils.row
import kotlinx.coroutines.flow.first
import java.util.*

import lab2.bicycles.*
import java.sql.Connection

private class ReplyBars {
    companion object {
        // pops up on start and after finishing command execution
        val DEFAULT = replyKeyboard(resizeKeyboard = true, oneTimeKeyboard = true) {
            row(simpleReplyButton("/genreAndTags"), simpleReplyButton("/keywords"))
        }

        // just a single, lonely "any" button
        val ANY_SINGLE = replyKeyboard(resizeKeyboard = true, oneTimeKeyboard = true) {
            row(simpleReplyButton("ANY"))
        }

        // "any" button got a mate!
        val ANY_OR_ALL = replyKeyboard(resizeKeyboard = true, oneTimeKeyboard = true) {
            row(simpleReplyButton("ANY"), simpleReplyButton("ALL"))
        }
    }
}

private suspend fun BehaviourContext.replyWithMovieList(movies: List<MovieData>, message: CommonMessage<TextContent>) {
    val textReply = StringBuilder()
    if (movies.isNotEmpty()) {
        textReply.append("Look what ive found!\n\n")
        var itr = 0
        for (movie in movies) {
            if (itr == 10)
                break

            itr++

            textReply.append("$itr. ${movie.name} (${movie.year})\n")
            for (tag in movie.tags)
                textReply.append("$tag,")

            textReply.append("\n${movie.description}\n")
        }
    }
    else
        textReply.append("Found nothing! Dont be so picky next time")

    reply(message, textReply.toString(), replyMarkup = ReplyBars.DEFAULT)
}


private suspend fun BehaviourContext.getGenre(
    message : CommonMessage<TextContent>,
    args : Array<String>)
: String {
    var genre = args.firstOrNull()
    if (genre == null){
        genre=waitText(SendTextMessage(message.chat.id,
            "Alright, which genre do you prefer? Take note that imho each movie can belong to one and only one genre. And its definitely not because im lazy. BTW you can type ANY if you have no preferences.",
            replyMarkup = ReplyBars.ANY_SINGLE))
            .first().text

        if (genre.lowercase()=="any")
            genre="NO_GENRE"

    }
    return genre
}

private suspend fun BehaviourContext.getTags(
    message : CommonMessage<TextContent>,
    includeAllOut : GenericBox<Boolean>)
: List<String> {
    var tags : List<String> = waitText(SendTextMessage(message.chat.id,
        "How about some tags? Specify any amount, separated with whitespace. Or just type \"any\" if you dont care",
        replyMarkup = ReplyBars.ANY_SINGLE))
        .first().text.split(Regex("\\s+"))

    if ((tags.firstOrNull() ?: "any").lowercase(Locale.getDefault())== "any") {
        tags = listOf()
    }

    if (tags.count()>1) {
        includeAllOut.value = (waitText(
            SendTextMessage(
                message.chat.id,
                "Youve specified 2 or more tags. Should your dream movie have ALL of your tags or ANY?",
                replyMarkup = ReplyBars.ANY_OR_ALL
            )
        ).first().text == "ALL")
    }

    return tags
}

suspend fun initBot(connection: Connection, token: String){
    val bot = telegramBot(token)

    bot.buildBehaviourWithLongPolling {
        onCommand("start") {
            reply(it, "this bot isnt working. Or is it? It sent a reply after all", replyMarkup = ReplyBars.DEFAULT)
        }

        onCommandWithArgs("genreAndTags") {message, args ->
            val genre = getGenre(message, args)

            val allTagsBox = GenericBox<Boolean>(false)
            val tags = getTags(message, allTagsBox)

            if (tags.isEmpty() && genre=="NO_GENRE")
                sendTextMessage(message.chat.id, "Can you be more specific next time? thanx.")
            else {
                val res = searchMovies(connection, genre, tags, allTagsBox.value)

                replyWithMovieList(res, message)
            }
        }

        onCommandWithArgs("keywords") {message, args ->
            val wordsToSearch : List<String> = if (args.isEmpty()) {
                waitText(SendTextMessage(message.chat.id, "Now tell me name of the movie you would like to search for. " +
                        "Take note, im not as smart as google-san or yandex-chan, i wont recognize typos and such stuff.")
                ).first().text.split(Regex("\\s+"))
            } else
                args.toList()

            val res = searchMovies(connection, nameKeywords = wordsToSearch.map{it.lowercase()})
            replyWithMovieList(res,message)
        }

    }.join()

}