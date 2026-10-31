package com.marcnunes.calculator.ui.calculator


import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.marcnunes.calculator.R
import com.marcnunes.calculator.ui.theme.keyShape
import androidx.compose.foundation.TooltipBox
import androidx.compose.material3.Tooltip
import kotlin.math.*

data class HistoryEntry(val expression: String, val result: String)


fun formatNumber(d: Double): String {
    if (d.isNaN() || d.isInfinite()) return "Error"
    if (d == d.toLong().toDouble()) return d.toLong().toString()
    return String.format("%.10g", d)
}


/** Parseur récursif descendant (les méthodes d'une classe peuvent se référencer librement). */
private class ExprParser(private val t: String) {
    private var pos = 0
    private fun peek(): Char = if (pos < t.length) t[pos] else ' '
    private fun next() { pos++ }
    private fun skip() { while (pos < t.length && t[pos] == ' ') pos++ }
    private fun atom(): Double {
        skip()
        if (peek() == '(') { next(); val v = add(); skip(); if (peek() == ')') next(); return v }
        if (peek() == '√') { next(); return sqrt(unary()) }
        val funcs = listOf(
            "sin(" to { x: Double -> sin(Math.toRadians(x)) },
            "cos(" to { x: Double -> cos(Math.toRadians(x)) },
            "tan(" to { x: Double -> tan(Math.toRadians(x)) },
            "ln(" to { x: Double -> ln(x) },
            "log(" to { x: Double -> log10(x) }
        )
        for ((name, fn) in funcs) {
            if (t.startsWith(name, pos)) { pos += name.length; val v = add(); skip(); if (peek() == ')') next(); return fn(v) }
        }
        val start = pos
        while (pos < t.length && (t[pos].isDigit() || t[pos] == '.')) pos++
        if (start == pos) { if (pos < t.length) pos++; return 0.0 }
        return t.substring(start, pos).toDouble()
    }
    private fun powTerm(): Double {
        val v = atom()
        skip()
        if (peek() == '^') { next(); return v.pow(unary()) }
        return v
    }
    private fun unary(): Double {
        skip()
        if (peek() == '-') { next(); return -unary() }
        if (peek() == '+') { next(); return unary() }
        return powTerm()
    }
    private fun mul(): Double {
        var v = unary()
        while (true) {
            skip()
            when (peek()) {
                '*' -> { next(); v *= unary() }
                '/' -> { next(); v /= unary() }
                '%' -> { next(); v = v.mod(unary()) }
                else -> return v
            }
        }
    }
    fun add(): Double {
        var v = mul()
        while (true) {
            skip()
            when (peek()) {
                '+' -> { next(); v += mul() }
                '-' -> { next(); v -= mul() }
                else -> return v
            }
        }
    }
}

fun evaluate(expr: String): Double = ExprParser(
    expr.replace("×", "*").replace("÷", "/").replace("−", "-").replace("π", Math.PI.toString())
).add()

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalculatorScreen(context: Context, cornerPct: Int) {
    var expression by remember { mutableStateOf("") }
    var result by remember { mutableStateOf("0") }
    var scientific by remember { mutableStateOf(false) }
    var showHistory by remember { mutableStateOf(false) }
    val history = remember { mutableStateListOf<HistoryEntry>() }
    var toast by remember { mutableStateOf(false) }
    val copiedMsg = stringResource(R.string.copied_clipboard)

    fun equals() {
        if (expression.isBlank()) return
        val r = formatNumber(evaluate(expression))
        result = r
        history.add(0, HistoryEntry(expression, r))
        expression = r
    }

    if (showHistory) {
        ModalBottomSheet(onDismissRequest = { showHistory = false }) {
            Column(Modifier.padding(16.dp)) {
                Text(stringResource(R.string.history_title), fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(8.dp))
                if (history.isEmpty()) Text(stringResource(R.string.no_calculations))
                LazyColumn {
                    items(history) { h ->
                        Row(Modifier.fillMaxWidth().padding(6.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(h.expression + " =")
                            Text(h.result, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }


    Column(Modifier.fillMaxSize().padding(8.dp)) {
        Column(
            Modifier.fillMaxWidth().padding(horizontal = 16.dp).weight(1f),
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.Center
        ) {
            IconButton(onClick = { showHistory = true }) {
                Icon(painterResource(R.drawable.ic_history), contentDescription = stringResource(R.string.history_title))
            }
            Spacer(Modifier.weight(1f))
            Text(expression.ifBlank { "0" }, fontSize = 26.sp, textAlign = TextAlign.End, modifier = Modifier.fillMaxWidth())
            Text(
                result, fontSize = 40.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.End,
                modifier = Modifier.fillMaxWidth().clickable {
                    val cm = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    cm.setPrimaryClip(ClipData.newPlainText("result", result))
                    toast = true
                }
            )
            if (toast) {
                Text(copiedMsg, color = MaterialTheme.colorScheme.primary, fontSize = 13.sp)
                LaunchedEffect(toast) { kotlinx.coroutines.delay(2000); toast = false }
            }
        }

        if (scientific) {
            val sci = listOf("sin(", "cos(", "tan(", "π", "ln(", "log(", "√", "^", "EXP", "x!", "(", ")")
            sci.chunked(6).forEach { rowKeys ->
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    rowKeys.forEach { k ->
                        val sciDescription = when (k) {
                            "sin(" -> stringResource(R.string.tooltip_sin)
                            "cos(" -> stringResource(R.string.tooltip_cos)
                            "tan(" -> stringResource(R.string.tooltip_tan)
                            "π" -> stringResource(R.string.tooltip_pi)
                            "ln(" -> stringResource(R.string.tooltip_ln)
                            "log(" -> stringResource(R.string.tooltip_log)
                            "√" -> stringResource(R.string.tooltip_sqrt)
                            "^" -> stringResource(R.string.tooltip_power)
                            "EXP" -> stringResource(R.string.tooltip_exp)
                            "x!" -> stringResource(R.string.tooltip_factorial)
                            "(" -> stringResource(R.string.tooltip_open_paren)
                            ")" -> stringResource(R.string.tooltip_close_paren)
                            else -> null
                        }
                        if (sciDescription != null) {
                            TooltipBox(
                                tooltip = { Tooltip(text = sciDescription) },
                                modifier = Modifier.weight(1f)
                            ) {
                                Key(k, keyShape(cornerPct), Modifier.fillMaxWidth(), highlight = false) { expression += k }
                            }
                        } else {
                            Key(k, keyShape(cornerPct), Modifier.weight(1f), highlight = false) { expression += k }
                        }
                    }
                }
                Spacer(Modifier.height(6.dp))
            }
        }


        val rows = listOf(
            listOf("AC", "X", "%", "÷"),
            listOf("7", "8", "9", "×"),
            listOf("4", "5", "6", "−"),
            listOf("1", "2", "3", "+"),
            listOf("±", "0", ",", "=")
        )
        rows.forEach { rowKeys ->
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                rowKeys.forEach { k ->
                    val onClick: () -> Unit = when (k) {
                        "AC" -> { { expression = ""; result = "0" } }
                        "X" -> { { if (expression.isNotEmpty()) expression = expression.dropLast(1) } }
                        "±" -> { { expression = if (expression.startsWith("-")) expression.drop(1) else "-" + expression } }
                        "=" -> { { equals() } }
                        else -> { { expression += if (k == ",") "." else k } }
                    }
                    val description = when (k) {
                        "AC" -> stringResource(R.string.tooltip_clear_all)
                        "X" -> stringResource(R.string.tooltip_delete)
                        "%" -> stringResource(R.string.tooltip_percent)
                        "÷" -> stringResource(R.string.tooltip_divide)
                        "×" -> stringResource(R.string.tooltip_multiply)
                        "−" -> stringResource(R.string.tooltip_subtract)
                        "+" -> stringResource(R.string.tooltip_add)
                        "±" -> stringResource(R.string.tooltip_negate)
                        "," -> stringResource(R.string.tooltip_decimal)
                        "=" -> stringResource(R.string.tooltip_equals)
                        else -> null
                    }
                    val isIcon = (k == "X")
                    if (description != null) {
                        TooltipBox(
                            tooltip = { Tooltip(text = description) },
                            modifier = Modifier.weight(1f)
                        ) {
                            if (isIcon) {
                                IconKey(R.drawable.ic_backspace, keyShape(cornerPct), Modifier.fillMaxWidth(), highlight = (k in listOf("=", "÷", "×", "−", "+")), onClick = onClick)
                            } else {
                                Key(k, keyShape(cornerPct), Modifier.fillMaxWidth(), highlight = (k in listOf("=", "÷", "×", "−", "+")), onClick = onClick)
                            }
                        }
                    } else {
                        if (isIcon) {
                            IconKey(R.drawable.ic_backspace, keyShape(cornerPct), Modifier.weight(1f), highlight = (k in listOf("=", "÷", "×", "−", "+")), onClick = onClick)
                        } else {
                            Key(k, keyShape(cornerPct), Modifier.weight(1f), highlight = (k in listOf("=", "÷", "×", "−", "+")), onClick = onClick)
                        }
                    }
                }
            }
            Spacer(Modifier.height(6.dp))
        }
        TextButton(onClick = { scientific = !scientific }) {
            Text(stringResource(if (scientific) R.string.mode_classic else R.string.mode_scientific))
        }
    }
}

@Composable
private fun IconKey(iconRes: Int, shape: androidx.compose.foundation.shape.RoundedCornerShape, modifier: Modifier = Modifier, highlight: Boolean = false, onClick: () -> Unit) {
    Box(
        modifier
            .height(64.dp)
            .background(if (highlight) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant, shape)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(painterResource(iconRes), contentDescription = null, tint = if (highlight) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface)
    }
}

@Composable
private fun Key(label: String, shape: androidx.compose.foundation.shape.RoundedCornerShape, modifier: Modifier = Modifier, highlight: Boolean = false, onClick: () -> Unit) {
    Box(
        modifier
            .height(64.dp)
            .background(if (highlight) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant, shape)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(label, fontSize = 20.sp, color = if (highlight) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface)
    }
}
