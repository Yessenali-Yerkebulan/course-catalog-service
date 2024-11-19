package com.kotlinspring.repository

import com.kotlinspring.util.courseEntityList
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.ActiveProfiles
import java.util.stream.Stream
import kotlin.test.Test

@DataJpaTest
@ActiveProfiles("test")
class CourseRepositoryIntgTest {
    @Autowired
    lateinit var courseRepository: CourseRepository

    @BeforeEach
    fun setUp() {
        courseRepository.deleteAll()
        val courses = courseEntityList()
        courseRepository.saveAll(courses)
    }

    @Test
    fun findByNameContaining() {
        val courses = courseRepository.findCoursesByName("SpringBoot")

        assertThat(courses).isNotEmpty
        assertThat(courses.size).isEqualTo(2)
    }

    @ParameterizedTest
    @MethodSource("courseAndSize")
    fun findCoursesName_approach2(name: String, expectedSize: Int) {
        val courses = courseRepository.findByNameContaining(name)
        println("courses: $courses")
        assertThat(courses).hasSize(expectedSize)
    }


    companion object {
        @JvmStatic
        fun courseAndSize(): Stream<Arguments> {
            return Stream.of(Arguments.arguments("SpringBoot", 2),
                Arguments.arguments("Wiremock", 1))
        }
    }
}