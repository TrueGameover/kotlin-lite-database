package com.truegameover.lite.database.db

import com.truegameover.lite.database.HibernateSessionFactory
import mu.KotlinLogging
import org.hibernate.Session
import org.hibernate.Transaction
import javax.persistence.EntityManager
import javax.persistence.RollbackException

open class DatabaseManager {

    private val logger = KotlinLogging.logger(this::class.java.simpleName)

    fun getEntityManager(): EntityManager {

        return this.getSession()
    }

    fun getSession(): Session {

        return HibernateSessionFactory.getSessionFactory().openSession() as Session
    }

    fun beginTransaction(session: Session): Transaction {

        if (session.transaction.isActive) {

            session.transaction.rollback()
        }

        return session.beginTransaction()
    }

    @Throws(RollbackException::class)
    fun endTransaction(session: Session) {

        session.flush()
        session.transaction.commit()
    }

    fun clearTransaction(session: Session) {

        session.transaction.rollback()
    }

    fun rollbackTransaction(session: Session) {

        session.transaction.rollback()
    }
}