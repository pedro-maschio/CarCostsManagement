package com.pedro.maschio.carcostsmanagement.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.LocalGasStation
import androidx.compose.material.icons.filled.MiscellaneousServices
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pedro.maschio.carcostsmanagement.R
import com.pedro.maschio.carcostsmanagement.utils.CurrencyUtils
import com.pedro.maschio.carcostsmanagement.utils.DateUtils.getDateStringFromMillis

@Composable
fun CostEntry(
    modifier: Modifier = Modifier,
    shape: RoundedCornerShape = RoundedCornerShape(size = 8.dp),
    title: String = "Shell gas station",
    type: Int = 0,
    date: Long = System.currentTimeMillis(),
    price: Double = 247.0,
    onCostClick: () -> Unit = {},
    onDeleteButtonClick: () -> Unit = {}
) {
    val costOptions = stringArrayResource(R.array.cost_options)
    val icon = when (type) {
        0 -> {
            Icons.Default.LocalGasStation
        }
        1 -> {
            Icons.Default.Build
        }
        else -> {
            Icons.Default.MiscellaneousServices
        }
    }
    val formattedDate = getDateStringFromMillis(date)
    val costTag = "${costOptions[type]} • $formattedDate"
    Surface(
        modifier = modifier
            .clip(shape)
            .clickable(onClick = onCostClick),
        shape = shape,
        shadowElevation = 4.dp,
        border = BorderStroke(width = 1.dp, color = Color.Black)
    ) {
        Row(
            modifier = Modifier.padding(all = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(imageVector = icon, contentDescription = null)
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(text = title.ifEmpty { "${costOptions[type]} - ${stringResource(R.string.expense_title)}" })
                Text(text = costTag)
            }
            Text(text = CurrencyUtils.formatCurrency(price))
            IconButton(onClick = onDeleteButtonClick) {
                Icon(imageVector = Icons.Default.Delete, contentDescription = null)
            }
        }
    }
}

@Preview
@Composable
fun CostEntryPreview() {
    CostEntry()

}