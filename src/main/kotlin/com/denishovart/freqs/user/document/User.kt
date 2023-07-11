package com.denishovart.freqs.user.document

import org.springframework.data.mongodb.core.mapping.Document
import java.util.*

@Document
data class User(val id: UUID, val name: String?, val picture: String?)