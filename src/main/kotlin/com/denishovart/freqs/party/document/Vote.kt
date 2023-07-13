package com.denishovart.freqs.party.document

import com.denishovart.freqs.base.document.BaseDocument
import com.denishovart.freqs.user.document.User

data class Vote(val issuer: User, var comment: String?): BaseDocument()