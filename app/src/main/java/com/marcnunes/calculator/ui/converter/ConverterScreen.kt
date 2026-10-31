package com.marcnunes.calculator.ui.converter


import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf

import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.marcnunes.calculator.R
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import kotlin.math.pow


data class UnitDef(val label: String, val factor: Double)
data class ToolDef(@StringRes val nameRes: Int, val key: String, val iconRes: Int? = null, val accentRes: Int? = null)

object Categories {
    val unitCategories: Map<String, List<UnitDef>> = linkedMapOf(
        "length" to listOf(
            UnitDef("mm", 0.001), UnitDef("cm", 0.01), UnitDef("m", 1.0), UnitDef("km", 1000.0),
            UnitDef("po", 0.0254), UnitDef("pi", 0.3048), UnitDef("mi", 1609.344), UnitDef("M (marin)", 1852.0)
        ),
        "mass" to listOf(
            UnitDef("mg", 0.000001), UnitDef("g", 0.001), UnitDef("kg", 1.0), UnitDef("t", 1000.0),
            UnitDef("oz", 0.0283495), UnitDef("lb", 0.453592)
        ),
        "volume" to listOf(
            UnitDef("ml", 0.001), UnitDef("cl", 0.01), UnitDef("L", 1.0), UnitDef("m³", 1000.0),
            UnitDef("gal US", 3.78541), UnitDef("pt US", 0.473176)
        ),
        "temperature" to listOf(UnitDef("°C", 0.0), UnitDef("°F", 0.0), UnitDef("K", 0.0)),
        "area" to listOf(
            UnitDef("m²", 1.0), UnitDef("km²", 1000000.0), UnitDef("ha", 10000.0), UnitDef("a", 100.0),
            UnitDef("pi²", 0.092903), UnitDef("acre", 4046.86)
        ),
        "speed" to listOf(
            UnitDef("m/s", 1.0), UnitDef("km/h", 0.277778), UnitDef("mph", 0.44704), UnitDef("nœud", 0.514444)
        ),
        "time" to listOf(
            UnitDef("s", 1.0), UnitDef("min", 60.0), UnitDef("h", 3600.0),
            UnitDef("j", 86400.0), UnitDef("sem", 604800.0), UnitDef("an", 31536000.0)
        ),
        "data" to listOf(
            UnitDef("bit", 0.125), UnitDef("B", 1.0), UnitDef("KB", 1024.0), UnitDef("MB", 1048576.0),
            UnitDef("GB", 1073741824.0), UnitDef("TB", 1099511627776.0)
        ),
        "numeral" to listOf(UnitDef("DEC", 0.0), UnitDef("BIN", 0.0), UnitDef("OCT", 0.0), UnitDef("HEX", 0.0))
    )


    val tools: List<ToolDef> = listOf(
        ToolDef(R.string.tool_length, "length", R.drawable.ic_length, R.drawable.ic_length_accent),
        ToolDef(R.string.tool_mass, "mass", R.drawable.ic_weight),
        ToolDef(R.string.tool_volume, "volume", R.drawable.ic_volume),
        ToolDef(R.string.tool_temperature, "temperature", R.drawable.ic_temperature),
        ToolDef(R.string.tool_area, "area", R.drawable.ic_area),
        ToolDef(R.string.tool_speed, "speed", R.drawable.ic_speed, R.drawable.ic_speed_accent),
        ToolDef(R.string.tool_time, "time", R.drawable.ic_time),
        ToolDef(R.string.tool_data, "data", R.drawable.ic_data),
        ToolDef(R.string.tool_numeral, "numeral", R.drawable.ic_base, R.drawable.ic_base_accent),
        ToolDef(R.string.tool_imc, "imc", R.drawable.ic_imc),
        ToolDef(R.string.tool_date, "date", R.drawable.ic_date),
        ToolDef(R.string.tool_ip_formats, "ip_formats", R.drawable.ic_ip_converter),
        ToolDef(R.string.tool_ip_range, "ip_range", R.drawable.ic_ip_range)
    )
}


@Composable
fun ConverterScreen(context: Context) {
    var tool by remember { mutableStateOf<ToolDef?>(null) }
    tool?.let { t ->
        Column(Modifier.fillMaxSize()) {
            ToolHeader(title = stringResource(t.nameRes), onBack = { tool = null })
            Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp)) {
                when (t.key) {
                    "imc" -> ImcTool()
                    "date" -> DateTool()
                    "ip_formats" -> IpFormatTool(context)
                    "ip_range" -> IpRangeTool(context)
                    else -> UnitTool(context, t.key)
                }
            }
        }
    } ?: run {
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier.fillMaxSize().padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(Categories.tools.size) { i ->
                val t = Categories.tools[i]
                ElevatedCard(onClick = { tool = t }, modifier = Modifier.height(80.dp)) {
                    Column(
                        Modifier.fillMaxSize().padding(4.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        t.iconRes?.let { ToolIcon(it, t.accentRes, Modifier.size(28.dp)) }
                        Spacer(Modifier.height(4.dp))
                        Text(stringResource(t.nameRes), fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun ToolHeader(title: String, onBack: () -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
        IconButton(onClick = onBack) { Icon(painterResource(R.drawable.ic_back), contentDescription = null) }
        Text(title, fontSize = 20.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun ToolIcon(iconRes: Int, accentRes: Int?, modifier: Modifier = Modifier) {
    val primary = MaterialTheme.colorScheme.primary
    val accent2 = lerp(primary, Color.Black, 0.45f)
    Box(modifier, contentAlignment = Alignment.Center) {
        Image(painterResource(iconRes), contentDescription = null, modifier = Modifier.fillMaxSize().padding(2.dp), contentScale = ContentScale.Fit, colorFilter = ColorFilter.tint(primary))
        accentRes?.let {
            Image(painterResource(it), contentDescription = null, modifier = Modifier.fillMaxSize().padding(2.dp), contentScale = ContentScale.Fit, colorFilter = ColorFilter.tint(accent2))
        }
    }
}


private fun convertUnits(cat: String, units: List<UnitDef>, v: Double, from: Int, to: Int): String {
    if (cat == "temperature") {
        val c = when (units[from].label) { "°C" -> v; "°F" -> (v - 32) * 5 / 9; else -> v - 273.15 }
        val r = when (units[to].label) { "°C" -> c; "°F" -> c * 9 / 5 + 32; else -> c + 273.15 }
        return String.format("%.2f", r)
    }
    return String.format("%.6g", v * units[from].factor / units[to].factor)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun UnitTool(context: Context, cat: String) {
    val units = Categories.unitCategories[cat] ?: return
    val isNumeral = (cat == "numeral")
    var value by remember { mutableStateOf("") }
    var from by remember { mutableStateOf(0) }
    var to by remember { mutableStateOf(if (isNumeral) 3 else 1) }
    var copied by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = value,
        onValueChange = { value = it },
        label = { Text(stringResource(R.string.value_label, units[from].label)) },
        modifier = Modifier.fillMaxWidth(), singleLine = true
    )
    Spacer(Modifier.height(12.dp))
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
        UnitDropdown(units, from, Modifier.weight(1f)) { from = it }
        UnitDropdown(units, to, Modifier.weight(1f)) { to = it }
    }
    Spacer(Modifier.height(16.dp))

    val result: String = if (isNumeral) {
        val bases = mapOf(0 to 10, 1 to 2, 2 to 8, 3 to 16)
        val n = value.trim().toLongOrNull(bases[from] ?: 10)
        if (value.isBlank() || n == null) "—" else when (bases[to] ?: 10) {
            10 -> n.toString()
            2 -> n.toString(2)
            8 -> n.toString(8)
            else -> n.toString(16).uppercase()
        }
    } else {
        val v = value.trim().toDoubleOrNull() ?: 0.0
        convertUnits(cat, units, v, from, to)
    }


    OutlinedTextField(
        value = result,
        onValueChange = {},
        readOnly = true,
        label = { Text(stringResource(R.string.result_label, units[to].label)) },
        trailingIcon = {
            IconButton(onClick = {
                val cm = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                cm.setPrimaryClip(ClipData.newPlainText("result", result))
                copied = true
            }) { Icon(painterResource(R.drawable.ic_copy), contentDescription = stringResource(R.string.copy)) }
        },
        modifier = Modifier.fillMaxWidth(), singleLine = true
    )
    if (copied) Text(stringResource(R.string.copied), color = MaterialTheme.colorScheme.primary, fontSize = 12.sp)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun UnitDropdown(units: List<UnitDef>, sel: Int, modifier: Modifier = Modifier, onPick: (Int) -> Unit) {
    var open by remember { mutableStateOf(false) }
    Box(modifier) {
        OutlinedButton(onClick = { open = true }, modifier = Modifier.fillMaxWidth()) {
            Text(stringResource(R.string.from_prefix) + " " + units[sel].label, fontSize = 12.sp, maxLines = 1)
        }
        DropdownMenu(expanded = open, onDismissRequest = { open = false }) {
            units.forEachIndexed { i, u ->
                DropdownMenuItem(text = { Text(u.label) }, onClick = { open = false; onPick(i) })
            }
        }
    }
}


@Composable
private fun ImcTool() {
    var poids by remember { mutableStateOf("") }
    var taille by remember { mutableStateOf("") }
    var imc by remember { mutableStateOf<Float?>(null) }
    OutlinedTextField(poids, { poids = it.filter { c -> c.isDigit() || c == '.' } }, label = { Text(stringResource(R.string.imc_weight)) }, modifier = Modifier.fillMaxWidth(), singleLine = true)
    Spacer(Modifier.height(8.dp))
    OutlinedTextField(taille, { taille = it.filter { c -> c.isDigit() || c == '.' } }, label = { Text(stringResource(R.string.imc_height)) }, modifier = Modifier.fillMaxWidth(), singleLine = true)
    Spacer(Modifier.height(16.dp))
    Button(onClick = { imc = poids.toFloatOrNull()?.let { p -> taille.toFloatOrNull()?.let { t -> p / (t / 100f).pow(2) } } }, modifier = Modifier.fillMaxWidth()) {
        Text(stringResource(R.string.imc_button))
    }
    imc?.let { v ->
        Spacer(Modifier.height(12.dp))
        val cat = when {
            v < 18.5 -> stringResource(R.string.imc_underweight)
            v < 25 -> stringResource(R.string.imc_normal)
            v < 30 -> stringResource(R.string.imc_overweight)
            v < 35 -> stringResource(R.string.imc_obese1)
            v < 40 -> stringResource(R.string.imc_obese2)
            else -> stringResource(R.string.imc_obese3)
        }
        Text(stringResource(R.string.imc_result, String.format("%.1f", v), cat), fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DateTool() {
    var debut by remember { mutableStateOf<LocalDate?>(null) }
    var fin by remember { mutableStateOf<LocalDate?>(null) }
    var showPicker by remember { mutableStateOf(false) }
    var pickingStart by remember { mutableStateOf(true) }


    Text(stringResource(R.string.tool_date), fontWeight = FontWeight.SemiBold)
    Spacer(Modifier.height(12.dp))
    OutlinedTextField(debut?.toString() ?: "", {}, readOnly = true, label = { Text(stringResource(R.string.date_start)) }, modifier = Modifier.fillMaxWidth())
    Spacer(Modifier.height(8.dp))
    OutlinedTextField(fin?.toString() ?: "", {}, readOnly = true, label = { Text(stringResource(R.string.date_end)) }, modifier = Modifier.fillMaxWidth())
    Spacer(Modifier.height(12.dp))
    Button(onClick = { pickingStart = true; showPicker = true }, modifier = Modifier.fillMaxWidth()) {
        Icon(painterResource(R.drawable.ic_calendar), contentDescription = null)
        Spacer(Modifier.width(6.dp))
        Text(stringResource(R.string.pick_dates))
    }
    Spacer(Modifier.height(12.dp))
    val ecart = if (debut != null && fin != null)
        stringResource(R.string.ecart_days, ChronoUnit.DAYS.between(debut, fin))
    else stringResource(R.string.ecart_none)
    Text(ecart, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)

    if (showPicker) {
        val state = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { showPicker = false },
            confirmButton = {
                TextButton(onClick = {
                    state.selectedDateMillis?.let { millis ->
                        val d = Instant.ofEpochMilli(millis).atZone(ZoneId.of("UTC")).toLocalDate()
                        if (pickingStart) { debut = d; pickingStart = false } else { fin = d; showPicker = false }
                    }
                }) { Text(stringResource(R.string.ok)) }
            },
            dismissButton = { TextButton(onClick = { showPicker = false }) { Text(stringResource(R.string.cancel)) } }
        ) { DatePicker(state = state) }
    }
}


private fun parseIp(s: String): Long? {
    val parts = s.trim().split(".")
    if (parts.size != 4) return null
    var l = 0L
    for (p in parts) {
        val v = p.toIntOrNull() ?: return null
        if (v !in 0..255) return null
        l = (l shl 8) or v.toLong()
    }
    return l
}

private fun ipToString(l: Long): String =
    ((l shr 24) and 255).toString() + "." + ((l shr 16) and 255).toString() + "." + ((l shr 8) and 255).toString() + "." + (l and 255).toString()

private fun maskLong(p: Int): Long = if (p <= 0) 0L else (-1L shl (32 - p)) and 0xFFFFFFFFL

@Composable
private fun CopyRow(context: Context, label: String, value: String) {
    Row(Modifier.fillMaxWidth().padding(vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
        Column(Modifier.weight(1f)) {
            Text(label, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
            Text(value, fontSize = 15.sp)
        }
        IconButton(onClick = {
            val cm = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            cm.setPrimaryClip(ClipData.newPlainText(label, value))
        }) { Icon(painterResource(R.drawable.ic_copy), contentDescription = stringResource(R.string.copy), Modifier.size(18.dp)) }
    }
}


@Composable
private fun IpFormatTool(context: Context) {
    var ip by remember { mutableStateOf("192.168.1.1") }
    var cidr by remember { mutableStateOf("/24") }
    Text(stringResource(R.string.ip_input_format), fontWeight = FontWeight.SemiBold)
    Text(stringResource(R.string.ip_decimal_hint), fontSize = 13.sp)
    Spacer(Modifier.height(8.dp))
    OutlinedTextField(ip, { ip = it }, label = { Text(stringResource(R.string.ip_label)) }, modifier = Modifier.fillMaxWidth(), singleLine = true)
    Spacer(Modifier.height(8.dp))
    OutlinedTextField(cidr, { cidr = it }, label = { Text(stringResource(R.string.cidr_label)) }, modifier = Modifier.fillMaxWidth(), singleLine = true)
    Spacer(Modifier.height(12.dp))
    Text(stringResource(R.string.ip_formats_title), fontWeight = FontWeight.Bold)
    val l = parseIp(ip)
    val prefix = cidr.trim().removePrefix("/").toIntOrNull() ?: 24
    if (l != null) {
        CopyRow(context, stringResource(R.string.ip_fmt_dotted), ipToString(l))
        CopyRow(context, stringResource(R.string.ip_fmt_hex), String.format("%08X", l))
        CopyRow(context, stringResource(R.string.ip_fmt_binary), String.format("%32s", l.toString(2)).replace(' ', '0').chunked(8).joinToString("."))
        CopyRow(context, stringResource(R.string.ip_fmt_int32), l.toString())
        CopyRow(context, stringResource(R.string.ip_fmt_netmask, "/" + prefix), ipToString(maskLong(prefix)))
    } else {
        Text(stringResource(R.string.invalid_ip), color = MaterialTheme.colorScheme.error)
    }
}


@Composable
private fun IpRangeTool(context: Context) {
    var ip by remember { mutableStateOf("192.168.1.50") }
    var prefix by remember { mutableStateOf(24f) }
    Text(stringResource(R.string.iprange_subtitle), fontSize = 13.sp)
    Spacer(Modifier.height(8.dp))
    OutlinedTextField(ip, { ip = it }, label = { Text(stringResource(R.string.ip_label)) }, modifier = Modifier.fillMaxWidth(), singleLine = true)
    Spacer(Modifier.height(8.dp))
    val p = prefix.toInt()
    Text(stringResource(R.string.iprange_netmask_cidr, ipToString(maskLong(p)), p), fontWeight = FontWeight.SemiBold)
    Slider(value = prefix, onValueChange = { prefix = it }, valueRange = 0f..32f, steps = 31)

    val l = parseIp(ip)
    if (l == null) {
        Text(stringResource(R.string.invalid_ip), color = MaterialTheme.colorScheme.error)
        return
    }
    val mask = maskLong(p)
    val network = l and mask
    val broadcast = network or (mask.inv() and 0xFFFFFFFFL)
    val total = 1L shl (32 - p)
    val hosts = if (p >= 31) total else total - 2
    val first = if (p >= 31) network else network + 1
    val last = if (p >= 31) broadcast else broadcast - 1
    val firstOctet = ip.trim().split(".").firstOrNull()?.toIntOrNull() ?: 0
    val classe = when (firstOctet) {
        in 1..126 -> "A"; in 128..191 -> "B"; in 192..223 -> "C"; in 224..239 -> "D"; else -> "E"
    }
    val prive = (firstOctet == 10) || (ip.trim().startsWith("192.168.")) ||
        (ip.trim().startsWith("172.") && (ip.trim().split(".").getOrNull(1)?.toIntOrNull() in 16..31))


    Spacer(Modifier.height(12.dp))
    Text(stringResource(R.string.iprange_synth_title), fontWeight = FontWeight.Bold)
    CopyRow(context, stringResource(R.string.iprange_entered), ip.trim())
    CopyRow(context, stringResource(R.string.iprange_netmask), ipToString(mask) + " (/" + p + ")")
    CopyRow(context, stringResource(R.string.iprange_wildcard), ipToString(mask.inv() and 0xFFFFFFFFL))
    CopyRow(context, stringResource(R.string.iprange_class), classe + " — " + stringResource(if (prive) R.string.iprange_private else R.string.iprange_public))

    Spacer(Modifier.height(12.dp))
    Text(stringResource(R.string.iprange_range_title), fontWeight = FontWeight.Bold)
    CopyRow(context, stringResource(R.string.iprange_network_id), ipToString(network))
    CopyRow(context, stringResource(R.string.iprange_broadcast), ipToString(broadcast))
    CopyRow(context, stringResource(R.string.iprange_first), ipToString(first))
    CopyRow(context, stringResource(R.string.iprange_last), ipToString(last))
    CopyRow(context, stringResource(R.string.iprange_hosts), stringResource(R.string.iprange_hosts_count, hosts, total))

    Spacer(Modifier.height(12.dp))
    Text(stringResource(R.string.iprange_binary_title), fontWeight = FontWeight.Bold)
    Text(binOctets(network) + "  " + stringResource(R.string.iprange_bin_network), fontSize = 12.sp)
    Text(binOctets(mask) + "  " + stringResource(R.string.iprange_bin_mask), fontSize = 12.sp)
    Text(binOctets(broadcast) + "  " + stringResource(R.string.iprange_bin_broadcast), fontSize = 12.sp)
    Spacer(Modifier.height(24.dp))
}

private fun binOctets(l: Long): String =
    String.format("%32s", l.toString(2)).replace(' ', '0').chunked(8).joinToString(".")
