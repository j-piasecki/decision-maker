package io.github.breskin.decisionmaker

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun DecisionMaker() {
    Column() {
        RecordButton(
            Modifier
                .width(128.dp)
                .height(128.dp))
    }
}