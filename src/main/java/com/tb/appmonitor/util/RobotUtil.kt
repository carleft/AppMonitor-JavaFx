package com.tb.appmonitor.util

import com.tb.appmonitor.log.Slf4jKt
import com.tb.appmonitor.log.Slf4jKt.Companion.log
import javafx.scene.robot.Robot
import java.awt.GraphicsEnvironment
import java.awt.Toolkit

/**
 * Robot工具类
 */
@Slf4jKt
object RobotUtil {

    val robot: Robot = Robot()

    //屏幕宽度
    val screenWidth: Int = Toolkit.getDefaultToolkit().screenSize.width
    //屏幕高度
    val screenHeight: Int = Toolkit.getDefaultToolkit().screenSize.height
    //屏幕横向缩放
    var screenScaleX: Double = 1.0
    //屏幕纵向缩放
    var screenScaleY: Double = 1.0

    init {
        GraphicsEnvironment.getLocalGraphicsEnvironment()
            ?.defaultScreenDevice
            ?.defaultConfiguration
            ?.defaultTransform?.apply {
                screenScaleX = scaleX
                screenScaleY = scaleY
            }
        log.info("init, scaleX: $screenScaleX, scaleY: $screenScaleY")
    }
}