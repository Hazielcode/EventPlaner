package com.alfonso.eventoplaner.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alfonso.eventoplaner.data.model.Event
import com.alfonso.eventoplaner.data.repository.EventRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class EventViewModel : ViewModel() {
    private val repository = EventRepository()

    private val _events = MutableStateFlow<List<Event>>(emptyList())
    val events: StateFlow<List<Event>> = _events

    private val _eventState = MutableStateFlow<EventState>(EventState.Idle)
    val eventState: StateFlow<EventState> = _eventState

    init {
        loadEvents()
    }

    private fun loadEvents() {
        viewModelScope.launch {
            repository.getEvents().collect { eventList ->
                _events.value = eventList
            }
        }
    }

    fun createEvent(title: String, date: String, description: String) {
        if (title.isBlank()) {
            _eventState.value = EventState.Error("El título es obligatorio")
            return
        }

        viewModelScope.launch {
            _eventState.value = EventState.Loading
            val result = repository.createEvent(title, date, description)
            _eventState.value = if (result.isSuccess) {
                EventState.Success("Evento creado exitosamente")
            } else {
                EventState.Error(result.exceptionOrNull()?.message ?: "Error al crear evento")
            }
        }
    }

    fun updateEvent(eventId: String, title: String, date: String, description: String) {
        if (title.isBlank()) {
            _eventState.value = EventState.Error("El título es obligatorio")
            return
        }

        viewModelScope.launch {
            _eventState.value = EventState.Loading
            val result = repository.updateEvent(eventId, title, date, description)
            _eventState.value = if (result.isSuccess) {
                EventState.Success("Evento actualizado exitosamente")
            } else {
                EventState.Error(result.exceptionOrNull()?.message ?: "Error al actualizar evento")
            }
        }
    }

    fun deleteEvent(eventId: String) {
        viewModelScope.launch {
            _eventState.value = EventState.Loading
            val result = repository.deleteEvent(eventId)
            _eventState.value = if (result.isSuccess) {
                EventState.Success("Evento eliminado exitosamente")
            } else {
                EventState.Error(result.exceptionOrNull()?.message ?: "Error al eliminar evento")
            }
        }
    }

    fun resetState() {
        _eventState.value = EventState.Idle
    }
}

sealed class EventState {
    object Idle : EventState()
    object Loading : EventState()
    data class Success(val message: String) : EventState()
    data class Error(val message: String) : EventState()
}