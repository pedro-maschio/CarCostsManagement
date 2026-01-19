package com.pedro.maschio.carcostsmanagement.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
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
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pedro.maschio.carcostsmanagement.data.database.entities.CarCost
import com.pedro.maschio.carcostsmanagement.utils.DateUtils.getDateStringFromMillis

@Composable
fun AddCostEntry(
    modifier: Modifier = Modifier,
    costEntry: CarCost?,
    onCostEntryAdded: (CarCost) -> Unit = {},
    onCostEntryCancelled: () -> Unit = {},
) {
    var model: CarCost by remember {
        mutableStateOf(
            costEntry ?: CarCost(
                type = "",
                price = 0.0,
                date = System.currentTimeMillis(),
                mileage = 0,
                description = ""
            )
        )
    }
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()

    fun onPriceChange(price: String) {
        if (price.isBlank()) {
            model = model.copy(price = 0.0)
            return
        }
        if (price.toDoubleOrNull() == null) {
            return
        }
        model = model.copy(price = price.toDouble())
    }

    fun onMileageChange(mileage: String) {
        if (mileage.isBlank()) {
            model = model.copy(mileage = 0)
            return
        }
        if (mileage.toIntOrNull() == null) {
            return
        }
        model = model.copy(mileage = mileage.toInt())
    }

    fun onDescriptionChange(description: String) {
        model = model.copy(description = description)
    }

    fun onDateChange(date: Long) {
        model = model.copy(date = date)
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        val radioOptions = listOf("Gas", "Maintenance", "Other")
        val (selectedOption, onOptionSelected) = remember { mutableStateOf(radioOptions[0]) }
        Column(modifier.selectableGroup()) {
            Text(text = "Type")
            radioOptions.forEach { text ->
                Row(
                    Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .selectable(
                            selected = (text == selectedOption),
                            onClick = { onOptionSelected(text) },
                            role = Role.RadioButton
                        )
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = (text == selectedOption),
                        onClick = null // null recommended for accessibility with screen readers
                    )
                    Text(
                        text = text,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(start = 16.dp)
                    )
                }
            }
        }

        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            ),
            label = {
                Text(text = "Amount (R$)")
            },
            maxLines = 1,
            value = if(model.price == 0.0) "" else model.price.toString(),
            onValueChange = { onPriceChange(it) },
        )
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
            label = {
                Text(text = "Current mileage (optional)")
            },
            maxLines = 1,
            value = if(model.mileage == 0) "" else model.mileage.toString(),
            onValueChange = { onMileageChange(it) })
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            label = {
                Text(text = "Description (optional)")
            },
            value = model.description, onValueChange = { onDescriptionChange(it) })
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            label = {
                Text(text = "Date")
            },
            readOnly = true,
            trailingIcon = {
                IconButton(onClick = { showDatePicker = !showDatePicker }) {
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = "Select date"
                    )
                }
            },
            maxLines = 1,
            value = getDateStringFromMillis(model.date), onValueChange = {
                // No op
            })

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.Center,
        ) {
            Button(
                modifier = Modifier
                    .weight(1f)
                    .padding(top = 16.dp, bottom = 16.dp, end = 8.dp), onClick = {
                    onCostEntryAdded(
                        model.copy(
                            type = selectedOption
                        )
                    )
                }) {
                Text(text = if (model.id == 0L) "Add cost" else "Update cost")
            }
            Button(
                modifier = Modifier.padding(top = 16.dp, bottom = 16.dp, end = 8.dp),
                onClick = {
                    onCostEntryCancelled()
                }) {
                Text(text = "Cancel")
            }
        }
    }

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let {
                        onDateChange(it)
                    }
                    showDatePicker = false
                }) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}


@Preview
@Composable
fun AddCostEntryPreview(modifier: Modifier = Modifier) {
    AddCostEntry(
        costEntry = CarCost(
            id = 0,
            type = "Gas",
            price = 247.0,
            date = System.currentTimeMillis(),
            mileage = 134000,
            description = ""
        )
    )
}