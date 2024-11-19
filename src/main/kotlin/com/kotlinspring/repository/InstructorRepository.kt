package com.kotlinspring.repository

import com.kotlinspring.entity.Instructor
import org.springframework.data.jpa.repository.JpaRepository

interface InstructorRepository : JpaRepository<Instructor, Int> {
}