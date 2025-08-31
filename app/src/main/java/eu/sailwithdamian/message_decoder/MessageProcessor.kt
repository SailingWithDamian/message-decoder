package eu.sailwithdamian.message_decoder

import android.os.Environment
import java.io.ByteArrayInputStream
import java.io.File
import java.io.FileOutputStream
import java.time.Instant
import java.time.format.DateTimeFormatter
import java.util.Base64
import java.util.zip.InflaterInputStream

data class Message(
    val type: String,
    val number: Int,
    val total: Int,
    val payload: String
)

fun ExportMessages(messages: List<Message>): String? {
    val downloadsDir =
        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
    if (!downloadsDir.exists()) {
        downloadsDir.mkdirs()
    }
    val fileName: String = DateTimeFormatter.ISO_INSTANT.format(Instant.now()).replace(":", "-")
        .replace(".", "-") + "." + messages[0].type
    val outputFile = File(downloadsDir, fileName)

    messages.joinToString(separator = "") { it.payload }

    try {
        FileOutputStream(outputFile).use { fileOutputStream ->
            val encodedPayload = messages.joinToString(separator = "") { it.payload }
            val compressedBytes = Base64.getDecoder().decode(encodedPayload)
            val byteInputStream = ByteArrayInputStream(compressedBytes)
            InflaterInputStream(byteInputStream).use { inflater ->
                val buffer = ByteArray(1024)
                var bytesRead: Int
                while (inflater.read(buffer).also { bytesRead = it } != -1) {
                    fileOutputStream.write(buffer, 0, bytesRead)
                }
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
        return null
    }
    return outputFile.name
}

fun DecodeMessage(input: String): Message? {
    val parts = input.split(":")
    if (parts.size != 5 || parts[0] != "msg") {
        return null
    }
    return Message(
        type = parts[1],
        number = parts[2].toInt(),
        total = parts[3].toInt(),
        payload = parts[4]
    )
}
