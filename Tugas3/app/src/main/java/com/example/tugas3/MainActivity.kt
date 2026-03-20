package com.example.tugas3

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tugas3.ui.theme.Tugas3Theme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

// 1. Model State UI
data class ProfileUiState(
    val name: String = "Maxavier G M",
    val bio: String = "Informatics Engineering Students at the ITERA",
    val email: String = "maxavier.123140191@itera.ac.id",
    val phone: String = "+62 852 6044 4021",
    val location: String = "Lampung, Indonesia",
    val isDarkMode: Boolean = false,
    val isEditing: Boolean = false
)

// 2. ViewModel dengan StateFlow
class ProfileViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    fun toggleEditing() {
        _uiState.update { it.copy(isEditing = !it.isEditing) }
    }

    fun updateProfile(newName: String, newBio: String) {
        _uiState.update { 
            it.copy(name = newName, bio = newBio, isEditing = false) 
        }
    }

    fun toggleDarkMode(enabled: Boolean) {
        _uiState.update { it.copy(isDarkMode = enabled) }
    }
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: ProfileViewModel = viewModel()
            val uiState by viewModel.uiState.collectAsState()

            Tugas3Theme(darkTheme = uiState.isDarkMode) {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    ProfileScreen(
                        uiState = uiState,
                        onDarkModeToggle = viewModel::toggleDarkMode,
                        onEditClick = viewModel::toggleEditing,
                        onSaveClick = viewModel::updateProfile,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun ProfileScreen(
    uiState: ProfileUiState,
    onDarkModeToggle: (Boolean) -> Unit,
    onEditClick: () -> Unit,
    onSaveClick: (String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    var editedName by remember(uiState.name) { mutableStateOf(uiState.name) }
    var editedBio by remember(uiState.bio) { mutableStateOf(uiState.bio) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Dark Mode Toggle Switch
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End
        ) {
            Text("Dark Mode", style = MaterialTheme.typography.labelMedium)
            Spacer(modifier = Modifier.width(8.dp))
            Switch(
                checked = uiState.isDarkMode,
                onCheckedChange = onDarkModeToggle
            )
        }

        ProfileHeader(
            name = uiState.name,
            imageVector = Icons.Default.Person
        )
        
        Spacer(modifier = Modifier.height(16.dp))

        if (uiState.isEditing) {
            // Form Edit (State Hoisting)
            OutlinedTextField(
                value = editedName,
                onValueChange = { editedName = it },
                label = { Text("Name") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = editedBio,
                onValueChange = { editedBio = it },
                label = { Text("Bio") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { onSaveClick(editedName, editedBio) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Check, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Save Changes")
            }
        } else {
            Text(
                text = uiState.bio,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 24.dp)
            )
            
            Spacer(modifier = Modifier.height(24.dp))

            ProfileCard {
                Column(modifier = Modifier.padding(16.dp)) {
                    InfoItem(icon = Icons.Default.Email, label = "Email", value = uiState.email)
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), thickness = 0.5.dp)
                    InfoItem(icon = Icons.Default.Phone, label = "Phone", value = uiState.phone)
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), thickness = 0.5.dp)
                    InfoItem(icon = Icons.Default.LocationOn, label = "Location", value = uiState.location)
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onEditClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Edit, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Edit Profile")
            }
        }
    }
}

@Composable
fun ProfileHeader(name: String, imageVector: ImageVector, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = imageVector,
                contentDescription = "Profile Picture",
                modifier = Modifier.size(80.dp),
                tint = MaterialTheme.colorScheme.primary
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = name,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun InfoItem(icon: ImageVector, label: String, value: String, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.outline
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun ProfileCard(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        content()
    }
}

@Preview(showBackground = true)
@Composable
fun ProfilePreview() {
    Tugas3Theme {
        ProfileScreen(
            uiState = ProfileUiState(),
            onDarkModeToggle = {},
            onEditClick = {},
            onSaveClick = { _, _ -> }
        )
    }
}
