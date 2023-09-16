package io.springbatch.springbatchlecture.page.jpa

import jakarta.persistence.*

@Entity
class Customer(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long,

    val username: String,

    val age: Int,

    @OneToOne(mappedBy = "customer")
    val address: Address
)
