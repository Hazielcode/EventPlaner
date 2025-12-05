package com.alfonso.eventoplaner.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.alfonso.eventoplaner.data.model.Event
import com.alfonso.eventoplaner.navigation.Screen
import com.alfonso.eventoplaner.ui.viewmodel.AuthViewModel
import com.alfonso.eventoplaner.ui.viewmodel.EventState
import com.alfonso.eventoplaner.ui.viewmodel.EventViewModel
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventListScreen(
    navController: NavController,
    authViewModel: AuthViewModel,
    eventViewModel: EventViewModel
) {
    val events by eventViewModel.events.collectAsState()
    val eventState by eventViewModel.eventState.collectAsState()
    var showDeleteDialog by remember { mutableStateOf(false) }
    var eventToDelete by remember { mutableStateOf<Event?>(null) }

    LaunchedEffect(eventState) {
        when (eventState) {
            is EventState.Success -> {
                eventViewModel.resetState()
            }
            else -> {}
        }
    }

    if (showDeleteDialog && eventToDelete != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Eliminar Evento") },
            text = { Text("¿Estás seguro de eliminar '${eventToDelete?.title}'?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        eventToDelete?.let { eventViewModel.deleteEvent(it.id) }
                        showDeleteDialog = false
                        eventToDelete = null
                    }
                ) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mis Eventos") },
                actions = {
                    IconButton(onClick = {
                        authViewModel.logout()
                        navController.navigate(Screen.Login.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ExitToApp, "Cerrar Sesión")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate(Screen.CreateEvent.route) }
            ) {
                Icon(Icons.Default.Add, "Crear Evento")
            }
        }
    ) { padding ->
        if (events.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("No tienes eventos. ¡Crea uno!")
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(events) { event ->
                    EventCard(
                        event = event,
                        onEdit = {
                            val encodedTitle = URLEncoder.encode(event.title, StandardCharsets.UTF_8.toString())
                            val encodedDate = URLEncoder.encode(event.date, StandardCharsets.UTF_8.toString())
                            val encodedDesc = URLEncoder.encode(event.description, StandardCharsets.UTF_8.toString())
                            navController.navigate("${Screen.EditEvent.route}/${event.id}/$encodedTitle/$encodedDate/$encodedDesc")
                        },
                        onDelete = {
                            eventToDelete = event
                            showDeleteDialog = true
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun EventCard(
    event: Event,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = event.title,
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = event.date,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary
            )
            if (event.description.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = event.description,
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(onClick = onEdit) {
                    Icon(Icons.Default.Edit, "Editar", tint = MaterialTheme.colorScheme.primary)
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, "Eliminar", tint = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}
