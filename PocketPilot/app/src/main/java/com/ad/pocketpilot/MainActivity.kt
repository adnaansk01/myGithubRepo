package com.ad.pocketpilot

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.ad.pocketpilot.theme.GradientBackground
import com.ad.pocketpilot.ui.AddExpenseScreen
import com.ad.pocketpilot.ui.HomeScreen
import com.ad.pocketpilot.ui.theme.PocketPilotTheme

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PocketPilotTheme {
                GradientBackground {
                    InAppNavigation()
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun InAppNavigation() {
    val navController: NavHostController = rememberNavController()
    NavHost(navController = navController, startDestination = "homescreen") {
        composable("homescreen") {
            HomeScreen(onAddExpenseClick = {
                navController.navigate("addexpensescreen")
            }, navController)
        }

        composable("addexpensescreen") {
            AddExpenseScreen(onBack = {navController.popBackStack()}, navController)
        }
        composable("addexpensescreen/{id}") {
            backStackEntry ->
            val id = backStackEntry.arguments?.getString("id")?.toIntOrNull()
            if(id != null) {
                AddExpenseScreen(onBack = {navController.popBackStack()},navController,id)
            }
        }
    }
}