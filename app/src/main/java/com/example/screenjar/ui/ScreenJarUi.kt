package com.example.screenjar.ui

import android.os.Build
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
    val potionStatus = getPotionStatus(jarPercent)
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
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        backgroundColor,
                        backgroundColor.copy(alpha = 0.7f)
                    )
                )
            )
            .padding(20.dp),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header Section
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text(
                text = "🧪 ScreenJar 🧪",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 32.sp,
                color = Color(0xFF2C3E50)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Your Daily Potion Tracker",
                style = MaterialTheme.typography.labelLarge,
                fontStyle = FontStyle.Italic,
                color = Color(0xFF7F8C8D)
            )
        }

        // Potion Display
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.animateContentSize(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                )
            )
        ) {
            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data("file:///android_asset/$assetName")
                    .build(),
                imageLoader = imageLoader,
                contentDescription = "Potion state",
                modifier = Modifier.height(280.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = potionStatus.emoji,
                fontSize = 48.sp,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }

        // Status Cards
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(bottom = 8.dp)
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White.copy(alpha = 0.9f)
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = potionStatus.title,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = potionStatus.color,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "$jarPercent%",
                            style = MaterialTheme.typography.displaySmall,
                            fontWeight = FontWeight.Black,
                            color = potionStatus.color
                        )
                        Text(
                            text = " Screen Impact",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color(0xFF34495E)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = jarMessage,
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF2C3E50),
                        lineHeight = 24.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = potionStatus.description,
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        fontStyle = FontStyle.Italic,
                        color = Color(0xFF7F8C8D),
                        lineHeight = 20.sp
                    )
                }
            }

            if (showDebugControls && debugOverride != null) {
                Spacer(modifier = Modifier.height(16.dp))
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFFFF9E6)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "🛠️ Debug Mode: Test Potion Levels",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFE67E22)
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
                            style = MaterialTheme.typography.labelMedium,
                            color = Color(0xFFD35400)
                        )
                    }
                }
            }
        }
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

data class PotionStatus(
    val title: String,
    val emoji: String,
    val description: String,
    val color: Color
)

private fun getPotionStatus(percent: Int): PotionStatus {
    return when {
        percent <= 10 -> PotionStatus(
            title = "✨ Pure & Pristine ✨",
            emoji = "😇",
            description = "Your potion is crystal clear! Amazing self-control!",
            color = Color(0xFF27AE60)
        )
        percent <= 30 -> PotionStatus(
            title = "🫗 Nearly Empty",
            emoji = "😊",
            description = "Light usage today. The potion remains mostly pure.",
            color = Color(0xFF3498DB)
        )
        percent <= 55 -> PotionStatus(
            title = "⚠️ Getting Murky",
            emoji = "😐",
            description = "Screen time is building up. Watch out for the brew!",
            color = Color(0xFFF39C12)
        )
        percent <= 75 -> PotionStatus(
            title = "📊 Moderate Level",
            emoji = "😬",
            description = "The potion is darkening. Time to take a break?",
            color = Color(0xFFE67E22)
        )
        percent <= 90 -> PotionStatus(
            title = "🧪 Dangerously Dark",
            emoji = "😰",
            description = "Warning! The brew is getting sinister. Reduce screen time!",
            color = Color(0xFFE74C3C)
        )
        else -> PotionStatus(
            title = "💀 CURSED POTION 💀",
            emoji = "☠️",
            description = "CRITICAL! The potion has turned toxic! Step away from the screen NOW!",
            color = Color(0xFF8E44AD)
        )
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
