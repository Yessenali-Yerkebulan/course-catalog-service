package com.kotlinspring.controller

import com.kotlinspring.dto.CourseDTO
import com.kotlinspring.entity.Course
import com.kotlinspring.repository.CourseRepository
import com.kotlinspring.service.CourseService
import com.kotlinspring.util.courseEntityList
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.reactive.server.WebTestClient

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureWebTestClient
class CourseControllerIntgTest {
    @Autowired
    lateinit var webTestClient: WebTestClient

    @Autowired
    lateinit var courseRepository : CourseRepository

    @BeforeEach
    fun setUp() {
        courseRepository.deleteAll()
        val courses = courseEntityList()
        courseRepository.saveAll(courses)
    }

    @Test
    fun addCourse() {
        val courseDto = CourseDTO(null, "Build Restful APIs using SpringBoot and Kotlin", "Test user")

        val savedCourseDTO = webTestClient
            .post()
            .uri("/v1/courses")
            .bodyValue(courseDto)
            .exchange()
            .expectStatus().isCreated
            .expectBody(CourseDTO::class.java)
            .returnResult()
            .responseBody

        assertThat(savedCourseDTO!!.id).isNotNull
    }

    @Test
    fun retrieveAllCourses() {
        val courseDTOs = webTestClient
            .get()
            .uri("/v1/courses")
            .exchange()
            .expectStatus().isOk
            .expectBodyList(CourseDTO::class.java)
            .returnResult()
            .responseBody

        assertThat(courseDTOs).isNotEmpty
        assertThat(courseDTOs!!.size).isEqualTo(3)
    }

    @Test
    fun updateCourse() {
        val course =     Course(null,
            "Build Reactive Microservices using Spring WebFlux/SpringBoot", "Development"
        )
        courseRepository.save(course)

        val updatedCourseDTO = CourseDTO(null,
            "Build Reactive Microservices using Spring WebFlux/SpringBoot updated", "Development"
        )

        val updatedCourse = webTestClient
            .put()
            .uri("/v1/courses/{courseId}", course.id)
            .bodyValue(updatedCourseDTO)
            .exchange()
            .expectStatus().isOk
            .expectBody(CourseDTO::class.java)
            .returnResult()
            .responseBody

        assertThat(updatedCourse!!.name).isEqualTo(updatedCourseDTO.name)
    }

    @Test
    fun deleteCourse() {
        val course = Course(null, "Build Reactive Microservices using Spring WebFlux/SpringBoot", "Development")
        courseRepository.save(course)

        webTestClient
            .delete()
            .uri("/v1/courses/{courseId}", course.id)
            .exchange()
            .expectStatus().isNoContent
    }
}