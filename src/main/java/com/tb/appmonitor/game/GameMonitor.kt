package com.tb.appmonitor.game

import com.sun.jna.platform.win32.WinDef
import com.tb.appmonitor.log.Slf4jKt
import com.tb.appmonitor.log.Slf4jKt.Companion.log
import com.tb.appmonitor.util.Const
import com.tb.appmonitor.util.PropertiesUtil
import com.tb.appmonitor.util.WinSystemUtil
import kotlinx.coroutines.*
import java.io.File

/**
 * 游戏进程监视器
 */
@Slf4jKt
object GameMonitor {

    //程序启动超时时间，单位秒
    private const val RUN_EXE_TIME_OUT = 30

    private var job: Job? = null
    private val scope = CoroutineScope(Dispatchers.Default)
    private var battlenetHWND: WinDef.HWND? = null
    private var hearthstoneHWND: WinDef.HWND? = null

    /**
     * 开始运行游戏
     */
    @JvmStatic
    fun startExecuteGame() {
        log.warn("开始运行！")
        if (job?.isActive != true) {
            job = scope.launch {
                while (isActive) {
                    log.warn("监控程序轮询检查...")
                    //检查战网
                    checkBattlenet()
                    //检查炉石传说
                    checkHearthStone()
                    delay(10000)
                }
            }
        }
    }

    /**
     * 停止脚本
     */
    @JvmStatic
    fun stop() {
        log.warn("停止运行！")
        job?.cancel()
    }

    @JvmStatic
    private suspend fun checkBattlenet() {
        log.warn("正在检查战网")
        if (WinSystemUtil.getHWNDByTitle(Const.BATTLENET_WINDOW_NAME).also { 
                battlenetHWND = it
            } == null) {
            log.warn("未检测到战网窗口，正在启动战网...")
            if (isBattlenetPathVaild()) {
                //战网程序绝对路径
                val absolutePath = PropertiesUtil.getProperty(Const.PROPERTIES_KEY_BATTLENET_FILE_PATH) +
                        "\\" + Const.BATTLENET_EXE_NAME
                WinSystemUtil.runExe(absolutePath)
                //等待战网启动
                withContext(Dispatchers.Default) {
                    var times = 0 //等待次数
                    while (true) {
                        log.warn("正在等待战网启动...${++times}")
                        if (times <= RUN_EXE_TIME_OUT) {
                            if (WinSystemUtil.getHWNDByTitle(Const.BATTLENET_WINDOW_NAME).also {
                                    battlenetHWND = it
                                } != null) {
                                log.warn("检查到战网窗口，战网启动成功")
                                break
                            }
                            delay(1000)
                        } else {
                            log.warn("战网启动超时")
                            break
                        }
                    }
                }
            } else {
                log.warn("战网安装目录不正确，请检查")
            }
        } else {
            log.warn("检测到战网程序正在运行中")
        }
    }

    private suspend fun checkHearthStone() {
        log.warn("正在检查炉石传说")
        if (WinSystemUtil.getHWNDByTitle(Const.HEARTHSTONE_WINDOW_NAME).also { 
                hearthstoneHWND = it
            } == null) {
            log.warn("未检测到炉石传说窗口，正在启动炉石传说...")
            if (isHearthstonePathVaild()) {
                if (battlenetHWND != null) {
                    //前置窗口
                    WinSystemUtil.setWindowForeground(battlenetHWND)
                } else {
                    log.warn("未检测到战网窗口")
                }
                //等待战网启动
                withContext(Dispatchers.Default) {
                    var times = 0 //等待次数
                    while (true) {
                        log.warn("正在等待炉石传说启动...${++times}")
                        if (times <= RUN_EXE_TIME_OUT) {
                            if (WinSystemUtil.getHWNDByTitle(Const.HEARTHSTONE_WINDOW_NAME).also {
                                    hearthstoneHWND = it
                                } != null) {
                                log.warn("检查到炉石传说窗口，炉石传说启动成功")
                                break
                            }
                            delay(1000)
                        } else {
                            log.warn("炉石传说启动超时")
                            break
                        }
                    }
                }
            } else {
                log.warn("炉石传说安装目录不正确，请检查")
            }
        } else {
            log.warn("检测到炉石传说正在运行中")
        }
    }

    /**
     * 战网路径是否有效
     */
    @JvmStatic
    fun isBattlenetPathVaild(directory: File =
                                 File(PropertiesUtil.getProperty(Const.PROPERTIES_KEY_BATTLENET_FILE_PATH))): Boolean {
        val targetExe = File(directory, Const.BATTLENET_EXE_NAME)
        return targetExe.exists().also {
            log.info("isBattlenetPathVaild: $it")
        }
    }

    /**
     * 炉石传说路径是否有效
     */
    @JvmStatic
    fun isHearthstonePathVaild(directory: File =
                                   File(PropertiesUtil.getProperty(Const.PROPERTIES_KEY_HEARTHSTONE_FILE_PATH))): Boolean {
        val targetExe = File(directory, Const.HEARTHSTONE_EXE_NAME)
        return targetExe.exists().also {
            log.info("isHearthstonePathVaild: $it")
        }
    }
}