package com.eimsound.ktor.route

import com.eimsound.ktor.validator.ValidationBuilder
import com.eimsound.ktor.provider.CallProvider
import com.eimsound.ktor.provider.EntityProvider
import com.eimsound.ktor.provider.ValidatorProvider
import com.eimsound.jimmer.sqlClient
import com.eimsound.ktor.provider.validate
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.utils.io.*

@KtorDsl
inline fun <reified TEntity : Any> Route.create(
    path: String = "",
    crossinline block: suspend CreateProvider<TEntity>.(TEntity) -> Unit,
) = post(path) {
    val body = call.receive<TEntity>()
    val provider = CreateProvider.Impl<TEntity>(call).apply { block(body) }.apply{
        validator?.validate(body)
    }
    val entity = provider.entity?.invoke(body) ?: body

    val result = sqlClient.insert(entity)
    call.respond(result.modifiedEntity)
}

interface CreateProvider<T : Any> : CallProvider, EntityProvider<T>, ValidatorProvider<T> {
    class Impl<T : Any>(
        override val call: RoutingCall,
    ) : CreateProvider<T> {
        override var entity: ((T) -> T)? = null
        override var validator: (ValidationBuilder.(T) -> Unit)? = null
    }
}
