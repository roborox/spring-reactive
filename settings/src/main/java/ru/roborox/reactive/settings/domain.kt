package ru.roborox.reactive.settings

import org.springframework.data.annotation.Id
import org.springframework.data.annotation.Version
import org.springframework.data.mongodb.core.mapping.Document

@Document
data class Setting(
    @Id
    val id: String,
    val value: String,
    @Version
    val version: Long? = null
)