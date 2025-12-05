package com.alfonso.eventoplaner.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.alfonso.eventoplaner.ui.screens.*
import com.alfonso.eventoplaner.ui.viewmodel.AuthState
import com.alfonso.eventoplaner.ui.viewmodel.AuthViewModel
import com.alfonso.eventoplaner.ui.viewmodel.EventViewModel

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = viewModel()
    val eventViewModel: EventViewModel = viewModel()
    val authState by authViewModel.authState.collectAsState()

    val startDestination = if (authState is AuthState.Authenticated) {
        Screen.EventList.route
    } else {
        Screen.Login.route
    }

    NavHost(navController = navController, startDestination = startDestination) {
        composable(Screen.Login.route) {
            LoginScreen(
                navController = navController,
                authViewModel = authViewModel
            )
        }

        composable(Screen.Register.route) {
            RegisterScreen(
                navController = navController,
                authViewModel = authViewModel
            )
        }

        composable(Screen.EventList.route) {
            EventListScreen(
                navController = navController,
                authViewModel = authViewModel,
                eventViewModel = eventViewModel
            )
        }

        composable(Screen.CreateEvent.route) {
            CreateEventScreen(
                navController = navController,
                eventViewModel = eventViewModel
            )
        }

        composable(
            route = Screen.EditEvent.route + "/{eventId}/{title}/{date}/{description}",
            arguments = listOf(
                navArgument("eventId") { type = NavType.StringType },
                navArgument("title") { type = NavType.StringType },
                navArgument("date") { type = NavType.StringType },
                navArgument("description") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            EditEventScreen(
                navController = navController,
                eventViewModel = eventViewModel,
                eventId = backStackEntry.arguments?.getString("eventId") ?: "",
                currentTitle = backStackEntry.arguments?.getString("title") ?: "",
                currentDate = backStackEntry.arguments?.getString("date") ?: "",
                currentDescription = backStackEntry.arguments?.getString("description") ?: ""
            )
        }
    }
}

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
    object EventList : Screen("event_list")
    object CreateEvent : Screen("create_event")
    object EditEvent : Screen("edit_event")
}