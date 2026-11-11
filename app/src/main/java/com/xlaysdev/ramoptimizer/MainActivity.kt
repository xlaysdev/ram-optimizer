package com.xlaysdev.ramoptimizer

import android.app.ActivityManager
import android.content.Context
import android.os.Bundle
import android.os.StatFs
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.dp
import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                RamOptimizerScreen(context = this)
            }
        }
    }
}

private data class DeviceStats(
    val totalRam: Long,
    val availableRam: Long,
    val totalStorage: Long,
    val freeStorage: Long
) {
    val usedRam: Long get() = (totalRam - availableRam).coerceAtLeast(0)
    val ramUsage: Float
        get() = if (totalRam > 0) usedRam.toFloat() / totalRam else 0f
    val usedStorage: Long get() = (totalStorage - freeStorage).coerceAtLeast(0)
    val storageUsage: Float
        get() = if (totalStorage > 0) usedStorage.toFloat() / totalStorage else 0f
}

private fun readDeviceStats(context: Context): DeviceStats {
    val manager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
    val memory = ActivityManager.MemoryInfo().also(manager::getMemoryInfo)
    val storage = StatFs(context.filesDir.absolutePath)

    return DeviceStats(
        totalRam = memory.totalMem,
        availableRam = memory.availMem,
        totalStorage = storage.totalBytes,
        freeStorage = storage.availableBytes
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RamOptimizerScreen(context: Context) {
    var stats by remember { mutableStateOf(readDeviceStats(context)) }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("RAM Optimizer") })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            Text(
                text = "Device overview",
                style = MaterialTheme.typography.headlineSmall
            )
            Text(
                text = "Live system information from your Android device.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            StatCard(
                title = "RAM usage",
                icon = {
                    Icon(Icons.Default.Memory, contentDescription = null)
                },
                used = stats.usedRam,
                total = stats.totalRam,
                fraction = stats.ramUsage,
                detail = "${formatBytes(stats.availableRam)} available"
            )

            StatCard(
                title = "Internal storage",
                icon = {
                    Icon(Icons.Default.Storage, contentDescription = null)
                },
                used = stats.usedStorage,
                total = stats.totalStorage,
                fraction = stats.storageUsage,
                detail = "${formatBytes(stats.freeStorage)} free"
            )

            Spacer(modifier = Modifier.height(4.dp))

            Button(
                onClick = { stats = readDeviceStats(context) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Refresh, contentDescription = null)
                Text(
                    text = "Refresh stats",
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            Text(
                text = "Android manages app memory automatically. This app monitors usage; it does not force-stop apps or promise fake RAM boosts.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun StatCard(
    title: String,
    icon: @Composable () -> Unit,
    used: Long,
    total: Long,
    fraction: Float,
    detail: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    icon()
                    Text(title, style = MaterialTheme.typography.titleMedium)
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "${formatBytes(used)} / ${formatBytes(total)}",
                    style = MaterialTheme.typography.titleLarge
                )
                Text(
                    text = detail,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            CircularProgressIndicator(
                progress = { fraction.coerceIn(0f, 1f) },
                strokeWidth = 8.dp,
                strokeCap = StrokeCap.Round
            )
        }
    }
}

private fun formatBytes(bytes: Long): String {
    if (bytes < 1024) return "$bytes B"
    val units = arrayOf("KB", "MB", "GB", "TB")
    var value = bytes.toDouble()
    var unit = -1
    do {
        value /= 1024.0
        unit++
    } while (value >= 1024.0 && unit < units.lastIndex)
    return String.format(Locale.getDefault(), "%.1f %s", value, units[unit])
}
