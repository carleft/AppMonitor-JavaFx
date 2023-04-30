module com.tb.appmonitor {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.logging;
    requires com.sun.jna.platform;
    requires kotlin.stdlib;
    requires com.sun.jna;
    requires kotlinx.coroutines.core.jvm;
    requires java.desktop;
    requires lombok;
    requires org.apache.logging.log4j;
    requires org.apache.logging.log4j.core;
    requires slf4j.api;

    exports com.tb.appmonitor;
    opens com.tb.appmonitor to javafx.fxml;
    exports com.tb.appmonitor.controller;
    opens com.tb.appmonitor.controller to javafx.fxml;
    //自定义Appender需要将该包开放给org.apache.logging.log4j.core
    opens com.tb.appmonitor.log;
    exports com.tb.appmonitor.log to org.apache.logging.log4j.core;
}