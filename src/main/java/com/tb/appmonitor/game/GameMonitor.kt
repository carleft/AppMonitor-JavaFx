package com.tb.appmonitor.game

import com.tb.appmonitor.util.Const
import com.tb.appmonitor.util.PropertiesUtil
import com.tb.appmonitor.util.WinSystemUtil
import kotlinx.coroutines.*

/**
 * 游戏进程监视器
 */
object GameMonitor {

    private var job: Job? = null
    private val scope = CoroutineScope(Dispatchers.Default)

    /**
     * 开始运行游戏
     */
    @JvmStatic
    fun startExecuteGame() {
        if (job?.isActive != true) {
            job = scope.launch {
                while (true) {
                    val absolutePath = PropertiesUtil.getProperty(Const.PROPERTIES_FILE_NAME) + "\\" + Const.BATTLE_NET_EXE_NAME
                    //检查是否有战网窗口
                    if (WinSystemUtil.getHWNDByTitle(Const.BATTLE_NET_WINDOW_NAME) == null) {
                        WinSystemUtil.runExe(absolutePath)
                    }
                    delay(10000)
                }
            }
        }
    }

    /**
     * 停止脚本
     */
    fun stop() {
        job?.cancel()
    }

    /**
     * 游戏进程是否正在进行
     */
    fun isGameMonitorRunning(): Boolean {
        return job?.isActive == true
    }
}