package microsoft.graph

import com.google.gson.JsonObject
import com.microsoft.graph.logger.DefaultLogger
import com.microsoft.graph.logger.LoggerLevel
import com.microsoft.graph.models.extensions.*
import com.microsoft.graph.requests.extensions.*
import java.io.InputStream


/**
 * microsoft.graph.Graph
 */
object Graph {
    private var graphClient: IGraphServiceClient? = null
    private var authProvider: SimpleAuthProvider? = null
    private fun ensureGraphClient(accessToken: String) {
        if (graphClient == null) {
            // Create the auth provider
            authProvider = SimpleAuthProvider(accessToken)

            // Create default logger to only log errors
            val logger = DefaultLogger()
            logger.loggingLevel = LoggerLevel.ERROR

            // Build a microsoft.graph.Graph client
            graphClient = GraphServiceClient.builder()
                .authenticationProvider(authProvider)
                .logger(logger)
                .buildClient()
        }
    }

    fun getUser(accessToken: String): User? {
        ensureGraphClient(accessToken)

        // GET /me to get authenticated user
        return graphClient
            ?.me()
            ?.buildRequest()
            ?.get()
    }

    fun getTeams(accessToken: String): IGroupCollectionPage? {
        ensureGraphClient(accessToken)

        return graphClient?.me()?.joinedTeams()?.buildRequest()?.get()
    }

    fun getChannels(accessToken: String, teamId: String): IChannelCollectionPage? {
        ensureGraphClient(accessToken)

        return graphClient?.teams(teamId)?.channels()?.buildRequest()?.get()
    }

    fun getFilesFolder(accessToken: String, teamId: String, channelId: String): JsonObject? {
        ensureGraphClient(accessToken)

        return graphClient?.customRequest("/teams/$teamId/channels/$channelId/filesFolder")?.buildRequest()?.get()
    }

    fun getFolderChildren(accessToken: String, driveId: String, folderId: String): IDriveItemCollectionPage? {
        ensureGraphClient(accessToken)

        return graphClient?.drives(driveId)?.items(folderId)?.children()?.buildRequest()?.get()
    }

    fun getInputStreamsFromDriveItems(accessToken: String, driveItems: List<DriveItem>): List<InputStream?> {
        ensureGraphClient(accessToken)

        return driveItems.map { item ->
            getInputStreamFromDriveItem(accessToken, item)
        }
    }

    fun getInputStreamFromDriveItem(accessToken: String, driveItem: DriveItem): InputStream? {
        ensureGraphClient(accessToken)

        return graphClient?.drives(driveItem.parentReference.driveId)?.items(driveItem.id)?.content()?.buildRequest()?.get()
    }

    fun <T> mapOverPages(accessToken: String, pageCollection: IGroupCollectionPage, lambda: (data: Group) -> T): MutableList<T> {
        ensureGraphClient(accessToken)

        var collection: IGroupCollectionPage? = pageCollection
        val result = mutableListOf<T>()

        while (collection != null) {
            result.addAll(
                collection.currentPage.map(lambda)
            )

            collection = collection.nextPage?.buildRequest()?.get()
        }

        return result
    }

    fun <T> mapOverPages(accessToken: String, pageCollection: IChannelCollectionPage, lambda: (data: Channel) -> T): MutableList<T> {
        ensureGraphClient(accessToken)

        var collection: IChannelCollectionPage? = pageCollection
        val result = mutableListOf<T>()

        while (collection != null) {
            result.addAll(
                collection.currentPage.map(lambda)
            )

            val nextPage = collection.nextPage?.buildRequest()?.get()
            collection = nextPage
        }

        return result
    }

    fun <T> mapOverPages(accessToken: String, pageCollection: IDriveCollectionPage, lambda: (data: Drive) -> T): MutableList<T> {
        ensureGraphClient(accessToken)

        var collection: IDriveCollectionPage? = pageCollection
        val result = mutableListOf<T>()

        while (collection != null) {
            result.addAll(
                collection.currentPage.map(lambda)
            )

            collection = collection.nextPage?.buildRequest()?.get()
        }

        return result
    }

    fun <T> mapOverPages(
        accessToken: String,
        pageCollection: IDriveSharedWithMeCollectionPage,
        lambda: (data: DriveItem) -> T
    ): MutableList<T> {
        ensureGraphClient(accessToken)

        var collection: IDriveSharedWithMeCollectionPage? = pageCollection
        val result = mutableListOf<T>()

        while (collection != null) {
            result.addAll(
                collection.currentPage.map(lambda)
            )

            collection = collection.nextPage?.buildRequest()?.get()
        }

        return result
    }

    fun <T> mapOverPages(accessToken: String, pageCollection: IDriveItemCollectionPage, lambda: (data: DriveItem) -> T): MutableList<T> {
        ensureGraphClient(accessToken)

        var collection: IDriveItemCollectionPage? = pageCollection
        val result = mutableListOf<T>()

        while (collection != null) {
            result.addAll(
                collection.currentPage.map(lambda)
            )

            collection = collection.nextPage?.buildRequest()?.get()
        }

        return result
    }
}