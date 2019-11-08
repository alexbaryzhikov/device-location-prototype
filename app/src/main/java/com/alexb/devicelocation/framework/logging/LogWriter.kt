package com.alexb.devicelocation.framework.logging

import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.Channel.Factory.BUFFERED
import kotlinx.coroutines.launch
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.io.PrintWriter

class LogWriter(context: Context) {

    private val logPath = context.getExternalFilesDir(null)
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val channel = Channel<String>(BUFFERED)
    private var writer: PrintWriter? = null

    init {
        startWriterJob()
    }

    fun writeLine(line: String) {
        channel.offer(line)
    }


    private fun startWriterJob() {
        scope.launch {
            for (line in channel) {
                runCatching { writeLineToFile(line) }
            }
        }
    }

    private fun writeLineToFile(line: String) {
        writer()?.run {
            println(line)
            flush()
        }
    }

    private fun writer(): PrintWriter? {
        return writer ?: runCatching {
            writer = createWriter()
            writer
        }.getOrNull()
    }

    private fun createWriter(): PrintWriter {
        val file = File(logPath, logFileName)
        val fileWriter = FileWriter(file, file.exists())
        val bufferedWriter = BufferedWriter(fileWriter)
        return PrintWriter(bufferedWriter)
    }

    companion object {
        private const val logFileName = "location.log"
    }
}