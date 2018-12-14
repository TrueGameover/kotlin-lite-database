package com.truegameover.lite.database.db

import mu.KotlinLogging
import org.hibernate.Session
import javax.persistence.RollbackException

open class BaseModel {

    companion object {

        protected val logger = KotlinLogging.logger(this::class.java.simpleName)
    }

    fun save(session: Session) {

        try {

            if (session.transaction.isActive) {

                session.transaction.rollback()
            }

            session.clear()
            session.beginTransaction()
            session.saveOrUpdate(this)
            session.flush()
            session.transaction.commit()

        } catch (e: RollbackException) {

            session.transaction.rollback()
            logger.warn(e.toString())
        }
    }

    fun saveTransactional(session: Session) {

        session.saveOrUpdate(this)
    }

    fun deleteTransactional(session: Session) {

        session.delete(this)
    }

    fun delete(session: Session) {

        try {

            if (session.transaction.isActive) {

                session.transaction.rollback()
            }

            session.beginTransaction()
            session.delete(this)
            session.flush()
            session.transaction.commit()

        } catch (e: RollbackException) {

            session.transaction.rollback()
            logger.warn(e.toString())
        }
    }
}