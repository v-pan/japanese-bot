package bot

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import com.gitlab.kordlib.core.behavior.channel.createEmbed
import com.gitlab.kordlib.core.event.message.MessageCreateEvent
import com.gitlab.kordlib.core.on
import kotlinx.coroutines.runBlocking
import java.io.File

class BotCliParser : CliktCommand() {
    private val token: String? by option(help="Discord bot user token")
    private val graph: Boolean by option(help="Authenticates with Microsoft Graph if set").flag(default = false)

    private suspend fun login(token: String, authenticate: Boolean) {
        val bot = JapaneseBot(token) {
            enableGraphAuth = authenticate

            if (File("./translatedFiles").listFiles() == null) {
                enableGraphAuth = true
            }
        }

        val translationDir = File("./translatedFiles")

        bot.kord.on<MessageCreateEvent> {
            if(message.author?.isBot == true) return@on

            when (message.content) {
                "!romaji" -> message.channel.createEmbed {
                    title = "Available commands:"
                    field("Help", false) { "!romaji help" }
                    field("List all files", false) { "!romaji list" }
                    field("Transcribe file", false) { "!romaji [file number]" }
                }
                "!romaji help" -> message.channel.createEmbed {
                    title = "Available commands:"
                    field("Help", false) { "!romaji help" }
                    field("List all files", false) { "!romaji list" }
                    field("Transcribe file", false) { "!romaji [file number]" }
                }
                "!romaji list" -> {
                    val fileList = translationDir.listFiles()!!.toMutableList()

                    var count = 0
                    var cur = fileList.take(25)
                    while(cur.isNotEmpty()) {
                        message.channel.createEmbed {
                            title = "Files: (page ${count + 1})"
                            description = "To request a file's transcription, run !romaji [file number]"
                            cur.forEachIndexed { index, file ->
                                if (file.name != ".manifest") {
                                    field("${index + (count * 25)}", true) { file.name.substring(0, file.name.length - 4) }
                                }
                            }
                        }

                        fileList.removeAll(cur)
                        cur = fileList.take(25)
                        count++
                    }
                }
                else -> when { // Commands with params
                    message.content.contains("!romaji") -> message.channel.createEmbed {
                        val (_, number) = message.content.split(" ")
                        title = "Transcription:"
                        description = translationDir.listFiles()!![number.toInt()].readText()
                    }
                }
            }
        }

        bot.kord.on<MessageCreateEvent> {
            if(message.author?.isBot == true) return@on

            when {
                message.content.startsWith("<@!777216205735198741> say ") -> {
                    if(message.author?.id?.value == "249962604603113473") {
//                        message.delete()
                        message.channel.createMessage(message.content.replace("<@!777216205735198741> say ", ""))
                    }
                }
                message.content.toLowerCase().contains("jill is jill") -> {
                    val jillMention = "<@651790356802961433>"
                    message.channel.createMessage("$jillMention is Jill")
                    var lowercase = message.content.toLowerCase()
                    var counter = 0
                    while(lowercase.contains("jill is jill")) {
                        lowercase = lowercase.replaceFirst("jill is jill", "")
                        counter += 1
                    }
                    for(i in 0 until counter - 1) {
                        message.channel.createMessage("$jillMention is Jill")
                    }
                }
            }
        }

        bot.kord.on<MessageCreateEvent> {
            if (message.content == "<@!777216205735198741> talk here") {
                if (message.author?.id?.value == "249962604603113473") {
                    message.delete()
                    while (true) {
                        val res = readLine()
                        if (res.isNullOrBlank()) {
                            break
                        } else {
                            message.channel.createMessage(res)
                        }
                    }
                }
            }
        }

        println("Bot ready")

        bot.kord.login {
            watching("for !romaji")
        }
    }

    override fun run() = runBlocking {
        login(token!!, graph)
    }
}