package com.example.spacemeta

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import com.example.spacemeta.screens.NavGraph
import com.example.spacemeta.ui.theme.SpaceMetaTheme
import com.google.firebase.FirebaseApp


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        enableEdgeToEdge()
        setContent {
            SpaceMetaTheme {
                val navController = rememberNavController()
                NavGraph(navController)
            }
        }
    }
}