package com.eimsound.ktor.provider

interface EntityProvider<T : Any> {
    var entity: ((T) -> T)?
}

infix fun <T : Any> EntityProvider<T>.entity(block: (T) -> T) {
    entity = block
}


