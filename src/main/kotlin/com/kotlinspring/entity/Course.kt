package com.kotlinspring.entity

import jakarta.persistence.Entity
import jakarta.persistence.Table

@Entity
@Table(name = "courses")
data class Course(
    val id: Int?,
    val name:String,
    val category: String,
)
