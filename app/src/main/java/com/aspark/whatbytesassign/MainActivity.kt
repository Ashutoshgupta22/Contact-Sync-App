package com.aspark.whatbytesassign

import android.Manifest
import android.app.Activity
import android.content.Context
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.window.DialogProperties
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.aspark.whatbytesassign.ui.screen.HomeScreen
import com.aspark.whatbytesassign.ui.theme.WhatBytesAssignTheme

@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WhatBytesAssignTheme {
                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = {
                                Text(text = title.toString())
                            },
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer
                            )
                        )
                    },
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->

                    val context = LocalContext.current
                    val permissions = arrayOf(
                        Manifest.permission.READ_CONTACTS,
                        Manifest.permission.WRITE_CONTACTS
                    )

                    if (checkPermissions(permissions, context))
                        HomeScreen(modifier = Modifier.padding(innerPadding))
                    else
                        PermissionHandler(
                            permissions = permissions,
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
        showRationale = true
    }

    fun checkAndRequestPermissions() {
        permissionsGranted = checkPermissions(permissions, context)
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
        if (!checkPermissions(permissions, context)) {
            checkAndRequestPermissions()
        }
        onDispose { }
    }

    if (permissionsGranted) {
        Log.i("MainActivity", "PermissionHandler: granted")
        content()
    } else {
        Log.i("MainActivity", "PermissionHandler: denied")

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
                    OutlinedButton(onClick = { showRationale = false }) {
                        Text("Cancel")
                    }
                },
                properties = DialogProperties(
                    dismissOnBackPress = false,
                    dismissOnClickOutside = false
                )
            )
        }
    }
}


fun checkPermissions(permissions: Array<String>, context: Context): Boolean {
    return permissions.all {
        ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
    }
}


@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    WhatBytesAssignTheme {
        HomeScreen()
    }
}