package com.denishovart.freqs.party.document

import com.denishovart.freqs.base.document.BaseDocument
import com.denishovart.freqs.user.document.User
import org.springframework.data.mongodb.core.mapping.Document

@Document
data class Party(var name: String, var creator: User): BaseDocument()