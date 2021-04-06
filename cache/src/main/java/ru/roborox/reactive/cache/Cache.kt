package ru.roborox.reactive.cache

import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.annotation.Version
import java.util.*

data class Cache(
    @Id
    val id: String,
    val data: Any?,
    @LastModifiedDate
    val updateDate: Date? = null,
    @Version
    val version: Long? = null
) {
    fun canBeUsed(maxAge: Long): Boolean {
        return updateDate!!.time >= System.currentTimeMillis() - maxAge
    }
}