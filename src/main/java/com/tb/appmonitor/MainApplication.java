package com.tb.appmonitor;

import com.tb.appmonitor.controller.GameScriptController;
import com.tb.appmonitor.util.Const;
import com.tb.appmonitor.util.RobotUtil;
import com.tb.appmonitor.util.WinSystemUtil;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MainApplication extends Application {
    @Override
    public void start(Stage stage) {
        //初始化RobotUtil类
        //TODO:更优雅的实现方式
        RobotUtil.INSTANCE.getRobot();
        //设置应用程序图标
        stage.getIcons().add(new Image(Const.APP_ICON_FILE_NAME));

        //默认为true
        //当true时，关闭最后一个窗口，程序也关闭，将会调用Application.stop()
        //false，关闭最后一个窗口，程序继续运行,除非使用Platform.exit()
        Platform.setImplicitExit(false);

        //设置窗口隐藏逻辑
        stage.setOnHidden(event -> {
            hideWindow(stage);
        });
        //展示场景
        GameScriptController.showScene(getClass(), stage);
    }

    public static void main(String[] args) {
        launch();
    }

    private void hideWindow(Stage stage) {
        log.info("hideWindow");
        //隐藏窗口时展示系统托盘图标
        WinSystemUtil.initTrayIcon(stage);
    }
}