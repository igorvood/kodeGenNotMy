package com.valapay.test.codegen

import com.valapay.test.codegen.Generators.generatCodecsRegistration
import com.valapay.test.codegen.Generators.generatCrudVerticles
import com.valapay.test.codegen.Generators.generateAddresses
import com.valapay.test.codegen.Generators.generateRouters
import com.valapay.test.codegen.descriptors.EntitiesCreator
import com.valapay.test.codegen.descriptors.Entity
import com.valapay.test.codegen.descriptors.Router
import com.valapay.test.codegen.typemappers.KotlinTypeMapper
import java.io.File


object KotlinEntitiesMain {

    @JvmStatic
    fun generate(
        generationDir: File, crudAddressesClassName: String, routerTemplate: String,
        routerPackageName: String,
        evenyBusAddressesTemplate: String,
        eventBusPackageName: String,
        crudVerticlesTemplate: String,
        verticlesPackage: String,
        codecsRegistrationClass: String,
        codecsRegistrationTemplateName: String,
        entitiesPackage: String,
        frontendAnnoPackage: String
    ) {
        val entitiesDescriptor = EntitiesCreator(KotlinTypeMapper, frontendAnnoPackage)
        val entities = entitiesDescriptor.createEntities(entitiesPackage)
        val entitiesByName = entities.groupBy { it.name }.mapValues { entry -> entry.value[0] }
        val entitiesByParent: Map<String, List<Entity>> =
            entities.map { e -> e.parents.map { Pair(it, e) } }.flatten().groupBy { it.first }
                .mapValues { l -> l.value.map { it.second } }
        entitiesByParent.keys.forEach {
            entitiesByName[it]?.children = entitiesByParent[it] ?: listOf()
        }

        val entitiesForJpaAndVerticles = entities.filter { it.shouldGenerateRouterAndVerticle() }

        val router = Router(
            "CrudRouter", "/crud",
            entitiesForJpaAndVerticles.map { entitiesDescriptor.createEntityRestEndpoints(it) }.flatten()
        )

        val velocityEngine = VelocityUtils.createVelocityEngine()

        generateRouters(
            velocityEngine, listOf(router), generationDir, crudAddressesClassName, routerTemplate,
            routerPackageName
        )
        generateAddresses(
            velocityEngine, listOf(router), generationDir, crudAddressesClassName,
            evenyBusAddressesTemplate, eventBusPackageName
        )
        generatCrudVerticles(
            velocityEngine, entitiesForJpaAndVerticles, generationDir, crudVerticlesTemplate,
            verticlesPackage
        )
        generatCodecsRegistration(
            velocityEngine,
            entities,
            generationDir,
            codecsRegistrationClass,
            codecsRegistrationTemplateName,
            eventBusPackageName
        )
    }


}