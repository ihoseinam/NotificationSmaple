package it.hoseinahmadi.notificationsmaple

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
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
    lateinit var buider:NotificationCompat.Builder
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
                        val notificationManager = NotificationManagerCompat.from(this@MainActivity)
                        val notificationId = Random(System.currentTimeMillis()).nextInt()

                        val notificationRequestPermission = rememberLauncherForActivityResult(
                            contract = ActivityResultContracts.RequestPermission()
                        ) {
                            if (it) {
                                notificationManager.notify(notificationId,buider.build())
                            } else {
                                Toast.makeText(
                                    this@MainActivity,
                                    "not permision",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                        createNotificationChannel()
                        val myNotifIntent =Intent(this@MainActivity,MainActivity::class.java)
                        myNotifIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
//                        val pendingIntent =PendingIntent.getActivities(this@MainActivity,0,myNotifIntent,PendingIntent.FLAG_IMMUTABLE)
                        Button(onClick = {
                            val builder =
                                NotificationCompat.Builder(this@MainActivity, channelId)
                                    .setSmallIcon(android.R.drawable.ic_dialog_info)
                                    .setContentTitle("you have new message")
                                    .setContentText("is message test")
                                    .setShowWhen(true)
                                    .setAutoCancel(true)
                                    .setPriority(NotificationCompat.PRIORITY_HIGH)


                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
                                ) {
                                    notificationManager.notify(notificationId, builder.build())
                                } else {
                                    notificationRequestPermission.launch(Manifest.permission.POST_NOTIFICATIONS)

                                }
                            } else {
                                notificationManager.notify(notificationId, builder.build())
                            }

                        }) {
                            Text(text = "notiiif")
                        }
                    }
                }
            }
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "test_chanel_name"
            val channel =
                NotificationChannel(channelId, name, NotificationManager.IMPORTANCE_HIGH)
            channel.description = "My Channel Description"
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }

    }
}

