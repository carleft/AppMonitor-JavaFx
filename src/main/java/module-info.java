module com.tb.appmonitor {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.logging;
    requires com.sun.jna.platform;
    requires kotlin.stdlib;
    requires com.sun.jna;
    requires kotlinx.coroutines.core.jvm;


    opens com.tb.appmonitor to javafx.fxml;
    exports com.tb.appmonitor;
}