package com.truegameover.lite.database

import com.truegameover.lite.database.db.BaseModel
import org.hibernate.SessionFactory
import org.hibernate.boot.Metadata
import org.hibernate.boot.MetadataSources
import org.hibernate.boot.registry.StandardServiceRegistry
import org.hibernate.boot.registry.StandardServiceRegistryBuilder
import java.io.File
import kotlin.reflect.KClass

object HibernateSessionFactory {

    private var sessionFactory: SessionFactory? = null
    private val registeredEntities = mutableListOf<KClass<BaseModel>>()

    /**
     * Must be called before access hibernate
     */
    fun buildSessionFactory(): Boolean {

        val registry =
            StandardServiceRegistryBuilder().configure(File("hibernate.cfg.xml")) // configures settings from hibernate.cfg.xml
                .build()

        try {

            this.sessionFactory = this.buildMetadata(registry)
                .buildSessionFactory()

            return true

        } catch (e: Exception) {

            StandardServiceRegistryBuilder.destroy(registry)

            throw ExceptionInInitializerError(e)
        }
    }

    fun buildMetadata(registry: StandardServiceRegistry): Metadata {

        val metadataSources = MetadataSources(registry)

        for (registeredEntity in this.registeredEntities) {

            metadataSources.addAnnotatedClass(registeredEntity.java)
        }

        return metadataSources.buildMetadata()
    }

    fun shutdown() {

        sessionFactory?.close()
    }

    fun <T : BaseModel> registerEntityClass(entity: KClass<T>) {

        this.registeredEntities.clear()
        this.registeredEntities.add(entity as KClass<BaseModel>)
    }

    @Synchronized
    fun getSessionFactory(): SessionFactory {

        if (this.sessionFactory == null) {

            this.buildSessionFactory()
        }

        return this.sessionFactory as SessionFactory
    }
}