package com.marcnunes.calculator.ui.theme


import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun CalculatorTheme(
    darkTheme: Boolean,
    accentColor: Int,
    content: @Composable () -> Unit
) {
    val accent = Color(accentColor)
    MaterialTheme(
        colorScheme = if (darkTheme)
            darkColorScheme(primary = accent, secondary = accent)
        else
            lightColorScheme(primary = accent, secondary = accent),
        content = content
    )
}

fun keyShape(cornerPct: Int): RoundedCornerShape {
    val p = cornerPct.coerceIn(0, 100) / 100f
    return RoundedCornerShape(percent = (p * 50).toInt().coerceAtLeast(4))
}
