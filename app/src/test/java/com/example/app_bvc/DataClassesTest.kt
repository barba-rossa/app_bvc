package com.example.app_bvc

import org.junit.Test
import com.google.common.truth.Truth.assertThat

class DataClassesTest {

    @Test
    fun `student data class should have correct default values`() {
        val student = Student()
        assertThat(student.name).isEqualTo("")
        assertThat(student.age).isEqualTo(0)
        assertThat(student.dob).isEqualTo("")
        assertThat(student.preferredLanguage).isEqualTo("English")
    }

    @Test
    fun `application data class should have correct default values`() {
        val application = Application()
        assertThat(application.name).isEqualTo("")
        assertThat(application.status).isEqualTo("")
    }

    @Test
    fun `notification data class should have correct default values`() {
        val notification = Notification()
        assertThat(notification.message).isEqualTo("")
        assertThat(notification.timestamp).isEqualTo("")
    }

    @Test
    fun `course data class should have correct default values`() {
        val course = Course()
        assertThat(course.name).isEqualTo("")
        assertThat(course.progress).isEqualTo(0)
    }

    @Test
    fun `group data class should have correct default values`() {
        val group = Group()
        assertThat(group.name).isEqualTo("")
        assertThat(group.members).isEqualTo(0)
    }

    @Test
    fun `event data class should have correct default values`() {
        val event = Event()
        assertThat(event.name).isEqualTo("")
        assertThat(event.date).isEqualTo("")
    }
}