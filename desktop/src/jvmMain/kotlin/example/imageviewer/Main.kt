package example.imageviewer

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import example.imageviewer.model.ContentState
import example.imageviewer.style.icAppRounded
import example.imageviewer.utils.getPreferredWindowSize
import example.imageviewer.view.AppUI
import example.imageviewer.view.RefreshView
import example.imageviewer.view.SplashUI

suspend fun main() {
    application {
        Window(
            onCloseRequest = ::exitApplication,
            title = "Refresh view",
            state = WindowState(
                position = WindowPosition.Aligned(Alignment.Center),
                size = getPreferredWindowSize(800, 1000)
            ),
        ) {
            MaterialTheme {
                RefreshView()
            }
        }
    }
}
