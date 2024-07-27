package userinterface.composables

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

@Composable
fun MessagePrompt(message: String, onDismiss: () -> Unit, usage: String) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        )
    ) {
        // Container for the message and close button
        Surface(
            modifier = Modifier.padding(8.dp),
            shape = MaterialTheme.shapes.small,
            elevation = 4.dp
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                when (usage) {
                    "error" -> Text(
                        text = message,
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.body2
                    )

                    "message" -> Text(
                        text = message,
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.body1
                    )

                    else -> assert(false)
                }
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Filled.Close, contentDescription = "Close")
                }
            }
        }
    }
}
