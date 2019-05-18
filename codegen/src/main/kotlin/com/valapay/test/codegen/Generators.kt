package com.valapay.test.codegen

import com.valapay.test.codegen.descriptors.Entity
import com.valapay.test.codegen.descriptors.Message
import com.valapay.test.codegen.descriptors.Router
import org.apache.commons.lang3.StringUtils
import org.apache.velocity.Template
import org.apache.velocity.VelocityContext
import org.apache.velocity.app.VelocityEngine
import java.io.File

object Generators {
    fun generateRouters(
        velocityEngine: VelocityEngine, routers: List<Router>, parentDir: File,
        addressesClassName: String, templateName: String, routersPackage:String
    ) {
        val t: Template = velocityEngine.getTemplate(templateName)

        for (router in routers) {

            val context = VelocityContext()
            context.put("addresses", addressesClassName)
            context.put("router", router)
            context.put("utils", GenerationUtils)

            VelocityUtils.generateToFile(
                File(
                    createDir(
                        parentDir,
                        routersPackage
                    ), router.name + ".kt"
                ), context, t
            )

        }
    }

    fun generateRoutersVerticles(velocityEngine: VelocityEngine, routers: List<Router>, parentDir: File,
                                 addressesClassName: String, templateName: String,
                                 verticlesPackage:String
    ) {
        val t: Template = velocityEngine.getTemplate(templateName)

        for (router in routers) {

            val context = VelocityContext()
            context.put("addresses", addressesClassName)
            context.put("router", router)
            context.put("utils", GenerationUtils)

            VelocityUtils.generateToFile(
                File(
                    createDir(
                        parentDir,
                        verticlesPackage
                    ), "Abstract${router.name}Verticle.kt"
                ), context, t
            )

        }
    }

    fun generateTsDTO(
        velocityEngine: VelocityEngine,
        entities: List<Entity>,
        messages: List<Message>,
        parentDir: File,
        templateName: String
    ) {
        val t: Template = velocityEngine.getTemplate(templateName)

        for (message in messages) {
            val context = VelocityContext()
            context.put("entities", entities)
            context.put("messages", messages)
            context.put("entity", message)
            context.put("utils", GenerationUtils)


            VelocityUtils.generateToFile(File(parentDir, message.name + ".ts"), context, t)
        }

        for (entity in entities) {
            val context = VelocityContext()
            context.put("entities", entities)
            context.put("messages", messages)
            context.put("entity", entity)
            context.put("utils", GenerationUtils)


            VelocityUtils.generateToFile(File(parentDir, entity.name + ".ts"), context, t)
        }
    }

    fun generatJpaRepos(
        velocityEngine: VelocityEngine,
        entities: List<Entity>, parentDir: File,
        className: String, repoTemplate: String, repoPackage:String
    ) {
        val t: Template = velocityEngine.getTemplate(repoTemplate)

        val context = VelocityContext()
        context.put("entities", entities)
        context.put("utils", GenerationUtils)
        context.put("stringutils", StringUtils())

        VelocityUtils.generateToFile(
            File(
                createDir(
                    parentDir,
                    repoPackage
                ), "$className.kt"
            ), context, t
        )

    }

    fun generatCrudVerticles(
        velocityEngine: VelocityEngine,
        entities: List<Entity>, parentDir: File,
        templateName: String, verticlesPackage:String
    ) {
        val t: Template = velocityEngine.getTemplate(templateName)

        for (entity in entities) {
            val context = VelocityContext()
            context.put("entity", entity)
            context.put("utils", GenerationUtils)
            val fileName = (if (entity.abstractVerticle) "Abstract" else "" )+"${entity.name}Verticle.kt"
            VelocityUtils.generateToFile(
                File(
                    createDir(
                        parentDir,
                        verticlesPackage
                    ), fileName
                ),
                context, t
            )
        }
    }

    fun generatCodecsRegistration(
        velocityEngine: VelocityEngine, messages: List<*>, parentDir: File,
        className: String, templateName: String, classPackage: String
    ) {
        val t: Template = velocityEngine.getTemplate(templateName)

        val context = VelocityContext()
        context.put("classname", className)
        context.put("messages", messages)
        context.put("package", classPackage)

        VelocityUtils.generateToFile(
            File(createDir(parentDir, classPackage), "$className.kt"), context, t
        )

    }

    fun generateAddresses(
        velocityEngine: VelocityEngine,
        routers: List<Router>, parentDir: File,
        addressesClassName: String,
        templateName: String, eventBusPackage:String
    ) {
        val t = velocityEngine.getTemplate(templateName)

        val context = VelocityContext()
        context.put("classname", addressesClassName)
        context.put("routers", routers)
        context.put("utils", GenerationUtils)

        VelocityUtils.generateToFile(
            File(
                createDir(parentDir, eventBusPackage),
                "$addressesClassName.kt"
            ),
            context,
            t
        )
    }

    private fun createDir(parentDir: File, packageName: String): String {
        return parentDir.absolutePath + File.separator + packageName.replace('.', File.separatorChar)
    }

}