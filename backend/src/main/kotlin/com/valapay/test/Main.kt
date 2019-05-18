package com.valapay.test

import org.springframework.context.annotation.AnnotationConfigApplicationContext

object Main {
    @JvmStatic
    fun main(args: Array<String>) {
        val springContext = AnnotationConfigApplicationContext()
        springContext.register(RunnerConfiguration::class.java)
        springContext.refresh()
    }
}