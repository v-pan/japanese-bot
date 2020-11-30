package bot

import Authentication
import com.gitlab.kordlib.core.Kord
import java.util.*

class JapaneseBotBuilder(private val token: String) {
    var enableGraphAuth = true

//    val kordClient = runBlocking { Kord(token) }

    private fun authenticate(): String {
        val oAuthProperties = Properties().also {
            it.load(this::class.java.classLoader.getResourceAsStream("oAuth.properties"))
        }

        val appId = oAuthProperties.getProperty("app.id")
        val appScopes = oAuthProperties.getProperty("app.scopes").split(", ")

        Authentication.initialize(appId)
        return Authentication.getUserAccessToken(appScopes.toTypedArray())
    }

    suspend fun build(): JapaneseBot {
        val bot = JapaneseBot(Kord(token))

        if(enableGraphAuth) {
            bot.updateTranslatedFiles(accessToken = authenticate())
        }

        return bot
    }
}