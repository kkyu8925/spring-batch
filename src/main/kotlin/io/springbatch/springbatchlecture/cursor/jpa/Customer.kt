package io.springbatch.springbatchlecture.cursor.jpa

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id

@Entity
class Customer(

    @Id
    @GeneratedValue
    val id: Long,

    val firstname: String,

    val lastname: String,

    val birthdate: String,
)
