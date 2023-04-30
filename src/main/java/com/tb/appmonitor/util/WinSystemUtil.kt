package com.tb.appmonitor.util

import com.sun.jna.Pointer
import com.sun.jna.platform.win32.User32
import com.sun.jna.platform.win32.WinDef.HWND
import com.sun.jna.platform.win32.WinUser
import com.sun.jna.ptr.IntByReference
import com.tb.appmonitor.log.Slf4jKt
import com.tb.appmonitor.log.Slf4jKt.Companion.log
import javafx.application.Platform
import javafx.stage.Stage
import java.awt.*
import java.awt.event.MouseEvent
import java.awt.event.MouseListener
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader


@Slf4jKt
object WinSystemUtil {

    //系统托盘图标
    private var trayIcon: TrayIcon? = null
    //程序主舞台
    private var primaryStage: Stage? = null

    /**
     * 初始化系统托盘图标
     */
    @JvmStatic
    fun initTrayIcon(stage: Stage) {
        primaryStage = stage
        if (trayIcon == null) {
            trayIcon = TrayIcon(Toolkit.getDefaultToolkit().getImage(Const.APP_ICON_FILE_NAME), Const.APP_NAME).apply {
                isImageAutoSize = true
                toolTip = Const.APP_NAME
                addMouseListener (object: MouseListener {
                    override fun mouseClicked(e: MouseEvent?) {
                        if (e?.clickCount == 2) {
                            //鼠标双击，展示主舞台（在JavaFx主线程执行）
                            Platform.runLater { primaryStage?.show() }
                        }
                    }
                    override fun mousePressed(e: MouseEvent?) {}
                    override fun mouseReleased(e: MouseEvent?) {}
                    override fun mouseEntered(e: MouseEvent?) {}
                    override fun mouseExited(e: MouseEvent?) {}
                })
                // 创建托盘图标菜单
                val popupMenu = PopupMenu()
                val exitMenuItem = MenuItem("退出程序")
                exitMenuItem.addActionListener { _->
                    //移除图标
                    SystemTray.getSystemTray().remove(this)
                    //关闭程序
                    Platform.exit()
                }
                popupMenu.add(exitMenuItem)
                this.popupMenu = popupMenu
            }.also {
                if (SystemTray.isSupported()) {
                    log.info("initTrayIcon, added")
                    SystemTray.getSystemTray().add(it)
                }
            }
        }
    }

    /**
     * 通过窗口标题获取窗口
     */
    @JvmStatic
    fun getHWNDByTitle(windowTitle: String): HWND? {
        return User32.INSTANCE.FindWindow(null, windowTitle).also {
            log.info("getHWNDByTitle, windowTitle: $windowTitle, HWND: $it")
        }
    }


    /**
     * 执行Exe程序
     * @param exeAbsolutePath 绝对路径
     */
    @JvmStatic
    fun runExe(exeAbsolutePath: String) {
        log.info("runExe, path: $exeAbsolutePath")
        try {
            val command = "cmd /c \"$exeAbsolutePath\""
            Runtime.getRuntime().exec(command)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    /**
     * 将窗口置于前台
     */
    @JvmStatic
    fun setWindowForeground(hwnd: HWND?) {
        log.info("setWindowForeground, hwnd: $hwnd")
        hwnd?.let {
            val currentForegroundWindow = User32.INSTANCE.GetForegroundWindow()
            log.info("currentForegroundWindow: $currentForegroundWindow")
            if (it != currentForegroundWindow) {
                // 最小化窗口
                User32.INSTANCE.ShowWindow(it, User32.SW_MINIMIZE)
                // 还原窗口
                User32.INSTANCE.ShowWindow(it, User32.SW_RESTORE)
                // 将窗口置于前台
                User32.INSTANCE.SetForegroundWindow(it)
            }
        }
    }

    /**
     * 通过窗口名称将窗口置于前台
     */
    @JvmStatic
    fun setWindowForegroundByTitle(windowTitle: String) {
        User32.INSTANCE.FindWindow(null, windowTitle).also {
            setWindowForeground(it)
        }

    }

    /**
     * 通过exe名称将窗口设置在前台
     */
    @JvmStatic
    fun setWindowForegroundByExe(exeName: String) {
        val hwnd = findHWNDByExe(exeName)
        val currentForegroundWindow = User32.INSTANCE.GetForegroundWindow()
        log.info("setWindowForegroundByExe, hwnd: $hwnd, currentForegroundWindow: $currentForegroundWindow")
        hwnd?.let {
            if (it != currentForegroundWindow) {
                // 最小化窗口
                User32.INSTANCE.ShowWindow(it, User32.SW_MINIMIZE)
                // 还原窗口
                User32.INSTANCE.ShowWindow(it, User32.SW_RESTORE)
                // 将窗口置于前台
                User32.INSTANCE.SetForegroundWindow(it)
            }
        }
    }

    /**
     * 通过exe名称查询窗口句柄
     */
    @JvmStatic
    fun findHWNDByExe(exeName: String): HWND? {
        var hwnd: HWND? = null
        try {
            val command = "cmd /c for /f \"tokens=2 delims=,\" %a in ('tasklist /fi \"imagename eq ${exeName}\" /fo csv /nh ^| findstr /i \"${exeName}\"') do echo %~a"
            val process = Runtime.getRuntime().exec(command)
            val inputStream = process.inputStream
            val reader = BufferedReader(InputStreamReader(inputStream))
            val pidList: ArrayList<Int> = ArrayList()
            var line: String?
            //按行读取，将所有进程号保存到pidList中
            while (reader.readLine().also { line = it } != null) {
                line?.toIntOrNull()?.let {
                    pidList.add(it)
                }
            }
            pidList.takeIf {it.isNotEmpty()}?.let { pidList ->
                pidList.forEach { pid ->
                    hwnd = findHWNDByPID(pid)
                    if (hwnd != null) return@let
                }
            }
            reader.close()
            inputStream.close()
            process.waitFor()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return hwnd.also {
            log.info("findHWNDByExe, HWND: $it")
        }
    }

    /**
     * 通过进程号获取窗口句柄
     */
    @JvmStatic
    fun findHWNDByPID(pid: Int): HWND? {
        var hwnd: HWND? = null
        val lpEnumFunc = object: WinUser.WNDENUMPROC {
            override fun callback(hWnd: HWND?, data: Pointer?): Boolean {
                val pidRef: IntByReference = IntByReference()
                //获取该窗口对应的pid
                User32.INSTANCE.GetWindowThreadProcessId(hWnd, pidRef)
                if (pidRef.value == pid) {
                    //返回 false 停止枚举
                    hwnd = hWnd
                    return false
                }
                // 返回 true 继续枚举
                return true
            }
        }
        //枚举所有顶级窗口，该方法为阻塞方法
        User32.INSTANCE.EnumWindows(lpEnumFunc, Pointer.createConstant(0))
        return hwnd.also {
            log.info("findHWNDByPID, HWND: $it")
        }
    }

    @JvmStatic
    fun isExeRunning(exe: String): Boolean {
        var isRunning = false
        try {
            val process = Runtime.getRuntime().exec("tasklist.exe /FI \"IMAGENAME eq ${exe}\"")
            val inputStream = process.inputStream
            val reader = BufferedReader(InputStreamReader(inputStream))
            var line: String?
            //按行读取，如果某一行包含"PID"，则表示记事本程序正在运行中
            while (reader.readLine().also { line = it } != null) {
                if (line?.contains("PID") == true) {
                    isRunning = true
                    break
                }
            }
            reader.close()
            inputStream.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return isRunning.also {
            log.info("isExeRunning: $it")
        }
    }
}