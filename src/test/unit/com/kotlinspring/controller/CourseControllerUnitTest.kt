package com.kotlinspring.controller

import com.kotlinspring.dto.CourseDTO
import com.kotlinspring.entity.Course
import com.kotlinspring.service.CourseService
import com.kotlinspring.util.courseDTO
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.test.web.reactive.server.WebTestClient

@WebMvcTest(controllers = [CourseController::class])
@AutoConfigureWebTestClient
class CourseControllerUnitTest {
    @Autowired
    lateinit var webTestClient: WebTestClient

    @MockkBean
    lateinit var courseServiceMockk : CourseService

    @Test
    fun addCourse() {
        val courseDto = CourseDTO(null, "Build Restful APIs using SpringBoot and Kotlin", "Test user")

        every {
            courseServiceMockk.addCourse(any())
        } returns courseDTO(id = 1)

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
        every {
            courseServiceMockk.retrieveAllCourses()
        }.returnsMany(
            listOf(courseDTO(id =1),
                courseDTO(id = 2,
                    name = "Build Restful APIs using SpringBoot and Kotlin"))
        )

        val courseDTOs = webTestClient
            .get()
            .uri("/v1/courses")
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
        every {
            courseServiceMockk.updateCourse(any(), any())
        } returns courseDTO(id=100, name="Build Reactive Microservices using Spring WebFlux/SpringBoot updated")

        val updatedCourseDTO = CourseDTO(null,
            "Build Reactive Microservices using Spring WebFlux/SpringBoot updated", "Development"
        )

        val updatedCourse = webTestClient
            .put()
            .uri("/v1/courses/{courseId}", 100)
            .bodyValue(updatedCourseDTO)
            .exchange()
            .expectStatus().isOk
            .expectBody(CourseDTO::class.java)
            .returnResult()
            .responseBody

        assertThat(updatedCourse!!.name).isEqualTo(updatedCourseDTO.name)
    }
}