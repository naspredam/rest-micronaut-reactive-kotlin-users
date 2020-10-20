package com.service.micronaut.users

import javax.persistence.*

@Entity
@Table(name = "users")
data class UserData(
        @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
        var id: Long? = null,

        @Column(name = "first_name")
        val firstName: String? = null,

        @Column(name = "last_name")
        val lastName: String? = null,

        @Column(name = "phone")
        val phone: String? = null
)