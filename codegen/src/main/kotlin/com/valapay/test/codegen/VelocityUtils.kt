package com.valapay.test.codegen

import org.apache.velocity.Template
import org.apache.velocity.VelocityContext
import org.apache.velocity.app.VelocityEngine
import org.apache.velocity.runtime.RuntimeConstants
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader
import java.io.File
import java.io.FileWriter

object VelocityUtils {
    fun generateToFile(file: File, context: VelocityContext, template: Template) {
        val dir = file.parentFile
        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                throw RuntimeException("Can't create dir " + dir.absolutePath)
            }
        }
        val writer = FileWriter(file)
        writer.use {
            template.merge(context, writer)
        }
    }

    fun createVelocityEngine(): VelocityEngine {
        val velocityEngine = VelocityEngine()
        velocityEngine.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath")
        velocityEngine.setProperty("classpath.resource.loader.class", ClasspathResourceLoader::class.java.name)
        velocityEngine.init()
        return velocityEngine
    }
}