package com.denishovart.freqs.base.document

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field
import java.util.*

@Document
abstract class BaseDocument {
    @Id
    var id: UUID? = null

    @CreatedDate
    var createdAt: Date? = null
}