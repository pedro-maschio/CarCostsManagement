package com.pedro.maschio.carcostsmanagement.ui.components

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pedro.maschio.carcostsmanagement.R
import com.pedro.maschio.carcostsmanagement.data.database.entities.CarCost
import com.pedro.maschio.carcostsmanagement.data.database.entities.CostType
import com.pedro.maschio.carcostsmanagement.data.database.entities.RecurrenceType
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
                type = CostType.GAS.value,
                price = 0.0,
                date = System.currentTimeMillis(),
                description = ""
            )
        )
    }
    var showDatePicker by remember { mutableStateOf(false) }
    var showAdditionalFields by remember { mutableStateOf(model.description.isNotBlank()) }
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

        val radioOptions = stringArrayResource(R.array.cost_options)
        var selectedOption by remember { mutableIntStateOf(model.type) }
        Column(modifier.selectableGroup()) {
            Text(text = "Type")
            radioOptions.forEachIndexed { index, text ->
                Row(
                    Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .selectable(
                            selected = (index == selectedOption),
                            onClick = { selectedOption = index },
                            role = Role.RadioButton
                        )
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = (index == selectedOption),
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
                Text(text = stringResource(R.string.amount_label))
            },
            maxLines = 1,
            value = if (model.price == 0.0) "" else model.price.toString(),
            onValueChange = { onPriceChange(it) },
        )
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            label = {
                Text(text = stringResource(R.string.date_label))
            },
            readOnly = true,
            trailingIcon = {
                IconButton(onClick = { showDatePicker = !showDatePicker }) {
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = null
                    )
                }
            },
            maxLines = 1,
            value = getDateStringFromMillis(model.date), onValueChange = {
                // No op
            })

        val recurrenceOptions = stringArrayResource(R.array.recurrence_options)
        var selectedRecurrence by remember { mutableIntStateOf(model.recurrence) }

        AnimatedVisibility(visible = selectedOption == CostType.MAINTENANCE.value || selectedOption == CostType.OTHERS.value) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .selectableGroup()
            ) {
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                Text(
                    text = stringResource(R.string.recurrence_label),
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                recurrenceOptions.forEachIndexed { index, text ->
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .selectable(
                                selected = (index == selectedRecurrence),
                                onClick = { selectedRecurrence = index },
                                role = Role.RadioButton
                            ),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = (index == selectedRecurrence),
                            onClick = null
                        )
                        Text(
                            text = text,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(start = 16.dp)
                        )
                    }
                }
            }
        }

        if(!showAdditionalFields) {
            IconButton(onClick = {
                showAdditionalFields = !showAdditionalFields
            }) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null
                )
            }
        }
        AnimatedVisibility(visible = showAdditionalFields) {
            Column {
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    label = {
                        Text(text = stringResource(R.string.description_label))
                    },
                    value = model.description, onValueChange = { onDescriptionChange(it) })
            }
        }
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
                            type = selectedOption,
                            recurrence = if (selectedOption == CostType.MAINTENANCE.value || selectedOption == CostType.OTHERS.value) selectedRecurrence else RecurrenceType.NONE.value
                        )
                    )
                }) {
                Text(
                    text = if (model.id == 0L) stringResource(R.string.add_cost_button) else stringResource(
                        R.string.update_cost_button
                    )
                )
            }
            Button(
                modifier = Modifier.padding(top = 16.dp, bottom = 16.dp, end = 8.dp),
                onClick = {
                    onCostEntryCancelled()
                }) {
                Text(text = stringResource(R.string.cancel_button))
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
                    Text(stringResource(R.string.ok_button))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text(stringResource(R.string.cancel_button))
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}


@Preview
@Composable
fun AddCostEntryPreview() {
    AddCostEntry(
        costEntry = CarCost(
            id = 0,
            type = 0,
            price = 247.0,
            date = 32434223,
            description = "15000 KM"
        )
    )
}