package com.example.fittrack

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.fittrack.data.Tab
import com.example.fittrack.ui.theme.FitTrackTheme

class MainActivity : ComponentActivity() {
    @SuppressLint("CoroutineCreationDuringComposition")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FitTrackTheme {
                FitTrackAppContent()
            }
        }
    }
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @Composable
    private fun FitTrackAppContent() {
        val refreshTrigger = remember { mutableStateOf(false) }
        val navController = rememberNavController()
        var selectedTab by remember { mutableStateOf(Tab.Home) }

        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route
        LaunchedEffect(currentRoute) {
            selectedTab = when (currentRoute) {
                "home" -> Tab.Home
                "record" -> Tab.Record
                "stats" -> Tab.Stats
                else -> Tab.Home
            }
        }

        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Scaffold(
                topBar = {
                    if (currentRoute != "splash") {
                        FitTopAppBar(selectedTab)
                    }
                },
                bottomBar = {
                    if (currentRoute != "splash") {
                        BottomBar(selectedTab) {
                            when (it) {
                                Tab.Home -> navController.navigate("home")
                                Tab.Record -> navController.navigate("record")
                                Tab.Stats -> navController.navigate("stats")
                            }
                        }
                    }
                }
            ) {
                NavHost(navController, startDestination = "splash") {
                    composable("splash") {
                        SplashScreen {
                            navController.navigate("home")
                        }
                    }
                    composable("home") {
                        HomeScreen()
                    }
                    composable("record") {
                        RecordScreen()
                    }
                    composable("stats") {
                        StatsScreen(refreshTrigger)
                    }
                }
            }
        }
    }
}