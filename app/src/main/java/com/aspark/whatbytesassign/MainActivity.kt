package com.aspark.whatbytesassign

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.aspark.whatbytesassign.ui.screen.HomeScreen
import com.aspark.whatbytesassign.ui.theme.WhatBytesAssignTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WhatBytesAssignTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->

                    PermissionHandler(
                        permissions = arrayOf(
                            Manifest.permission.READ_CONTACTS,
                            Manifest.permission.WRITE_CONTACTS
                        ),
                        rationale = "Contact permissions are needed to sync contacts.",
                        onPermissionResult = { granted ->
                            if (granted) {
                                Log.i("MainActivity", "onCreate: Permission Granted")
                            } else {
                                Log.i("MainActivity", "onCreate: Permission Denied")

                            }
                        }
                    ) {
                        HomeScreen(modifier = Modifier.padding(innerPadding))
                    }
                }
            }
        }
    }
}

@Composable
fun PermissionHandler(
    permissions: Array<String>,
    rationale: String,
    onPermissionResult: (Boolean) -> Unit,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    var showRationale by remember { mutableStateOf(false) }
    var permissionsGranted by remember { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissionsMap ->
        val allGranted = permissionsMap.values.all { it }
        permissionsGranted = allGranted
        onPermissionResult(allGranted)
    }

    fun checkPermissions(): Boolean {
        return permissions.all {
            ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
        }
    }

    fun checkAndRequestPermissions() {
        permissionsGranted = checkPermissions()
        if (!permissionsGranted) {
            showRationale = permissions.any {
                ActivityCompat.shouldShowRequestPermissionRationale(context as Activity, it)
            }
            if (!showRationale) {
                permissionLauncher.launch(permissions)
            }
        }
    }

    DisposableEffect(permissions) {
        if (!checkPermissions()) {
            checkAndRequestPermissions()
        }
        onDispose { }
    }

    if (permissionsGranted) {
        Log.i("MainActivity", "PermissionHandler: granted")
        content()
    } else {
        if (showRationale) {
            AlertDialog(
                onDismissRequest = { showRationale = false },
                title = { Text("Permission Required") },
                text = { Text(rationale) },
                confirmButton = {
                    Button(onClick = {
                        showRationale = false
                        permissionLauncher.launch(permissions)
                    }) {
                        Text("OK")
                    }
                },
                dismissButton = {
                    Button(onClick = { showRationale = false }) {
                        Text("Cancel")
                    }
                }
            )
        } else {
            // Show a placeholder or loading indicator while waiting for permissions
            Text("Checking permissions...")
        }
    }
}


@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    WhatBytesAssignTheme {
        HomeScreen()
    }
}