package com.tb.appmonitor

import com.tb.appmonitor.util.WinSystemUtil.Companion.isExeRunning
import com.tb.appmonitor.util.WinSystemUtil.Companion.runExe
import com.tb.appmonitor.util.WinSystemUtil.Companion.setWindowForegroundByExe
import javafx.concurrent.Task
import javafx.concurrent.WorkerStateEvent
import javafx.event.EventHandler

/**
 * 监控任务
 * 每个一段时间检查程序，保持程序运行且窗口在前台
 * @param exeName 进程名称，以exe结尾
 * @param interval 监控间隔，单位ms
 * @param onFailedListener 错误回调
 */
class MonitorTask(private val exeName: String,
                  private val interval: Long,
                  private val onFailedListener: EventHandler<WorkerStateEvent>)
    : Task<Void>() {

    init {
        setOnFailed {
            @Suppress("JAVA_MODULE_DOES_NOT_EXPORT_PACKAGE")
            exception?.printStackTrace()
            onFailedListener.handle(it)
        }
    }

    @Throws(Exception::class)
    override fun call(): Void? {
        while (true) {
            if (!isExeRunning(exeName)) {
                runExe(exeName)
            }
            setWindowForegroundByExe(exeName)
            Thread.sleep(interval)
        }
    }
}