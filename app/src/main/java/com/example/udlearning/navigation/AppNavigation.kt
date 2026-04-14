package com.example.udlearning.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.*
import com.example.udlearning.ui.screens.login.LoginScreen
import com.example.udlearning.ui.screens.register.RegisterScreen
import com.example.udlearning.ui.screens.recovery.RecoveryScreen
import com.example.udlearning.ui.screens.home.HomeScreen
import com.example.udlearning.ui.screens.profile.ProfileScreen

@Composable
fun AppNavigation() {

    val navController = rememberNavController()

    NavHost(navController, startDestination = "login") {

        composable("login") { LoginScreen(navController) }
        composable("register") { RegisterScreen(navController) }
        composable("recovery") { RecoveryScreen(navController) }
        composable("home") { HomeScreen(navController) }
        composable("profile") { ProfileScreen(navController) }
    }
}