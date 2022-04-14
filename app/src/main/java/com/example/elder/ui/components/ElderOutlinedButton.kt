package com.example.elder.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.elder.ui.theme.ElderTheme

@Composable
fun ElderOutlinedButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    content: @Composable () -> Unit
) {
    OutlinedButton(
        onClick = onClick,
        shape = CircleShape,
        modifier = modifier,
        border = null,
        elevation = ButtonDefaults.elevation(4.dp)
    ) {
        Box(modifier = Modifier.padding(4.dp)) { content() }
    }
}

@Preview
@Composable
fun PreviewElderOutlinedButton() {
    ElderTheme {
        ElderOutlinedButton(onClick = { /*TODO*/ }) {
            Text(text = "Hello, World!")
        }
    }
}