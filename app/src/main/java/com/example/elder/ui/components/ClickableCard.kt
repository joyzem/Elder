package com.example.elder.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.elder.ui.theme.ElderTheme

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ClickableCard(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    content: @Composable (() -> Unit)
) {
    Card(
        shape = MaterialTheme.shapes.small,
        onClick = { onClick() },
        modifier = modifier
            .fillMaxWidth()
            .wrapContentSize(Alignment.TopStart),
        elevation = 4.dp
    ) {
        content()
    }
}

@Preview
@Composable
private fun PreviewClickableCard() {
    ElderTheme {
        ClickableCard(onClick = {  }) {
            Text(text = "Hello, World!", modifier = Modifier.padding(16.dp))
        }
    }
}