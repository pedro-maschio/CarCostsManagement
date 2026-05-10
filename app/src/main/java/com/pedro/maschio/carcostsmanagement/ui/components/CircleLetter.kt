package com.pedro.maschio.carcostsmanagement.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.pedro.maschio.carcostsmanagement.ui.theme.primaryContainerLight

@Composable
fun CircleLetter(
    modifier: Modifier = Modifier,
    carName: String,
    isSelected: Boolean = false,
    onClickListener: () -> Unit
) {
    val title = if (isSelected) carName else (carName.firstOrNull() ?: "").toString()
    val fontWeight = if (isSelected) FontWeight(weight = 700) else FontWeight(weight = 400)
    val borderColor = if (isSelected) primaryContainerLight else Color.Black

    Box(
        modifier = modifier
            .defaultMinSize(
                minWidth = 48.dp, minHeight = 48.dp
            )
            .clip(CircleShape)
            .border(2.dp, color = borderColor, CircleShape)
            .clickable(onClick = onClickListener),
        contentAlignment = Alignment.Center
    ) {
        Text(
            modifier = Modifier.padding(all = 8.dp),
            text = title,
            fontWeight = fontWeight
        )
    }

}