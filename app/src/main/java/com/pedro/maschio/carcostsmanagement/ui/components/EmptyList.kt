package com.pedro.maschio.carcostsmanagement.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun EmptyList(
    modifier: Modifier = Modifier,
    message: String = "There is no car costs added yet! Click on Add Cost to start tracking your car expenses!"
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Icon(
            modifier = Modifier.padding(bottom = 32.dp),
            imageVector = Icons.Default.Warning,
            contentDescription = null
        )
        Text(
            text = message
        )
    }
}

@Preview
@Composable
fun EmptyCostsPreview() {
    EmptyList()
}