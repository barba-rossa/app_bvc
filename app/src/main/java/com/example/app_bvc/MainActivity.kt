package com.example.app_bvc

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
fun DropdownMenuSample(selectedValue: String, onValueChange: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    val options = listOf("English", "French", "Spanish")

    Box {
        Text(selectedValue, modifier = Modifier
            .clickable { expanded = true }
            .padding(8.dp))

        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onValueChange(option)
                        expanded = false
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(navController: NavHostController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Name: John Doe", fontSize = 18.sp)
        Text("Age: 21", fontSize = 18.sp)
        Text("Date of Birth: Jan 1, 2003", fontSize = 18.sp)

        var language by remember { mutableStateOf("English") }
        Spacer(modifier = Modifier.height(16.dp))
        Text("Preferred Language:")
        DropdownMenuSample(language) { selected -> language = selected }

        Spacer(modifier = Modifier.height(32.dp))
        Button(onClick = { navController.navigate("main") }) {
            Text("Back to Main Menu")
        }
    }
}

@Composable
fun ApplicationsScreen(navController: NavHostController) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text("Applications", fontSize = 22.sp, color = Color(0xFF002F6C))
        Spacer(modifier = Modifier.height(16.dp))
        Text("Easter Event - Approved", fontSize = 18.sp)
        Text("Library Event - On hold", fontSize = 18.sp)
        Spacer(modifier = Modifier.height(32.dp))
        Button(onClick = { navController.navigate("main") }) {
            Text("Back to Main Menu")
        }
    }
}

@Composable
fun NotificationsScreen(navController: NavHostController) {
    val messages = listOf(
        "New Teams message from John",
        "Reminder: Assignment due tomorrow",
        "Event Update: Room change for Workshop",
        "New club formed: Hiking Enthusiasts",
        "Library Notice: Book due in 2 days"
    )

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Notifications", fontSize = 22.sp, color = Color(0xFF002F6C))
        Spacer(modifier = Modifier.height(16.dp))
        messages.forEach {
            Text(it, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(8.dp))
        }
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = { navController.navigate("main") }) {
            Text("Back to Main Menu")
        }
    }
}

@Composable
fun ProgressScreen(navController: NavHostController) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text("Progress Tracker", fontSize = 22.sp, color = Color(0xFF002F6C))
        Spacer(modifier = Modifier.height(16.dp))
        Text("Course: Mobile App Dev - 75% Completed", fontSize = 18.sp)
        Text("Course: Web Dev Basics - 50% Completed", fontSize = 18.sp)
        Text("Course: Database Design - 90% Completed", fontSize = 18.sp)
        Spacer(modifier = Modifier.height(32.dp))
        Button(onClick = { navController.navigate("main") }) {
            Text("Back to Main Menu")
        }
    }
}

@Composable
fun GroupsScreen(navController: NavHostController) {
    val groupNames = listOf("Chess Club", "Storytelling Group", "Hiking Enthusiasts", "Photography Club", "Coding Buddies")
    val joined = remember { mutableStateMapOf<String, Boolean>() }

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Groups", fontSize = 22.sp, color = Color(0xFF002F6C))
        Spacer(modifier = Modifier.height(16.dp))
        groupNames.forEach { group ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(group, modifier = Modifier.weight(1f), fontSize = 18.sp)
                val isMember = joined[group] == true
                Button(onClick = { joined[group] = !isMember }) {
                    Text(if (isMember) "Leave" else "Join")
                }
            }
        }
        Spacer(modifier = Modifier.height(32.dp))
        Button(onClick = { navController.navigate("main") }) {
            Text("Back to Main Menu")
        }
    }
}

@Composable
fun ScheduleScreen(navController: NavHostController) {
    val events = listOf(
        "Monday - Math Quiz",
        "Wednesday - Group Project Meeting",
        "Friday - Java Assignment Due"
    )
    Column(modifier = Modifier.padding(16.dp)) {
        Text("My Schedule", fontSize = 22.sp, color = Color(0xFF002F6C))
        Spacer(modifier = Modifier.height(16.dp))
        events.forEach {
            Text(it, fontSize = 18.sp)
            Spacer(modifier = Modifier.height(8.dp))
        }
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = { navController.navigate("main") }) {
            Text("Back to Main Menu")
        }
    }
}

@Composable
fun EventsScreen(navController: NavHostController) {
    val holidays = listOf(
        "April 18 - Easter Monday",
        "May 20 - Victoria Day",
        "July 1 - Canada Day"
    )
    Column(modifier = Modifier.padding(16.dp)) {
        Text("Upcoming Events", fontSize = 22.sp, color = Color(0xFF002F6C))
        Spacer(modifier = Modifier.height(16.dp))
        holidays.forEach {
            Text(it, fontSize = 18.sp)
            Spacer(modifier = Modifier.height(8.dp))
        }
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = { navController.navigate("main") }) {
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

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Need Help?", fontSize = 22.sp, color = Color(0xFF002F6C))
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = phone,
            onValueChange = { phone = it },
            label = { Text("Phone") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = message,
            onValueChange = { message = it },
            label = { Text("Message") },
            modifier = Modifier.fillMaxWidth().height(120.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = { navController.navigate("main") }) {
            Text("Back to Main Menu")
        }
    }
}