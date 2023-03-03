package com.example.gnirehtetrestart

import android.Manifest
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.PermissionChecker
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity() {

    private companion object CONSTANTS {
        private const val GNIREHTET_NAME = "Gnirehtet"
        private const val GNIREHTET_PACKAGE_NAME = "com.genymobile.gnirehtet"
        private val CUSTOM_DNS_SERVERS = arrayOf("76.76.2.2", "2606:1a40::2")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val infoRestarted = TextView(this).apply {
            text = ""
            maxLines = 100
        }

        val buttonRestart = Button(this).apply {
            text = "Restart"
            setOnClickListener {
                gnirehtetRestart(context, CUSTOM_DNS_SERVERS, infoRestarted)
            }
        }

        val buttonRestartNoDns = Button(this).apply {
            text = "Restart without custom DNS"
            setOnClickListener {
                gnirehtetRestart(context, null, infoRestarted)
            }
        }

        val buttonSettings = Button(this).apply {
            text = "Open Gnirehtet in Settings"
            setOnClickListener {
                context.startActivity(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = Uri.parse("package:$GNIREHTET_PACKAGE_NAME")
                })
            }
        }

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            addView(buttonRestart)
            addView(buttonRestartNoDns)
            addView(buttonSettings)
            addView(infoRestarted)
        }

        setContentView(layout)
    }

    private fun gnirehtetRestart(context: Context, dnsServers: Array<String>?, targetInfo: TextView) {
        targetInfo.text = ""

        if (!isAppInstalled(context, GNIREHTET_PACKAGE_NAME)) {
            Toast.makeText(context, "$GNIREHTET_NAME is not installed.", Toast.LENGTH_SHORT).show()
            return
        }

        if (!isAppEnabled(context, GNIREHTET_PACKAGE_NAME)) {
            Toast.makeText(context, "$GNIREHTET_NAME is not enabled.", Toast.LENGTH_SHORT).show()
            return
        }

        if (!checkPermission(context, Manifest.permission.WRITE_SECURE_SETTINGS)) {
            Toast.makeText(context, "Permission WRITE_SECURE_SETTINGS not granted.", Toast.LENGTH_SHORT).show()
            return
        }

        context.startActivity(Intent("com.genymobile.gnirehtet.STOP").apply {
            component = ComponentName("com.genymobile.gnirehtet", "com.genymobile.gnirehtet.GnirehtetActivity")
        })

        context.startActivity(Intent("com.genymobile.gnirehtet.START").apply {
            component = ComponentName("com.genymobile.gnirehtet", "com.genymobile.gnirehtet.GnirehtetActivity")

            if (dnsServers != null) {
                putExtras(Bundle().apply {
                    putStringArray("dnsServers", dnsServers)
                })
            }
        })

        updateInfoText(targetInfo, "$GNIREHTET_NAME restarted")
    }

    private fun updateInfoText(target: TextView, text: String) {
        val currentDateAndTime = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
        val line = "[${currentDateAndTime}] $text"

        if (target.text.isEmpty()) {
            target.text = line
        } else {
            target.text = "${target.text}\n$line"
        }
    }

    private fun isAppInstalled(context: Context, packageName: String): Boolean {
        return try {
            context.packageManager.getPackageInfo(packageName, PackageManager.GET_META_DATA)
            true
        } catch (ignored: PackageManager.NameNotFoundException) {
            false
        }
    }

    private fun isAppEnabled(context: Context, packageName: String): Boolean {
        return try {
            val applicationInfo = context.packageManager.getApplicationInfo(packageName, 0)
            applicationInfo.enabled
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }

    fun checkPermission(context: Context, permissionName: String): Boolean {
        return if (Build.VERSION.SDK_INT >= 23) {
            val granted = context.checkSelfPermission(permissionName)
            granted == PackageManager.PERMISSION_GRANTED
        } else {
            val granted = PermissionChecker.checkSelfPermission(context, permissionName)
            granted == PermissionChecker.PERMISSION_GRANTED
        }
    }

}
