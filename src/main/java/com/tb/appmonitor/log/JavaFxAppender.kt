package com.tb.appmonitor.log

import javafx.application.Platform
import javafx.scene.control.TextArea
import org.apache.logging.log4j.core.*
import org.apache.logging.log4j.core.appender.AbstractAppender
import org.apache.logging.log4j.core.config.plugins.Plugin
import org.apache.logging.log4j.core.config.plugins.PluginAttribute
import org.apache.logging.log4j.core.config.plugins.PluginElement
import org.apache.logging.log4j.core.config.plugins.PluginFactory
import org.apache.logging.log4j.core.layout.PatternLayout
import java.io.Serializable


@Plugin(name = "JavaFxAppender", category = Core.CATEGORY_NAME, elementType = Appender.ELEMENT_TYPE, printObject = true)
class JavaFxAppender(name: String?,
                     filter: Filter?,
                     layout: Layout<out Serializable>?,
                     ignoreExceptions: Boolean): AbstractAppender(name, filter, layout, ignoreExceptions) {

    override fun append(event: LogEvent?) {
//        Platform.runLater {
//            event?.let {event ->
//                layout.toByteArray(event).takeIf {
//                    it.isNotEmpty()
//                }?.let{
//                    textArea?.appendText(String(it))
//                }
//            }
//        }
        val bytes = layout.toByteArray(event)
        val string = String(bytes)
        Platform.runLater {
            textArea?.appendText(string)
        }
    }

    companion object {
        /**
         * 接收配置文件中的参数
         */
        @PluginFactory
        @JvmStatic
        fun createAppender(
            @PluginAttribute("name") name: String?,
            @PluginElement("Filter") filter: Filter?,
            @PluginElement("Layout") layout: Layout<out Serializable?>?,
            @PluginAttribute("ignoreExceptions") ignoreExceptions: Boolean,
        ): JavaFxAppender? {
            var layout = layout
            if (name == null) {
                LOGGER.error("no name defined in conf.")
                return null
            }
            if (layout == null) {
                layout = PatternLayout.createDefaultLayout()
            }
            return JavaFxAppender(name, filter, layout, ignoreExceptions)
        }

        var textArea: TextArea? = null
    }


}