package com.valapay.test.codegen

import com.valapay.test.codegen.Generators.generatJpaRepos
import com.valapay.test.codegen.descriptors.EntitiesCreator
import com.valapay.test.codegen.descriptors.Entity
import com.valapay.test.codegen.typemappers.KotlinTypeMapper
import java.io.File


object KotlinReposMain {

    @JvmStatic
    fun generate(
        generationDir: File, crudReposFileName: String, reposTemplate: String, repoPackageName: String,
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

        val velocityEngine = VelocityUtils.createVelocityEngine()

        generatJpaRepos(
            velocityEngine, entities, generationDir, crudReposFileName, reposTemplate,
            repoPackageName
        )

    }


}