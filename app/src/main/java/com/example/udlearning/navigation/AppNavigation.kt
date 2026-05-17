package com.example.udlearning.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.compose.*
import com.example.udlearning.ui.screens.login.LoginScreen
import com.example.udlearning.ui.screens.register.RegisterScreen
import com.example.udlearning.ui.screens.recovery.RecoveryScreen
import com.example.udlearning.ui.screens.home.HomeScreen
import com.example.udlearning.ui.screens.profile.ProfileScreen
import com.example.udlearning.ui.screens.session.CreateSessionScreen
import com.example.udlearning.ui.screens.session.ScheduleSessionScreen
import com.example.udlearning.ui.screens.session.SessionViewModel
import com.example.udlearning.ui.screens.session.StudentSessionsScreen
import com.example.udlearning.ui.screens.session.SessionActivitiesScreen
import com.example.udlearning.ui.screens.session.TeacherSessionsScreen
import com.example.udlearning.ui.screens.session.ManageSessionScreen
import com.example.udlearning.ui.screens.session.ManageSessionViewModel
import com.example.udlearning.ui.screens.session.EditSessionScreen
import com.example.udlearning.ui.screens.activity.CreateActivityScreen
import com.example.udlearning.ui.screens.activity.EditActivityScreen
import com.example.udlearning.ui.screens.activity.SolveSessionScreen
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.udlearning.ui.screens.assessment.CreateAssessmentScreen
import com.example.udlearning.ui.screens.assessment.TeacherAssessmentsScreen
import com.example.udlearning.ui.screens.assessment.StudentAssessmentsScreen
import com.example.udlearning.ui.screens.assessment.assessment_activities.NewAssessmentScreen

@Composable
fun AppNavigation() {

    val navController = rememberNavController()
    val sharedSessionViewModel: SessionViewModel = viewModel()

    NavHost(navController, startDestination = "login") {

        composable("login") { LoginScreen(navController) }
        composable("register") { RegisterScreen(navController) }
        composable("recovery") { RecoveryScreen(navController) }
        composable("home") { HomeScreen(navController) }
        composable("profile") { ProfileScreen(navController) }
        
        composable("create_session") { 
            CreateSessionScreen(navController, sessionViewModel = sharedSessionViewModel) 
        }
        composable("schedule_session") { 
            ScheduleSessionScreen(navController, sessionViewModel = sharedSessionViewModel) 
        }
        
        composable("student_sessions") {
            StudentSessionsScreen(navController = navController)
        }

        composable("student_assessments") {
            StudentAssessmentsScreen(navController = navController)
        }

        composable("create_assessment"){
            CreateAssessmentScreen(navController = navController, sessionViewModel = sharedSessionViewModel)

        }
        
        composable("session_activities/{sessionId}") { backStackEntry ->
            val sessionId = backStackEntry.arguments?.getString("sessionId") ?: return@composable
            SessionActivitiesScreen(
                navController = navController,
                sessionId = sessionId
            )
        }
        
        composable("teacher_sessions") {
            TeacherSessionsScreen(navController = navController)
        }

        composable("teacher_assessments") {
            TeacherAssessmentsScreen(navController = navController)
        }

        composable("manage_session/{sessionId}") { backStackEntry ->
            val sessionId = backStackEntry.arguments?.getString("sessionId") ?: return@composable
            val manageVm: ManageSessionViewModel = viewModel(backStackEntry)
            ManageSessionScreen(
                navController = navController,
                sessionId = sessionId,
                viewModel = manageVm
            )
        }

        composable("edit_session/{sessionId}") { backStackEntry ->
            val sessionId = backStackEntry.arguments?.getString("sessionId") ?: return@composable
            // Reuse the parent ManageSessionViewModel so state is already loaded
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry("manage_session/$sessionId")
            }
            val manageVm: ManageSessionViewModel = viewModel(parentEntry)
            EditSessionScreen(
                navController = navController,
                viewModel = manageVm
            )
        }

        composable("create_activity/{sessionId}") { backStackEntry ->
            val sessionId = backStackEntry.arguments?.getString("sessionId") ?: return@composable
            CreateActivityScreen(
                navController = navController,
                sessionId = sessionId
            )
        }

        composable("new_assessment_activity/{sessionId}") { backStackEntry ->
            val sessionId = backStackEntry.arguments?.getString("sessionId") ?: return@composable
            NewAssessmentScreen(
                navController = navController,
                sessionId = sessionId
            )
        }

        composable("edit_activity/{sessionId}/{activityId}") { backStackEntry ->
            val sessionId = backStackEntry.arguments?.getString("sessionId") ?: return@composable
            val activityId = backStackEntry.arguments?.getString("activityId") ?: return@composable
            EditActivityScreen(
                navController = navController,
                sessionId = sessionId,
                activityId = activityId
            )
        }

        composable("solve_session/{sessionId}") { backStackEntry ->
            val sessionId = backStackEntry.arguments?.getString("sessionId") ?: return@composable
            SolveSessionScreen(
                navController = navController,
                sessionId = sessionId
            )
        }
    }
}