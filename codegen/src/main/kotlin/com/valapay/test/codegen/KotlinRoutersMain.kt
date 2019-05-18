package com.valapay.test.codegen

import com.valapay.test.codegen.Generators.generateAddresses
import com.valapay.test.codegen.Generators.generateRouters
import com.valapay.test.codegen.Generators.generateRoutersVerticles
import com.valapay.test.codegen.VelocityUtils.createVelocityEngine
import com.valapay.test.codegen.descriptors.Router
import com.valapay.test.codegen.descriptors.RoutersCreator
import com.valapay.test.codegen.typemappers.KotlinTypeMapper
import java.io.File


object KotlinRoutersMain {

    @JvmStatic
    fun generate(
        generationDir: File, addressesClassName: String, routersTemplateName: String,
        routersPackage: String, routerVerticleTemplate: String, verticlesPackage: String,
        eventBusAddressesTemplate: String, eventBusPackage: String,
        frontendAnnoPackage: String
    ) {
        val endpoints: List<Router> = RoutersCreator(KotlinTypeMapper, routersPackage).createRouters()

        val velocityEngine = createVelocityEngine()

        generateRouters(
            velocityEngine, endpoints, generationDir, addressesClassName, routersTemplateName,
            routersPackage
        )
        generateRoutersVerticles(
            velocityEngine, endpoints, generationDir, addressesClassName, routerVerticleTemplate,
            verticlesPackage
        )
        generateAddresses(
            velocityEngine, endpoints, generationDir, addressesClassName, eventBusAddressesTemplate,
            eventBusPackage
        )
    }


}