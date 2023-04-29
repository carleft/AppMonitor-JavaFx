package com.tb.appmonitor.util

import java.io.File
import java.util.Properties

/**
 * 脚本配置
 */
object PropertiesUtil {

    //配置文件
    private val configFile = File(System.getProperty("user.dir"), Const.PROPERTIES_FILE_NAME).also {
        //文件不存在则创建一个
        if (!it.exists()) {
            it.createNewFile()
        }
    }

    private val properties = Properties().also {
        it.load(configFile.inputStream())
    }

    fun setProperty(key: String, value: String) {
        properties.apply {
            setProperty(key, value)
            store(configFile.outputStream(), null)
        }
    }

    fun getProperty(key: String): String =
        properties.takeIf { it.isNotEmpty() }?.run {
        getProperty(key)
    } ?: ""
}