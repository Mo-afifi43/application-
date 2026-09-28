package com.mhma.nibras.content

import java.io.File

/** Reads files from the module's assets folder, whether tests run from the module or the project root. */
object TestAssets {
    private val roots = listOf(File("src/main/assets"), File("app/src/main/assets"))

    fun read(path: String): String {
        val file = roots.map { File(it, path) }.firstOrNull { it.isFile }
            ?: throw IllegalArgumentException("Asset not found: $path")
        return file.readText(Charsets.UTF_8)
    }

    fun exists(path: String): Boolean = roots.any { File(it, path).isFile }

    fun repository(): ContentRepository = ContentRepository { read(it) }
}
