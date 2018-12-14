package com.truegameover.lite.database.db

import org.hibernate.Session
import javax.persistence.NoResultException
import javax.persistence.criteria.*
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.full.memberProperties

class Query<T : BaseModel> private constructor(private val type: KClass<T>, private val session: Session) {

    private val builder: CriteriaBuilder = this.session.criteriaBuilder
    private var query: CriteriaQuery<T> = this.builder.createQuery<T>(type.java)
    private val root: Root<T> = query.from<T>(type.java)
    private val wherePredicates = mutableListOf<Predicate>()
    private val orders = mutableListOf<Order>()

    companion object {

        fun <T : BaseModel> createQuery(type: KClass<T>, session: Session): Query<T> {

            return Query(type, session)
        }
    }

    init {

        query.select(root)
    }

    private fun getAttributeType(name: String): KType? {

        return type.memberProperties.firstOrNull { it.name == name }
                ?.returnType
    }

    fun <X> eq(attributeName: String, value: X): Query<T> {

        this.wherePredicates.add(builder.equal(this.root.get<X>(attributeName), value))

        return this
    }

    fun <X : Comparable<X>> le(attributeName: String, value: X): Query<T> {

        this.wherePredicates.add(builder.lessThanOrEqualTo<X>(this.root.get<X>(attributeName), value))

        return this
    }

    fun findAll(count: Int = Int.MAX_VALUE): List<T> {

        this.query = query.where(*this.wherePredicates.toTypedArray())
                .orderBy(this.orders)

        return this.session.createQuery(query)
                //                .setFirstResult(0)
                .setMaxResults(count)
                .resultList
    }

    fun <X> desc(field: String): Query<T> {

        this.orders.add(builder.desc(this.root.get<X>(field)))

        return this
    }

    fun <X> asc(field: String): Query<T> {

        this.orders.add(builder.asc(this.root.get<X>(field)))

        return this
    }

    fun find(): T? {

        return try {

            this.query = query.where(*this.wherePredicates.toTypedArray())
                    .orderBy(this.orders)

            this.session.createQuery(query)
                    .setMaxResults(1)
                    .singleResult

        } catch (e: NoResultException) {

            null
        }
    }
}