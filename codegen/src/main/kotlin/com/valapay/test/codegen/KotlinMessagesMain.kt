package com.valapay.test.codegen

import com.valapay.test.codegen.Generators.generatCodecsRegistration
import com.valapay.test.codegen.descriptors.MessagesCreator
import com.valapay.test.codegen.typemappers.KotlinTypeMapper
import java.io.File


object KotlinMessagesMain {

    @JvmStatic
    fun generate(
        generationDir: File, messagePackages: List<String>, codecPackage: String,
        codecsClassName: String, templateName: String, frontendAnnoPackage: String
    ) {
        val messagesDescriptor =
            MessagesCreator(
                KotlinTypeMapper, frontendAnnoPackage
            )
        val messages = messagePackages.map { messagesDescriptor.createMessages(it) }.flatten()

        val velocityEngine = VelocityUtils.createVelocityEngine()

        generatCodecsRegistration(
            velocityEngine,
            messages,
            generationDir,
            codecsClassName,
            templateName,
            codecPackage
        )
    }

}