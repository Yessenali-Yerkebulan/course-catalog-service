package com.kotlinspring.controller

import com.kotlinspring.dto.CourseDTO
import com.kotlinspring.entity.Course
import com.kotlinspring.repository.CourseRepository
import com.kotlinspring.repository.InstructorRepository
import com.kotlinspring.service.CourseService
import com.kotlinspring.util.PostgresSQLContainerInitializer
import com.kotlinspring.util.courseEntityList
import com.kotlinspring.util.instructorEntity
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.web.util.UriComponentsBuilder

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureWebTestClient
class CourseControllerIntgTest : PostgresSQLContainerInitializer() {
    @Autowired
    lateinit var webTestClient: WebTestClient

    @Autowired
    lateinit var courseRepository : CourseRepository

    @Autowired
    lateinit var instructorRepository: InstructorRepository

    @BeforeEach
    fun setUp() {
        courseRepository.deleteAll()
        instructorRepository.deleteAll()
        val instructor = instructorEntity()
        instructorRepository.save(instructor)
        val courses = courseEntityList(instructor)
        courseRepository.saveAll(courses)
    }

    @Test
    fun addCourse() {
        val instructor = instructorRepository.findAll().first()

        val courseDto = CourseDTO(null, "Build Restful APIs using SpringBoot and Kotlin", "DEVELOPMENT", instructor.id)

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
    fun retrieveAllCoursesByName() {
        val uri = UriComponentsBuilder.fromUriString("/v1/courses")
            .queryParam("course_name", "SpringBoot")
            .toUriString()

        val courseDTOs = webTestClient
            .get()
            .uri(uri)
            .exchange()
            .expectStatus().isOk
            .expectBodyList(CourseDTO::class.java)
            .returnResult()
            .responseBody

        assertThat(courseDTOs).isNotEmpty
        assertThat(courseDTOs!!.size).isEqualTo(2)
    }

    @Test
    fun updateCourse() {
        val instructor = instructorRepository.findAll().first()

        val course =     Course(null,
            "Build Reactive Microservices using Spring WebFlux/SpringBoot", "Development", instructor
        )
        courseRepository.save(course)

        val updatedCourseDTO = CourseDTO(null,
            "Build Reactive Microservices using Spring WebFlux/SpringBoot updated", "Development", course.instructor!!.id
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
        val instructor = instructorRepository.findAll().first()

        val course = Course(null, "Build Reactive Microservices using Spring WebFlux/SpringBoot", "Development", instructor)
        courseRepository.save(course)

        webTestClient
            .delete()
            .uri("/v1/courses/{courseId}", course.id)
            .exchange()
            .expectStatus().isNoContent
    }
}