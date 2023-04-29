package com.tb.appmonitor.controller

import com.tb.appmonitor.game.GameMonitor
import com.tb.appmonitor.util.*
import com.tb.appmonitor.util.Slf4jKt.Companion.log
import javafx.application.Application
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.fxml.Initializable
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.Label
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

    /**
     * 初始化时调用
     */
    override fun initialize(location: URL?, resources: ResourceBundle?) {
        labelBattlenetPath?.text = PropertiesUtil.getProperty(Const.PROPERTIES_KEY_BATTLE_NET_FILE_PATH)
        labelHearthstonePath?.text = PropertiesUtil.getProperty(Const.PROPERTIES_KEY_HEARTHSTONE_FILE_PATH)
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
        val chooser = DirectoryChooser()
        chooser.title = "请设置战网安装目录"
        val selectFile = chooser.showDialog(Stage())
        selectFile?.let {
            labelBattlenetPath?.text = it.absolutePath
            //保存配置
            PropertiesUtil.setProperty(Const.PROPERTIES_KEY_BATTLE_NET_FILE_PATH, it.absolutePath)
        }
    }

    @FXML
    fun onSettingHearthstonePath() {
        val chooser = DirectoryChooser()
        chooser.title = "请设置炉石传说安装目录"
        val selectFile = chooser.showDialog(Stage())
        selectFile?.let {
            labelHearthstonePath?.text = it.absolutePath
            //保存配置
            PropertiesUtil.setProperty(Const.PROPERTIES_KEY_HEARTHSTONE_FILE_PATH, it.absolutePath)
        }
    }

    fun onStageDestroyed() {

    }
}