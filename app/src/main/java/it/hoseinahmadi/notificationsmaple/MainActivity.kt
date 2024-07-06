package it.hoseinahmadi.notificationsmaple

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import it.hoseinahmadi.notificationsmaple.ui.theme.NotificationSmapleTheme
import java.util.Random
import java.util.UUID

class MainActivity : ComponentActivity() {
    private val channelId = "test_chanel_id"
    private lateinit var builder: NotificationCompat.Builder
    private lateinit var notificationManager: NotificationManagerCompat

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NotificationSmapleTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        notificationManager = NotificationManagerCompat.from(this@MainActivity)
                        val notificationId = Random(System.currentTimeMillis()).nextInt()

                        val notificationRequestPermission = rememberLauncherForActivityResult(
                            contract = ActivityResultContracts.RequestPermission()
                        ) {
                            if (it) {
                                notificationManager.notify(notificationId, builder.build())
                            } else {
                                Toast.makeText(
                                    this@MainActivity,
                                    "not permission",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                        createNotificationChannel()

                        val myNotifIntent = Intent(this@MainActivity, MainActivity::class.java)
                        myNotifIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

                        val pendingIntent =
                            PendingIntent.getActivity(
                                this@MainActivity,
                                0,
                                myNotifIntent,
                                PendingIntent.FLAG_IMMUTABLE
                            )

                        builder = NotificationCompat.Builder(this@MainActivity, channelId)
                            .setSmallIcon(android.R.drawable.ic_dialog_info)
                            .setContentTitle("You have a new message")
                            .setContentText("This is a test message")
                            .setShowWhen(true)
                            .setAutoCancel(true)
                            .setContentIntent(pendingIntent)
                            .setPriority(NotificationCompat.PRIORITY_HIGH)
                            .setDefaults(NotificationCompat.DEFAULT_ALL)
                            .setFullScreenIntent(pendingIntent, true) // نمایش نوتیفیکیشن به صورت heads-up

                        Button(onClick = {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
                                    notificationManager.notify(notificationId, builder.build())
                                } else {
                                    notificationRequestPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
                                }
                            } else {
                                notificationManager.notify(notificationId, builder.build())
                            }
                        }) {
                            Text(text = "Notify")
                        }
                    }
                }
            }
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "test_chanel_name"
            val importance = NotificationManager.IMPORTANCE_HIGH // اهمیت بالا
            val channel = NotificationChannel(channelId, name, importance).apply {
                description = "My Channel Description"
                enableLights(true)
                lightColor = Color.RED
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 1000, 500, 1000) // الگوی ویبره
            }
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }
}

