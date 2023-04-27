package com.tb.appmonitor;

import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import java.net.URL;
import java.util.ResourceBundle;


public class MainController implements Initializable {
    private Thread monitorThread;
    @FXML
    private Button buttonStart;
    @FXML
    private Button buttonEnd;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        monitorThread = null;
        buttonStart.setDisable(false);
        buttonEnd.setDisable(true);
    }

    @FXML
    protected void onMonitorStartClick() {
        if (monitorThread == null ||
                monitorThread.getState() == Thread.State.TERMINATED) {
            //线程未新建或线程已终止
            //重新创建线程并运行
            MonitorTask monitorTask = new MonitorTask("notepad.exe", 2000L, event -> {
                buttonStart.setDisable(false);
                buttonEnd.setDisable(true);
            });
            monitorThread = new Thread(monitorTask);
            monitorThread.setDaemon(true);
            monitorThread.setName("monitorThread");
            monitorThread.start();
            //设置按键
            buttonStart.setDisable(true);
            buttonEnd.setDisable(false);
        }
    }

    @FXML
    protected void onMonitorEndClick() {
        buttonStart.setDisable(false);
        buttonEnd.setDisable(true);
        if (monitorThread != null) {
            if (monitorThread.isAlive()) {
                monitorThread.interrupt();
            }
            monitorThread = null;
        }
    }

    public void onStageDestroyed() {
        onMonitorEndClick();
    }
}