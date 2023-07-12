package com.denishovart.freqs.base.document

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import java.util.*


abstract class BaseDocument {
    @Id
    var id: UUID? = null

    @CreatedDate
    var createdAt: Date? = null
}