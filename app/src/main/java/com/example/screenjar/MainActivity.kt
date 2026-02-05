package com.example.screenjar

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.screenjar.ui.theme.ScreenJarTheme
import com.example.screenjar.viewmodel.ScreenTimeViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ScreenJarTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    ScreenJarScreen(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun ScreenJarScreen(
    modifier: Modifier = Modifier,
    viewModel: ScreenTimeViewModel = viewModel()
) {
    val jarPercent by viewModel.jarPercentage.observeAsState(0)
    val jarMessage by viewModel.jarMessage.observeAsState("Loading...")

    LaunchedEffect(Unit) {
        viewModel.updateJar()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "ScreenJar 🍯")
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "Jar Fill: $jarPercent%")
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = jarMessage)
    }
}

@Preview(showBackground = true)
@Composable
fun ScreenJarPreview() {
    ScreenJarTheme {
        ScreenJarScreen()
    }
}
