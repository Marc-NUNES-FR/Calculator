package com.marcnunes.calculator.ui

import android.content.Context
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.marcnunes.calculator.R
import com.marcnunes.calculator.ui.calculator.CalculatorScreen
import com.marcnunes.calculator.ui.converter.ConverterScreen
import com.marcnunes.calculator.ui.settings.SettingsScreen
import com.marcnunes.calculator.ui.theme.CalculatorTheme

private enum class Tab(val labelRes: Int) {
    CALCULATOR(R.string.tab_calculator),
    CONVERTER(R.string.tab_converter),
    SETTINGS(R.string.tab_settings)
}


@Composable
fun CalculatorApp(context: Context) {
    val prefs = remember { context.getSharedPreferences("calculator_prefs", Context.MODE_PRIVATE) }
    val tab = remember { mutableStateOf(Tab.CALCULATOR) }
    val themeMode = remember { mutableStateOf(prefs.getString("theme_mode", "AUTO") ?: "AUTO") }
    val systemDark = isSystemInDarkTheme()
    val darkMode = remember { mutableStateOf(themeMode.value == "DARK" || (themeMode.value != "LIGHT" && systemDark)) }
    LaunchedEffect(themeMode.value, systemDark) {
        darkMode.value = when (themeMode.value) {
            "LIGHT" -> false
            "DARK" -> true
            else -> systemDark
        }
    }
    val accent = remember { mutableStateOf(prefs.getInt("accent_color", 0xFF00BCD4.toInt())) }
    val cornerPct = remember { mutableStateOf(prefs.getInt("corner_pct", 50)) }

    fun applyTheme(mode: String) {
        themeMode.value = mode
        prefs.edit().putString("theme_mode", mode).apply()
    }


    CalculatorTheme(darkTheme = darkMode.value, accentColor = accent.value) {
        Surface(modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier.fillMaxSize()) {
                Spacer(Modifier.height(16.dp))
                TabRow(selectedTabIndex = tab.value.ordinal) {
                    Tab.entries.forEach { t ->
                        Tab(
                            selected = tab.value == t,
                            onClick = { tab.value = t },
                            text = { Text(stringResource(t.labelRes)) }
                        )
                    }
                }
                when (tab.value) {
                    Tab.CALCULATOR -> CalculatorScreen(context = context, cornerPct = cornerPct.value)
                    Tab.CONVERTER -> ConverterScreen(context = context)
                    Tab.SETTINGS -> SettingsScreen(
                        context = context,
                        themeMode = themeMode,
                        darkMode = darkMode,
                        accent = accent,
                        cornerPct = cornerPct,
                        onReset = {
                            applyTheme("AUTO")
                            accent.value = 0xFF00BCD4.toInt()
                            cornerPct.value = 50
                            prefs.edit()
                                .putInt("accent_color", 0xFF00BCD4.toInt())
                                .putInt("corner_pct", 50)
                                .apply()
                        },
                        onThemeModeChange = { applyTheme(it) },
                        onAccentChange = { c -> accent.value = c; prefs.edit().putInt("accent_color", c).apply() },
                        onCornerChange = { p -> cornerPct.value = p; prefs.edit().putInt("corner_pct", p).apply() }
                    )
                }
            }
        }
    }
}
