package com.denishovart.freqs.base.repository

import com.denishovart.freqs.base.document.BaseDocument
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.data.repository.NoRepositoryBean
import java.util.*

@NoRepositoryBean
interface BaseMongoRepository<T : BaseDocument?> : ReactiveMongoRepository<T, UUID?>

