package com.pedro.maschio.carcostsmanagement.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun CostsHeader(
    modifier: Modifier = Modifier,
    totalCosts: Double = 2500.0,
    totalMileage: Int = 15000
) {
    Row(modifier = modifier.padding(16.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        CostsCard(title = "Total", value = "R$ $totalCosts")
        CostsCard(title = "Quilometragem atual", value = "$totalMileage km")
    }

}

@Preview
@Composable
fun CostsHeaderPreview(modifier: Modifier = Modifier) {
    CostsHeader()
}

@Composable
fun CostsCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    shape: RoundedCornerShape = RoundedCornerShape(size = 8.dp)
) {
    Surface(
        modifier = modifier.clip(shape),
        shape = shape,
        shadowElevation = 4.dp,
        border = BorderStroke(width = 1.dp, color = Color.Black)
    ) {
        Column(modifier = Modifier.padding(all = 8.dp), verticalArrangement = Arrangement.Center) {
            Row(
                modifier = Modifier.padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(imageVector = Icons.AutoMirrored.Default.TrendingUp, contentDescription = null)
                Text(text = title)
            }
            Row(horizontalArrangement = Arrangement.Start) {
                Text(text = value)
            }
        }
    }
}

@Preview
@Composable
fun CostsCardPreview(modifier: Modifier = Modifier) {
    CostsCard(title = "Este mês", value = "R$ 100,00")
}