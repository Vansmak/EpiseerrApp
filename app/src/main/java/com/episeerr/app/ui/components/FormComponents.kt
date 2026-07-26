package com.episeerr.app.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@Composable
fun FormSpacer() {
    androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(6.dp))
}

@Composable
fun SectionLabel(text: String) {
    Text(
        text,
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(bottom = 4.dp)
    )
}

/** A two-option chip picker, e.g. episodes/all, monitor/search, series/season. */
@Composable
fun TwoChoiceRow(
    selected: String,
    options: Pair<Pair<String, String>, Pair<String, String>>,
    onSelect: (String) -> Unit
) {
    val (first, second) = options
    Row {
        FilterChip(
            selected = selected == first.first,
            onClick = { onSelect(first.first) },
            label = { Text(first.second) },
            modifier = Modifier.padding(end = 8.dp)
        )
        FilterChip(
            selected = selected == second.first,
            onClick = { onSelect(second.first) },
            label = { Text(second.second) }
        )
    }
}

@Composable
fun SwitchRow(label: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(end = 8.dp))
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}

@Composable
fun NumberField(label: String, value: String, optional: Boolean = false, onValueChange: (String) -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = { new -> if (new.all { it.isDigit() }) onValueChange(new) },
        label = { Text(if (optional) "$label (optional)" else label) },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
    )
}
