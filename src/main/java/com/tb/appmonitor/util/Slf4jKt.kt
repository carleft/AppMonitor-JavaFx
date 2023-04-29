package com.tb.appmonitor.util

import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * lombok提供的@Slf4j只在java中可用，自定义一个kotlin的Slf4j注解
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class Slf4jKt{
    companion object{
        val <reified T> T.log: Logger
            inline get() = LoggerFactory.getLogger(T::class.java)
    }
}