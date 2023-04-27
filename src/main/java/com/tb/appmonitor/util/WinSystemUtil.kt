package com.tb.appmonitor.util

import com.sun.jna.Pointer
import com.sun.jna.platform.win32.User32
import com.sun.jna.platform.win32.WinDef.HWND
import com.sun.jna.platform.win32.WinUser
import com.sun.jna.ptr.IntByReference
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

class WinSystemUtil {

    companion object {

        private val TAG: String = Companion::class.java.simpleName

        @JvmStatic
        fun setWindowForegroundByTitle(windowTitle: String) {
            val hwnd: HWND? = User32.INSTANCE.FindWindow(null, windowTitle)
            hwnd?.let {
                User32.INSTANCE.SetForegroundWindow(it)
            }
        }

        /**
         * 通过exe名称将窗口设置在前台
         */
        @JvmStatic
        fun setWindowForegroundByExe(exeName: String) {
            val hwnd = findHWNDByExe(exeName)
            val currentForegroundWindow = User32.INSTANCE.GetForegroundWindow()
            println("$TAG: setWindowForegroundByExe, hwnd: $hwnd, currentForegroundWindow: $currentForegroundWindow")
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
            return hwnd
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
            return hwnd
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
            println("$TAG: isExeRunning, exe: $exe, isRunning: $isRunning")
            return isRunning
        }

        @JvmStatic
        fun runExe (exe: String) {
            try {
                Runtime.getRuntime().exec(exe)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }
}