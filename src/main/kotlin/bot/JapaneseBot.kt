package bot

import com.gitlab.kordlib.core.Kord
import com.microsoft.graph.models.extensions.DriveItem
import jakaroma.Jakaroma
import microsoft.graph.Graph
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.text.PDFTextStripper
import java.io.File
import java.io.FileNotFoundException

class JapaneseBot(val kord: Kord) {
    private fun getPDFTextStrings(accessToken: String): Map<DriveItem, String?> {
        val teamId = "1eaefb73-d26b-47a3-b536-51ab4b7a05ae"

        val result = mutableMapOf<DriveItem, String?>()

        // Check with the manifest file
        File("./translatedFiles").let {
            if (!it.isDirectory && !it.exists()) {
                it.mkdir()
            }
        }
        val manifestFile = File("./translatedFiles/.manifest")
        val lastModifiedMap = try {
             manifestFile.readLines().map { line ->
                 val split = line.split(" | ")
                 if(split.size == 2) {
                     val (fileName, dateModified) = split
                     fileName to dateModified
                 } else {
                     null to null
                 }
            }.toMap()
        } catch (e: FileNotFoundException) {
            manifestFile.writeText("")

            mutableMapOf()
        }

        Graph.getChannels(accessToken, teamId)?.let { channels ->
            Graph.mapOverPages(accessToken, channels) { channel ->
                if(channel.displayName == "General") {
                    Graph.getFilesFolder(accessToken, teamId, channel.id)?.let { filesFolder ->
                        val driveId = filesFolder.getAsJsonObject("parentReference").get("driveId").asString
                        val folderId = filesFolder.get("id").asString

                        // TODO: Make this recurse through all sub-folders and return a flat list of files
                        // Go through folders in filesFolder
                        Graph.getFolderChildren(accessToken, driveId, folderId)?.let { folders ->
                            Graph.mapOverPages(accessToken, folders) { folder ->
                                // Go through files of folder
                                Graph.getFolderChildren(accessToken, driveId, folder.id)?.let { files ->
                                    Graph.mapOverPages(accessToken, files) { item ->
                                        if (item.name.endsWith(".pdf")) {
                                            val dateModified = lastModifiedMap[item.name]

                                            // Mark for rewriting if we do not have the file in the manifest or if the file has been modified
                                            if (dateModified == null || dateModified != item.lastModifiedDateTime.time.toString()) {
                                                // Get InputStream of the file
                                                val stream = Graph.getInputStreamFromDriveItem(accessToken, item)

                                                // Strip the text from the PDF
                                                val text = if (stream != null) {
                                                    val pdDocument = PDDocument.load(stream)
                                                    val text = PDFTextStripper().getText(pdDocument)
                                                    pdDocument.close()

                                                    text
                                                } else {
                                                    null
                                                }

                                                // Put in map
                                                result[item] = text
                                            } else {
                                                println("Skipping ${item.name}")
                                                result[item] = null
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        return result
    }

    private fun writeTranslations(driveItemsToString: MutableMap<DriveItem, String?>) {
        driveItemsToString.entries.forEach { (item, text) ->
            if (text != null) { // We have new text to translate
                // Translate the text line by line

                var result = text
                val surfaceToRomaji = Jakaroma().translate(text)
                surfaceToRomaji.entries.forEach { (surface, romaji) ->
                    if(surface != romaji && surface != "**") {
                        result = result!!.replace(surface, "$surface ($romaji) ")
                    }
                }

                // Write result to an appropriately named file
                val dir = File("./translatedFiles")
                if (!dir.isDirectory && !dir.exists()) {
                    dir.mkdir()
                }
                val newFileName = "${item.name.substring(0, item.name.length-4)}.txt"

                File("./translatedFiles/$newFileName").writeText(result!!)
            }
        }
    }

    private fun writeManifestFile(driveItemsToString: MutableMap<DriveItem, String?>) {
        // Update the manifest file
        val manifestFile = File("./translatedFiles/.manifest")

        manifestFile.writeText("")
        val writer = manifestFile.writer()
        driveItemsToString.forEach { (item, _) ->
            writer.appendLine("${item.name} | ${item.lastModifiedDateTime.time}")
        }
        writer.close()
    }

    fun updateTranslatedFiles(accessToken: String) {
        val user = Graph.getUser(accessToken)
        println("Logged in as ${user?.displayName}")

        println("Fetching files...")
        val itemsToNewText = getPDFTextStrings(accessToken).toMutableMap()
        println("Done fetching")

        println("Translating text...")
        writeTranslations(itemsToNewText)
        println("Text translated")

        println("Updating manifest...")
        writeManifestFile(itemsToNewText)
        println("Manifest updated")
    }

    companion object {
        suspend inline operator fun invoke(token: String, builder: JapaneseBotBuilder.() -> Unit): JapaneseBot {
            return JapaneseBotBuilder(token).apply { builder() }.build()
        }
    }
}