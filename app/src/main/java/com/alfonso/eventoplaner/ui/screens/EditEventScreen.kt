package com.alfonso.eventoplaner.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.alfonso.eventoplaner.ui.viewmodel.EventState
import com.alfonso.eventoplaner.ui.viewmodel.EventViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditEventScreen(
    navController: NavController,
    eventViewModel: EventViewModel,
    eventId: String,
    currentTitle: String,
    currentDate: String,
    currentDescription: String
) {
    var title by remember { mutableStateOf(currentTitle) }
    var date by remember { mutableStateOf(currentDate) }
    var description by remember { mutableStateOf(currentDescription) }
    val eventState by eventViewModel.eventState.collectAsState()

    LaunchedEffect(eventState) {
        when (eventState) {
            is EventState.Success -> {
                navController.popBackStack()
            }
            else -> {}
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Editar Evento") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Título *") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = date,
                onValueChange = { date = it },
                label = { Text("Fecha") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Descripción") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                maxLines = 5
            )

            if (eventState is EventState.Loading) {
                CircularProgressIndicator()
            } else {
                Button(
                    onClick = { eventViewModel.updateEvent(eventId, title, date, description) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Actualizar Evento")
                }
            }

            if (eventState is EventState.Error) {
                Text(
                    text = (eventState as EventState.Error).message,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}