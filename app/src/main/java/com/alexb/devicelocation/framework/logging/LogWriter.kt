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
    private val writer: PrintWriter? by lazy { writer() }

    init {
        startWriterJob()
    }

    fun writeLine(line: String) {
        channel.offer(line)
    }

    private fun writer(): PrintWriter? {
        return runCatching {
            val file = File(logPath, logFileName)
            val fileWriter = FileWriter(file, file.exists())
            val bufferedWriter = BufferedWriter(fileWriter)
            PrintWriter(bufferedWriter)
        }.getOrNull()
    }

    private fun startWriterJob() {
        scope.launch {
            for (line in channel) {
                runCatching { writeLineToFile(line) }
            }
        }
    }

    private fun writeLineToFile(line: String) {
        writer?.run {
            println(line)
            flush()
        }
    }

    companion object {
        private const val logFileName = "location.log"
    }
}