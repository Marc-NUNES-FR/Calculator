package com.marcnunes.calculator.ui.settings


import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.marcnunes.calculator.R

import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

val PRESET_COLORS = listOf(
    0xFF00BCD4, 0xFF2196F3, 0xFF3F51B5, 0xFF673AB7, 0xFFF44336, 0xFFFF5722,
    0xFFFF9800, 0xFF4CAF50, 0xFF009688, 0xFF795548, 0xFF607D8B
).map { it.toInt() }

@Composable
fun SettingsScreen(
    themeMode: MutableState<String>,
    darkMode: MutableState<Boolean>,
    accent: MutableState<Int>,
    cornerPct: MutableState<Int>,
    onReset: () -> Unit,
    onThemeModeChange: (String) -> Unit,
    onAccentChange: (Int) -> Unit,
    onCornerChange: (Int) -> Unit
) {
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp)) {
        Text(stringResource(R.string.tab_settings), fontSize = 22.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(16.dp))

        Text(stringResource(R.string.theme_mode), fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("LIGHT" to R.drawable.ic_sun, "AUTO" to R.drawable.ic_moon, "DARK" to R.drawable.ic_moon).forEach { (m, icon) ->
                FilterChip(
                    selected = themeMode.value == m,
                    onClick = { onThemeModeChange(m) },
                    label = { Text(stringResource(when (m) { "LIGHT" -> R.string.theme_light; "DARK" -> R.string.theme_dark; else -> R.string.theme_auto })) },
                    leadingIcon = { Icon(painterResource(icon), contentDescription = null, Modifier.size(16.dp)) }
                )
            }
        }


        Spacer(Modifier.height(20.dp))
        Text(stringResource(R.string.accentuation), fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.height(8.dp))
        PRESET_COLORS.chunked(6).forEach { rowColors ->
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                rowColors.forEach { c ->
                    Box(
                        Modifier.size(32.dp).background(Color(c), CircleShape).clickable { onAccentChange(c) },
                        contentAlignment = Alignment.Center
                    ) { if (accent.value == c) Text("✓", color = Color.White) }
                }
            }
            Spacer(Modifier.height(8.dp))
        }
        Spacer(Modifier.height(4.dp))
        ColorPicker(current = accent.value, onPick = onAccentChange)

        Spacer(Modifier.height(20.dp))
        Text(stringResource(R.string.key_rounding), fontWeight = FontWeight.SemiBold)
        Slider(value = cornerPct.value.toFloat(), onValueChange = { onCornerChange(it.toInt()) }, valueRange = 0f..100f)
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            listOf("7", "+", "=").forEach { k ->
                Box(
                    Modifier.weight(1f).height(48.dp).background(
                        MaterialTheme.colorScheme.surfaceVariant,
                        RoundedCornerShape(percent = (cornerPct.value / 2).coerceAtLeast(4))
                    ),
                    contentAlignment = Alignment.Center
                ) { Text(k, fontSize = 18.sp) }
            }
        }

        Spacer(Modifier.height(24.dp))
        Button(onClick = onReset, modifier = Modifier.fillMaxWidth()) {
            Text(stringResource(R.string.reset_all))
        }
        Spacer(Modifier.height(24.dp))
    }
}


@Composable
private fun ColorPicker(current: Int, onPick: (Int) -> Unit) {
    val hsb = FloatArray(3)
    android.graphics.Color.colorToHSV(current, hsb)
    var hue by remember { mutableStateOf(hsb[0]) }
    var v by remember { mutableStateOf(if (hsb[2] == 0f) 1f else hsb[2]) }

    val currentColor = android.graphics.Color.HSVToColor(floatArrayOf(hue, 1f, v))
    Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
        HueWheel(
            hue = hue,
            modifier = Modifier.size(140.dp).pointerInput(Unit) {
                detectTapGestures { offset ->
                    val c = Offset(size.width / 2f, size.height / 2f)
                    val d = sqrt((offset.x - c.x).pow(2) + (offset.y - c.y).pow(2))
                    if (d < minOf(size.width, size.height) / 2f) {
                        var ang = Math.toDegrees(atan2((offset.y - c.y).toDouble(), (offset.x - c.x).toDouble()))
                        if (ang < 0) ang += 360.0
                        hue = ang.toFloat()
                        onPick(android.graphics.Color.HSVToColor(floatArrayOf(hue, 1f, v)))
                    }
                }
            }
        )
    }
    Spacer(Modifier.height(8.dp))
    Text(stringResource(R.string.brightness), fontSize = 13.sp)
    Slider(value = v, onValueChange = {
        v = it
        onPick(android.graphics.Color.HSVToColor(floatArrayOf(hue, 1f, v)))
    })
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.size(36.dp).background(Color(currentColor), CircleShape))
        Spacer(Modifier.width(12.dp))
        Text(stringResource(R.string.custom_color), color = MaterialTheme.colorScheme.primary)
    }
}


@Composable
private fun HueWheel(hue: Float, modifier: Modifier = Modifier) {
    Canvas(modifier) {
        val r = this.size.minDimension / 2 - 10f
        for (i in 0..359 step 6) {
            val angle = Math.toRadians(i.toDouble())
            drawCircle(
                color = Color(android.graphics.Color.HSVToColor(floatArrayOf(i.toFloat(), 1f, 1f))),
                radius = r / 6,
                center = Offset(
                    this.center.x + ((r - r / 6) * cos(angle)).toFloat(),
                    this.center.y + ((r - r / 6) * sin(angle)).toFloat()
                )
            )
        }
        val a = Math.toRadians(hue.toDouble())
        drawCircle(
            color = Color.White,
            radius = 7f,
            center = Offset(
                this.center.x + ((r - r / 6) * cos(a)).toFloat(),
                this.center.y + ((r - r / 6) * sin(a)).toFloat()
            )
        )
    }
}
