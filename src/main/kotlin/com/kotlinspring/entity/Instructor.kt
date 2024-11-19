package com.kotlinspring.entity

import jakarta.persistence.*

@Entity
@Table(name = "INSTRUCTORS")
data class Instructor(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    val id: Int?,
    val name: String,
    @OneToMany(
        mappedBy = "instructor",
        cascade = [(CascadeType.ALL)],
        orphanRemoval = true
    )
    var courses: MutableList<Course> = mutableListOf(),
)
