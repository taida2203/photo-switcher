package com.example.photoswitcher.utils

import java.io.File
import java.io.FileInputStream
import java.util.zip.ZipInputStream

object FileHelper {
    fun unzip(file: File): List<UnzippedFile> = ZipInputStream(FileInputStream(file))
        .use { zipInputStream ->
            generateSequence { zipInputStream.nextEntry }
                .filterNot { it.isDirectory }
                .map {
                    UnzippedFile(
                        filename = it.name,
                        content = zipInputStream.readBytes()
                    )
                }.toList()
        }
}

data class UnzippedFile(val filename: String, val content: ByteArray) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as UnzippedFile

        if (filename != other.filename) return false
        if (!content.contentEquals(other.content)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = filename.hashCode()
        result = 31 * result + content.contentHashCode()
        return result
    }
}

