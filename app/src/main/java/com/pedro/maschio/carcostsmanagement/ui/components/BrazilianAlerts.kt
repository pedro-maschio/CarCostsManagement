package com.pedro.maschio.carcostsmanagement.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.LocalGasStation
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun FlexAlert(
    ethanolPrice: Double,
    gasolinePrice: Double,
    modifier: Modifier = Modifier
) {
    if (ethanolPrice > 0 && gasolinePrice > 0) {
        val ratio = ethanolPrice / gasolinePrice
        if (ratio <= 0.7) {
            AlertCard(
                modifier = modifier,
                title = "Abasteça com Etanol!",
                description = "O etanol está custando ${(ratio * 100).toInt()}% do preço da gasolina, o que é vantajoso (limite de 70%).",
                color = Color(0xFF4CAF50)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun FlexAlertPreview() {
    FlexAlert(ethanolPrice = 3.50, gasolinePrice = 5.50)
}

@Composable
fun MaintenanceAlert(
    message: String,
    onActionClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    AlertCard(
        modifier = modifier,
        title = "Aviso de Manutenção",
        description = message,
        color = Color(0xFFFF9800),
        icon = Icons.Default.Build,
        actionLabel = "Confirmar Troca",
        onActionClick = onActionClick
    )
}

@Preview(showBackground = true)
@Composable
fun MaintenanceAlertPreview() {
    MaintenanceAlert(message = "Troca de óleo em 300 km", onActionClick = {})
}

@Composable
fun AlertCard(
    title: String,
    description: String,
    color: Color,
    modifier: Modifier = Modifier,
    icon: androidx.compose.ui.graphics.vector.ImageVector = Icons.Default.LocalGasStation,
    actionLabel: String? = null,
    onActionClick: (() -> Unit)? = null
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(8.dp),
        color = color.copy(alpha = 0.1f),
        border = BorderStroke(1.dp, color)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = color)
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        color = color
                    )
                    Text(
                        text = description,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
            if (actionLabel != null && onActionClick != null) {
                TextButton(
                    onClick = onActionClick,
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text(text = actionLabel, color = color)
                }
            }
        }
    }
}
