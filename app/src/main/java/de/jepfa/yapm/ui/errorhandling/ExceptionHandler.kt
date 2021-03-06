package de.jepfa.yapm.ui.errorhandling

import android.app.Activity
import android.content.Intent
import android.os.Process
import android.util.Log
import de.jepfa.yapm.util.DebugInfo.getDebugInfo
import java.io.PrintWriter
import java.io.StringWriter

/*
 Inspired from https://github.com/hardik-trivedi/ForceClose
 */
class ExceptionHandler(private val context: Activity) : Thread.UncaughtExceptionHandler {

    override fun uncaughtException(thread: Thread, exception: Throwable) {
        val stackTrace = StringWriter()
        exception.printStackTrace(PrintWriter(stackTrace))
        Log.e("EXH", "caught exception", exception)
        val errorReport = StringBuilder()
        errorReport.append("************ CAUSE OF ERROR ************\n\n")
        errorReport.append(stackTrace.toString())

        errorReport.append(getDebugInfo(context))

        val intent = Intent(context, ErrorActivity::class.java)
        intent.putExtra("error", errorReport.toString())
        context.startActivity(intent)
        Process.killProcess(Process.myPid())

        System.exit(10)
    }

}

