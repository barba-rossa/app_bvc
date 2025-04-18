package com.example.app_bvc

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.app_bvc.ui.theme.App_BVCTheme
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

// Data classes for Firestore documents
data class Student(
    val name: String = "",
    val age: Int = 0,
    val dob: String = "",
    val preferredLanguage: String = "English"
)

data class Application(
    val name: String = "",
    val status: String = ""
)

data class Notification(
    val message: String = "",
    val timestamp: String = ""
)

data class Course(
    val name: String = "",
    val progress: Int = 0
)

data class Group(
    val name: String = "",
    val members: Int = 0
)

data class Event(
    val name: String = "",
    val date: String = ""
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            App_BVCTheme {
                val navController = rememberNavController()
                AppNavigation(navController)
            }
        }
    }
}

@Composable
fun AppNavigation(navController: NavHostController) {
    NavHost(navController, startDestination = "main") {
        composable("main") { MainScreen(navController) }
        composable("profile") { ProfileScreen(navController) }
        composable("applications") { ApplicationsScreen(navController) }
        composable("notifications") { NotificationsScreen(navController) }
        composable("progress") { ProgressScreen(navController) }
        composable("groups") { GroupsScreen(navController) }
        composable("schedule") { ScheduleScreen(navController) }
        composable("events") { EventsScreen(navController) }
        composable("help") { HelpScreen(navController) }
    }
}

@Composable
fun MainScreen(navController: NavHostController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Header with Logo and Title
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.School,
                contentDescription = "Logo",
                tint = Color(0xFF002F6C),
                modifier = Modifier.size(40.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "SuperApp",
                fontSize = 24.sp,
                fontStyle = FontStyle.Italic,
                color = Color(0xFF002F6C)
            )
        }

        Divider(color = Color(0xFF002F6C), thickness = 2.dp)

        Spacer(modifier = Modifier.height(16.dp))

        Column(modifier = Modifier.padding(start = 24.dp)) {
            MenuItem(Icons.Default.Person, "My Profile") { navController.navigate("profile") }
            MenuItem(Icons.Default.AccountCircle, "Applications") { navController.navigate("applications") }
            MenuItem(Icons.Default.Edit, "Notifications") { navController.navigate("notifications") }
            MenuItem(Icons.Default.BarChart, "Progress") { navController.navigate("progress") }
            MenuItem(Icons.Default.Group, "Groups") { navController.navigate("groups") }
            MenuItem(Icons.Default.DateRange, "My schedule") { navController.navigate("schedule") }
            MenuItem(Icons.Default.FastForward, "Upcoming Events") { navController.navigate("events") }
            MenuItem(Icons.Default.Help, "Need help?") { navController.navigate("help") }
        }

        Spacer(modifier = Modifier.weight(1f))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF002F6C))
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Copyright 2024. Bow Valley College",
                color = Color.White,
                fontSize = 12.sp
            )
        }
    }
}

@Composable
fun MenuItem(icon: ImageVector, title: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color(0xFF002F6C),
            modifier = Modifier.size(28.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(title, fontSize = 18.sp, color = Color.Black)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LanguageDropdownMenu(
    selectedLanguage: String,
    onLanguageSelected: (String) -> Unit
) {
    val languages = listOf("English", "Spanish", "French", "German", "Mandarin", "Japanese", "Arabic")
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = selectedLanguage,
            onValueChange = {},
            readOnly = true,
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth(),
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.exposedDropdownSize()
        ) {
            languages.forEach { language ->
                DropdownMenuItem(
                    text = { Text(text = language) },
                    onClick = {
                        onLanguageSelected(language)
                        expanded = false
                    },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(navController: NavHostController) {
    val db = Firebase.firestore
    var student by remember { mutableStateOf<Student?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var language by remember { mutableStateOf("English") }

    // Fetch student profile data from Firestore
    LaunchedEffect(key1 = true) {
        try {
            val document = db.collection("students").document("current_user").get().await()
            student = document.toObject(Student::class.java)
            student?.let {
                if (it.preferredLanguage.isNotBlank()) {
                    language = it.preferredLanguage
                }
            }
            isLoading = false
        } catch (e: Exception) {
            Log.e("ProfileScreen", "Error fetching student data", e)
            isLoading = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            "Student Profile",
            fontSize = 24.sp,
            color = Color(0xFF002F6C),
            modifier = Modifier.padding(bottom = 16.dp)
        )

        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                color = Color(0xFF002F6C)
            )
        } else if (student != null) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFF5F5F5)
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Name: ${student?.name ?: "Not available"}", fontSize = 18.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Age: ${student?.age ?: "Not available"}", fontSize = 18.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Date of Birth: ${student?.dob ?: "Not available"}", fontSize = 18.sp)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFF5F5F5)
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Preferred Language:", fontSize = 18.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    LanguageDropdownMenu(language) { selected ->
                        language = selected
                        // Update language preference in Firestore
                        db.collection("students").document("current_user")
                            .update("preferredLanguage", selected)
                            .addOnSuccessListener { Log.d("ProfileScreen", "Language updated") }
                            .addOnFailureListener { e -> Log.e("ProfileScreen", "Error updating language", e) }
                    }
                }
            }
        } else {
            Text("No profile data available. Please check your connection.")
        }

        Spacer(modifier = Modifier.height(32.dp))
        Button(
            onClick = { navController.navigate("main") },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF002F6C))
        ) {
            Text("Back to Main Menu")
        }
    }
}

@Composable
fun ApplicationsScreen(navController: NavHostController) {
    val db = Firebase.firestore
    var applications by remember { mutableStateOf<List<Application>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(key1 = true) {
        try {
            val querySnapshot = db.collection("applications").get().await()
            applications = querySnapshot.documents.mapNotNull {
                it.toObject(Application::class.java)
            }
            isLoading = false
        } catch (e: Exception) {
            Log.e("ApplicationsScreen", "Error fetching applications", e)
            isLoading = false
        }
    }

    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            "Applications",
            fontSize = 22.sp,
            color = Color(0xFF002F6C),
            modifier = Modifier.padding(bottom = 16.dp)
        )

        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                color = Color(0xFF002F6C)
            )
        } else if (applications.isNotEmpty()) {
            LazyColumn {
                items(applications) { application ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFF5F5F5)
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(application.name, fontSize = 18.sp)
                                Text(
                                    application.status,
                                    fontSize = 16.sp,
                                    color = when (application.status.lowercase()) {
                                        "approved" -> Color(0xFF4CAF50)
                                        "rejected" -> Color(0xFFF44336)
                                        else -> Color(0xFFFF9800)
                                    }
                                )
                            }
                            Icon(
                                imageVector = when (application.status.lowercase()) {
                                    "approved" -> Icons.Default.CheckCircle
                                    "rejected" -> Icons.Default.Cancel
                                    else -> Icons.Default.HourglassEmpty
                                },
                                contentDescription = null,
                                tint = when (application.status.lowercase()) {
                                    "approved" -> Color(0xFF4CAF50)
                                    "rejected" -> Color(0xFFF44336)
                                    else -> Color(0xFFFF9800)
                                }
                            )
                        }
                    }
                }
            }
        } else {
            Text("No applications found.")
        }

        Spacer(modifier = Modifier.height(32.dp))
        Button(
            onClick = { navController.navigate("main") },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF002F6C))
        ) {
            Text("Back to Main Menu")
        }
    }
}

@Composable
fun NotificationsScreen(navController: NavHostController) {
    val db = Firebase.firestore
    var notifications by remember { mutableStateOf<List<Notification>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(key1 = true) {
        try {
            val querySnapshot = db.collection("notifications").get().await()
            notifications = querySnapshot.documents.mapNotNull {
                it.toObject(Notification::class.java)
            }
            isLoading = false
        } catch (e: Exception) {
            Log.e("NotificationsScreen", "Error fetching notifications", e)
            isLoading = false
        }
    }

    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            "Notifications",
            fontSize = 22.sp,
            color = Color(0xFF002F6C),
            modifier = Modifier.padding(bottom = 16.dp)
        )

        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                color = Color(0xFF002F6C)
            )
        } else if (notifications.isNotEmpty()) {
            LazyColumn {
                items(notifications) { notification ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFF5F5F5)
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(notification.message, fontSize = 16.sp)
                                Text(
                                    notification.timestamp,
                                    fontSize = 12.sp,
                                    color = Color.Gray
                                )
                            }
                        }
                    }
                }
            }
        } else {
            Text("No notifications found.")
        }

        Spacer(modifier = Modifier.height(32.dp))
        Button(
            onClick = { navController.navigate("main") },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF002F6C))
        ) {
            Text("Back to Main Menu")
        }
    }
}

@Composable
fun ProgressScreen(navController: NavHostController) {
    val db = Firebase.firestore
    var courses by remember { mutableStateOf<List<Course>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(key1 = true) {
        try {
            val querySnapshot = db.collection("courses").get().await()
            courses = querySnapshot.documents.mapNotNull {
                it.toObject(Course::class.java)
            }
            isLoading = false
        } catch (e: Exception) {
            Log.e("ProgressScreen", "Error fetching courses", e)
            isLoading = false
        }
    }

    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            "Progress Tracker",
            fontSize = 22.sp,
            color = Color(0xFF002F6C),
            modifier = Modifier.padding(bottom = 16.dp)
        )

        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                color = Color(0xFF002F6C)
            )
        } else if (courses.isNotEmpty()) {
            LazyColumn {
                items(courses) { course ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFF5F5F5)
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text("Course: ${course.name}", fontSize = 18.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            LinearProgressIndicator(
                                progress = { course.progress / 100f },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(12.dp),
                                color = Color(0xFF002F6C),
                                trackColor = Color.LightGray
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                "${course.progress}% Completed",
                                fontSize = 14.sp,
                                color = Color.Gray
                            )
                        }
                    }
                }
            }
        } else {
            Text("No courses found.")
        }

        Spacer(modifier = Modifier.height(32.dp))
        Button(
            onClick = { navController.navigate("main") },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF002F6C))
        ) {
            Text("Back to Main Menu")
        }
    }
}

@Composable
fun GroupsScreen(navController: NavHostController) {
    val db = Firebase.firestore
    var groups by remember { mutableStateOf<List<Group>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    val joined = remember { mutableStateMapOf<String, Boolean>() }

    LaunchedEffect(key1 = true) {
        try {
            val querySnapshot = db.collection("groups").get().await()
            groups = querySnapshot.documents.mapNotNull {
                it.toObject(Group::class.java)
            }
            isLoading = false
        } catch (e: Exception) {
            Log.e("GroupsScreen", "Error fetching groups", e)
            isLoading = false
        }
    }

    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            "Groups",
            fontSize = 22.sp,
            color = Color(0xFF002F6C),
            modifier = Modifier.padding(bottom = 16.dp)
        )

        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                color = Color(0xFF002F6C)
            )
        } else if (groups.isNotEmpty()) {
            LazyColumn {
                items(groups) { group ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFF5F5F5)
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(group.name, fontSize = 18.sp)
                                Text("${group.members} members", fontSize = 14.sp, color = Color.Gray)
                            }
                            val isMember = joined[group.name] == true
                            Button(
                                onClick = { joined[group.name] = !isMember },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isMember) Color.Red else Color(0xFF002F6C)
                                )
                            ) {
                                Text(if (isMember) "Leave" else "Join")
                            }
                        }
                    }
                }
            }
        } else {
            Text("No groups found.")
        }

        Spacer(modifier = Modifier.height(32.dp))
        Button(
            onClick = { navController.navigate("main") },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF002F6C))
        ) {
            Text("Back to Main Menu")
        }
    }
}

@Composable
fun ScheduleScreen(navController: NavHostController) {
    // Since schedule is likely user-specific, let's keep it as before for now
    val events = listOf(
        "Monday - Math Quiz",
        "Wednesday - Group Project Meeting",
        "Friday - Java Assignment Due"
    )

    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            "My Schedule",
            fontSize = 22.sp,
            color = Color(0xFF002F6C),
            modifier = Modifier.padding(bottom = 16.dp)
        )

        LazyColumn {
            items(events) { event ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFF5F5F5)
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Event,
                            contentDescription = null,
                            tint = Color(0xFF002F6C),
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(event, fontSize = 18.sp)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
        Button(
            onClick = { navController.navigate("main") },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF002F6C))
        ) {
            Text("Back to Main Menu")
        }
    }
}

@Composable
fun EventsScreen(navController: NavHostController) {
    val db = Firebase.firestore
    var events by remember { mutableStateOf<List<Event>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(key1 = true) {
        try {
            val querySnapshot = db.collection("events").get().await()
            events = querySnapshot.documents.mapNotNull {
                it.toObject(Event::class.java)
            }
            isLoading = false
        } catch (e: Exception) {
            Log.e("EventsScreen", "Error fetching events", e)
            isLoading = false
        }
    }

    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            "Upcoming Events",
            fontSize = 22.sp,
            color = Color(0xFF002F6C),
            modifier = Modifier.padding(bottom = 16.dp)
        )

        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                color = Color(0xFF002F6C)
            )
        } else if (events.isNotEmpty()) {
            LazyColumn {
                items(events) { event ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFF5F5F5)
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Event,
                                contentDescription = null,
                                tint = Color(0xFF002F6C),
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Column {
                                Text(event.name, fontSize = 18.sp)
                                Text(event.date, fontSize = 14.sp, color = Color.Gray)
                            }
                        }
                    }
                }
            }
        } else {
            Text("No events found.")
        }

        Spacer(modifier = Modifier.height(32.dp))
        Button(
            onClick = { navController.navigate("main") },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF002F6C))
        ) {
            Text("Back to Main Menu")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HelpScreen(navController: NavHostController) {
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }
    var submitted by remember { mutableStateOf(false) }

    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            "Need Help?",
            fontSize = 22.sp,
            color = Color(0xFF002F6C),
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFFF5F5F5)
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF002F6C),
                        focusedLabelColor = Color(0xFF002F6C)
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Phone") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF002F6C),
                        focusedLabelColor = Color(0xFF002F6C)
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = message,
                    onValueChange = { message = it },
                    label = { Text("Message") },
                    modifier = Modifier.fillMaxWidth().height(120.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF002F6C),
                        focusedLabelColor = Color(0xFF002F6C)
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(
                        onClick = { navController.navigate("main") },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
                    ) {
                        Text("Cancel")
                    }

                    Button(
                        onClick = {
                            // Here you would submit to Firebase
                            submitted = true
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF002F6C))
                    ) {
                        Text("Submit")
                    }
                }

                if (submitted) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        "Your request has been submitted! We'll get back to you soon.",
                        color = Color(0xFF4CAF50)
                    )
                }
            }
        }
    }
}