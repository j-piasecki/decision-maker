package io.github.breskin.decisionmaker

import android.content.res.Configuration.UI_MODE_NIGHT_MASK
import android.content.res.Configuration.UI_MODE_NIGHT_NO
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.breskin.decisionmaker.ui.theme.DecisionMakerTheme

@Composable
fun RecordButton(modifier: Modifier = Modifier, animationProgress: Float = 0f, onClick: () -> Unit = {}) {
    BoxWithConstraints(modifier, contentAlignment = Alignment.Center) {
        val width = maxWidth / 2
        val height = maxHeight / 2

        Box(
            Modifier
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.5f))
                .width(width + width * animationProgress)
                .height(height + height * animationProgress)
        ) {}

        Box(
            Modifier
                .clip(CircleShape)
                .clickable { onClick() }
                .background(MaterialTheme.colorScheme.primary)
                .width(width)
                .height(height),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_mic_24),
                contentDescription = "Recorder icon",
                tint = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.fillMaxSize(0.5f)
            )
        }
    }
}

@Preview(
    name = "Dark theme",
    uiMode = UI_MODE_NIGHT_YES,
    showBackground = true,
)
@Preview(
    name = "Light theme",
    uiMode = UI_MODE_NIGHT_NO,
    showBackground = true
)
@Composable
fun RecordButtonPreview() {
    Surface(color = MaterialTheme.colorScheme.background) {
        RecordButton(
            Modifier
                .background(Color.White)
                .width(128.dp)
                .height(128.dp),
        )
    }
}

@Preview(
    name = "Expanded",
    uiMode = UI_MODE_NIGHT_NO,
    showBackground = true
)
@Composable
fun RecordButtonPreviewExpanded() {
    DecisionMakerTheme() {
        Surface(color = MaterialTheme.colorScheme.background) {
            RecordButton(
                Modifier
                    .background(Color.White)
                    .width(128.dp)
                    .height(128.dp),
                animationProgress = 1f
            )
        }
    }
}