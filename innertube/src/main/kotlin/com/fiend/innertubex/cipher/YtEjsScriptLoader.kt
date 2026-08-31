package com.fiend.innertubex.cipher

import kotlin.text.Charsets

internal fun readYtEjsSolverScript(fileName: String): String {
    val path = "yt_ejs/$fileName"
    val stream =
        Thread.currentThread().contextClassLoader?.getResourceAsStream(path)
            ?: YtEjsScriptLoader::class.java.classLoader?.getResourceAsStream(path)
            ?: YtEjsScriptLoader::class.java.getResourceAsStream("/$path")
            ?: YtEjsScriptLoader::class.java.getResourceAsStream(path)
    requireNotNull(stream) { "Missing classpath resource: $path" }
    return stream.bufferedReader(Charsets.UTF_8).use { it.readText() }
}

private object YtEjsScriptLoader
