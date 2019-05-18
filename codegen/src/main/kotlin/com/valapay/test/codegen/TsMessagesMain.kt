package com.valapay.test.codegen

import com.valapay.test.codegen.Generators.generateTsDTO
import com.valapay.test.codegen.descriptors.EntitiesCreator
import com.valapay.test.codegen.descriptors.MessagesCreator
import com.valapay.test.codegen.typemappers.TsTypeMapper
import java.io.File


object TsMessagesMain {

    @JvmStatic
    fun generate(
        generationDir: File, entitiesPackage: String, messagesPackage: String,
        templateName: String, frontendAnnotationsPachage: String
    ) {
        val velocityEngine = VelocityUtils.createVelocityEngine()

        val entities = EntitiesCreator(TsTypeMapper, frontendAnnotationsPachage).createEntities(entitiesPackage)
        val messages = MessagesCreator(TsTypeMapper, frontendAnnotationsPachage).createMessages(messagesPackage)
        generateTsDTO(velocityEngine, entities, messages, generationDir, templateName)
    }


}