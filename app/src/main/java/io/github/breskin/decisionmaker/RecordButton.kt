package io.github.breskin.decisionmaker

import android.Manifest
import android.content.pm.PackageManager
import android.content.res.Configuration.UI_MODE_NIGHT_NO
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import io.github.breskin.decisionmaker.ui.theme.DecisionMakerTheme
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun RecordButton(
    modifier: Modifier = Modifier,
    animationProgress: Float = 0f,
    isActive: Boolean = false,
    onClick: () -> Unit = {}
) {
    val coroutineScope = rememberCoroutineScope()
    var visibleProgress by remember {
        mutableStateOf(0f)
    }

    var showPermissionModal by remember {
        mutableStateOf(false)
    }

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (granted) {
            onClick()
        } else {
            showPermissionModal = true
        }
    }

    // this may be bad
    DisposableEffect(animationProgress, isActive) {
        val task = coroutineScope.launch {
            val animProgress = animationProgress.coerceIn(0f, 0.8f)

            while (isActive) {
                visibleProgress += (animProgress - visibleProgress) * 0.1f
                delay(8)
            }
        }

        onDispose() {
            task.cancel()
        }
    }

    BoxWithConstraints(modifier, contentAlignment = Alignment.Center) {
        val width = maxWidth / 2
        val height = maxHeight / 2
        val context = LocalContext.current

        Box(
            Modifier
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.secondary.copy(alpha = 0.5f))
                .width(width + width * if (isActive) visibleProgress else 0f)
                .height(height + height * if (isActive) visibleProgress else 0f)
        ) {}

        Box(
            Modifier
                .clip(CircleShape)
                .clickable {
                    if (ContextCompat.checkSelfPermission(
                            context,
                            Manifest.permission.RECORD_AUDIO
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        if (!isActive) {
                            onClick()
                        }
                    } else {
                        launcher.launch(Manifest.permission.RECORD_AUDIO)
                    }
                }
                .background(MaterialTheme.colorScheme.secondary)
                .width(width)
                .height(height),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_mic_24),
                contentDescription = "Recorder icon",
                tint = MaterialTheme.colorScheme.onPrimary.copy(alpha = if (isActive) 1f else 0.7f),
                modifier = Modifier.fillMaxSize(0.5f)
            )
        }
    }

    if (showPermissionModal) {
        AlertDialog(
            onDismissRequest = { showPermissionModal = false },
            title = { Text(text = "Wymagane uprawnienie") },
            text = { Text(text = "Aplikacja wymaga uprawnienia do nagrywania dźwięku") },
            confirmButton = { Button(onClick = { showPermissionModal = false }) {
                Text(text = "OK")
            } },
        )
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
