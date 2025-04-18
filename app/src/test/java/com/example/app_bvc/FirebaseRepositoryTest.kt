package com.example.app_bvc

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnitRunner
import com.google.android.gms.tasks.Tasks

@RunWith(MockitoJUnitRunner::class)
class FirebaseRepositoryTest {

    @Mock
    private lateinit var mockFirestore: FirebaseFirestore

    @Mock
    private lateinit var mockCollection: CollectionReference

    @Mock
    private lateinit var mockDocument: DocumentReference

    @Mock
    private lateinit var mockSnapshot: DocumentSnapshot

    private lateinit var repository: FirebaseRepository

    @Before
    fun setup() {
        repository = FirebaseRepository(mockFirestore)
        `when`(mockFirestore.collection("students")).thenReturn(mockCollection)
        `when`(mockCollection.document(anyString())).thenReturn(mockDocument)
    }

    @Test
    fun `getStudentProfile should return student when document exists`() = runBlocking {
        `when`(mockDocument.get()).thenReturn(Tasks.forResult(mockSnapshot))
        `when`(mockSnapshot.exists()).thenReturn(true)
        `when`(mockSnapshot.getString("name")).thenReturn("John Doe")
        `when`(mockSnapshot.getString("email")).thenReturn("john@example.com")

        val result = repository.getStudentProfile("student123")

        assertNotNull(result)
        assertEquals("John Doe", result?.name)
        assertEquals("john@example.com", result?.email)
    }

    @Test
    fun `getStudentProfile should return null when document does not exist`() = runBlocking {
        `when`(mockDocument.get()).thenReturn(Tasks.forResult(mockSnapshot))
        `when`(mockSnapshot.exists()).thenReturn(false)

        val result = repository.getStudentProfile("unknown_user")

        assertNull(result)
    }
}
