package io.springbatch.springbatchlecture.page.jpa

import jakarta.persistence.*

@Entity
class Address(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long,

    val location: String,

    @OneToOne
    @JoinColumn(name = "customer_id")
    val customer: Customer
)
