package com.example.screenjar.ui

import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.request.ImageRequest
import com.example.screenjar.ui.theme.ScreenJarTheme
import com.example.screenjar.viewmodel.ScreenTimeViewModel

@Composable
fun ScreenJarRoute(
    modifier: Modifier = Modifier,
    viewModel: ScreenTimeViewModel = viewModel(),
    showDebugControls: Boolean = true
) {
    val jarPercent by viewModel.jarPercentage.observeAsState(0)
    val jarMessage by viewModel.jarMessage.observeAsState("Loading...")
    val debugOverride = remember { mutableStateOf<Int?>(null) }

    LaunchedEffect(Unit) {
        viewModel.updateJar()
    }

    val displayPercent = debugOverride.value ?: jarPercent
    val displayMessage = if (debugOverride.value != null) {
        "Simulated screen time for preview"
    } else {
        jarMessage
    }

    ScreenJarUi(
        jarPercent = displayPercent,
        jarMessage = displayMessage,
        modifier = modifier,
        debugOverride = debugOverride,
        showDebugControls = showDebugControls
    )
}

@Composable
fun ScreenJarUi(
    jarPercent: Int,
    jarMessage: String,
    modifier: Modifier = Modifier,
    debugOverride: androidx.compose.runtime.MutableState<Int?>? = null,
    showDebugControls: Boolean = true
) {
    val assetName = potionAssetForPercent(jarPercent)
    val backgroundColor = potionBackgroundForAsset(assetName)
    val context = LocalContext.current
    val imageLoader = remember {
        ImageLoader.Builder(context)
            .components {
                if (Build.VERSION.SDK_INT >= 28) {
                    add(ImageDecoderDecoder.Factory())
                } else {
                    add(GifDecoder.Factory())
                }
            }
            .build()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(backgroundColor)
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "ScreenJar",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "Screen time impact: $jarPercent%",
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = jarMessage,
            style = MaterialTheme.typography.bodyMedium
        )

        if (showDebugControls && debugOverride != null) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Debug: simulate screen time",
                style = MaterialTheme.typography.labelMedium
            )
            val sliderValue = remember(jarPercent) { mutableIntStateOf(jarPercent) }
            Slider(
                value = sliderValue.intValue.toFloat(),
                onValueChange = {
                    sliderValue.intValue = it.toInt()
                    debugOverride.value = sliderValue.intValue
                },
                valueRange = 0f..100f
            )
            Text(
                text = "Simulated: ${sliderValue.intValue}%",
                style = MaterialTheme.typography.labelSmall
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        AsyncImage(
            model = ImageRequest.Builder(context)
                .data("file:///android_asset/$assetName")
                .build(),
            imageLoader = imageLoader,
            contentDescription = "Potion state",
            modifier = Modifier.height(260.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "The potion looks more sinister as screen time rises.",
            style = MaterialTheme.typography.labelMedium
        )
    }
}

private fun potionAssetForPercent(percent: Int): String {
    return when {
        percent <= 10 -> "potion_pure.gif"
        percent <= 30 -> "potion_empty.gif"
        percent <= 55 -> "potion_bad.gif"
        percent <= 75 -> "potion_okay.gif"
        percent <= 90 -> "potion_worse.gif"
        else -> "potion_worse.gif"
    }
}

private fun potionBackgroundForAsset(assetName: String): Color {
    return when (assetName) {
        "potion_pure.gif" -> Color(0xFFE9F8E7)
        "potion_.gif" -> Color(0xFFEAF2FF)
        "potion_bad.gif" -> Color(0xFFFFF2DB)
        "potion_worse.gif" -> Color(0xFFFDE2E2)
        "potion_cursed.gif" -> Color(0xFFE9D7FF)
        "potion_empty.gif" -> Color(0xFFEFEFEF)
        else -> Color(0xFFF5F5F5)
    }
}

@Preview(showBackground = true)
@Composable
fun ScreenJarPreview() {
    ScreenJarTheme {
        ScreenJarUi(
            jarPercent = 42,
            jarMessage = "Keep it in check to avoid a cursed brew.",
            showDebugControls = false
        )
    }
}
