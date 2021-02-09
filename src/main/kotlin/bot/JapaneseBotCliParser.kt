package bot

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import kotlinx.coroutines.runBlocking

typealias ParserBuilder = suspend JapaneseBotCliParser.(token: String, authenticate: Boolean) -> Unit

class JapaneseBotCliParser : CliktCommand() {
    private val token: String? by option(help="Discord bot user token")
    private val graph: Boolean by option(help="Authenticates with Microsoft Graph if set").flag(default = false)

    lateinit var bot: JapaneseBot
    lateinit var behaviour: ParserBuilder

    fun build(behaviour: ParserBuilder): JapaneseBotCliParser {
        this.behaviour = behaviour
        return this
    }

    override fun run() = runBlocking {
        behaviour(token!!, graph)
    }
}