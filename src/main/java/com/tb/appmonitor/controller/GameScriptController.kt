package com.tb.appmonitor.controller

import com.tb.appmonitor.game.GameMonitor
import com.tb.appmonitor.log.JavaFxAppender
import com.tb.appmonitor.log.Slf4jKt
import com.tb.appmonitor.util.*
import com.tb.appmonitor.log.Slf4jKt.Companion.log
import javafx.application.Application
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.fxml.Initializable
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.TextArea
import javafx.stage.DirectoryChooser
import javafx.stage.Stage
import java.net.URL
import java.util.*

@Slf4jKt
class GameScriptController: Initializable {

    companion object {
        /**
         * 展示此页面
         */
        @JvmStatic
        fun showScene(clazz: Class<out Application>, stage: Stage) {
            val loader = FXMLLoader(clazz.getResource("game_scene.fxml"))
            stage.scene = Scene(loader.load(), 640.0, 480.0)
            loader.getController<GameScriptController>().primaryStage = stage
            stage.setOnCloseRequest {
                loader.getController<GameScriptController>().onStageDestroyed()
            }
            stage.title = Const.APP_NAME
            stage.show()
        }
    }

    var primaryStage: Stage? = null
    @FXML
    private var btnStart: Button? = null
    @FXML
    private var btnEnd: Button? = null
    @FXML
    private var labelBattlenetPath: Label? = null
    @FXML
    private var labelHearthstonePath: Label? = null
    @FXML
    private var textAreaLog: TextArea? = null

    /**
     * 初始化时调用
     */
    override fun initialize(location: URL?, resources: ResourceBundle?) {
        labelBattlenetPath?.text = PropertiesUtil.getProperty(Const.PROPERTIES_KEY_BATTLENET_FILE_PATH)
        labelHearthstonePath?.text = PropertiesUtil.getProperty(Const.PROPERTIES_KEY_HEARTHSTONE_FILE_PATH)

        //初始化日志输出的控件
        JavaFxAppender.textArea = textAreaLog
    }

    @FXML
    fun onStartClick() {
        log.info("onStartClick")
        GameMonitor.startExecuteGame()
    }

    @FXML
    fun onEndClick() {
        log.info("onEndClick")
        GameMonitor.stop()
    }

    @FXML
    fun onSettingBattlenetPath() {
        DirectoryChooser().apply {
            title = "请设置战网安装目录"
        }.showDialog(Stage())?.let {
            if (GameMonitor.isBattlenetPathVaild(it)) {
                //路径合法，保存配置
                log.warn("设置战网安装目录成功")
                labelBattlenetPath?.text = it.absolutePath
                PropertiesUtil.setProperty(Const.PROPERTIES_KEY_BATTLENET_FILE_PATH, it.absolutePath)
            } else {
                //路径非法
                log.warn("设置战网安装目录失败，目标文件不存在")
                labelBattlenetPath?.text = ""
                PropertiesUtil.setProperty(Const.PROPERTIES_KEY_BATTLENET_FILE_PATH, "")
            }
        }
    }

    @FXML
    fun onSettingHearthstonePath() {
        DirectoryChooser().apply {
            title = "请设置炉石传说安装目录"
        }.showDialog(Stage())?.let {
            if (GameMonitor.isHearthstonePathVaild(it)) {
                //路径合法，保存配置
                log.warn("设置炉石传说安装目录成功")
                labelHearthstonePath?.text = it.absolutePath
                PropertiesUtil.setProperty(Const.PROPERTIES_KEY_HEARTHSTONE_FILE_PATH, it.absolutePath)
            } else {
                //路径非法
                log.warn("设置炉石传说安装目录失败，目标文件不存在")
                labelHearthstonePath?.text = ""
                PropertiesUtil.setProperty(Const.PROPERTIES_KEY_HEARTHSTONE_FILE_PATH, "")
            }
        }
    }

    fun onStageDestroyed() {

    }
}