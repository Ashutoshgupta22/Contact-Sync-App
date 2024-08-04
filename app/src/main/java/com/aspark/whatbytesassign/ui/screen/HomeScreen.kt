package com.aspark.whatbytesassign.ui.screen

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aspark.whatbytesassign.ui.UiState
import com.aspark.whatbytesassign.ui.theme.WhatBytesAssignTheme
import com.aspark.whatbytesassign.viewmodel.HomeViewModel
import com.aspark.whatbytesassign.viewmodel.SyncProgress

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    homeViewModel: HomeViewModel = viewModel()
) {
    val uiState by homeViewModel.uiState.collectAsState()
    var description by remember { mutableStateOf("Add all new contacts") }

    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Button(
            onClick = homeViewModel::syncContacts,
            enabled = uiState !is UiState.Success,
        ) {
            Text(text = "Sync Contact")
        }
        Text(
            text = description,
            fontSize = 14.sp
        )

        Spacer(modifier = Modifier.height(8.dp))
        ShowProgress(uiState) {
            description = if (it) "Syncing..." else "Add all new contacts"
        }

        Spacer(modifier = Modifier.height(32.dp))
        Button(
            onClick = {},
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFF44336)
            )
        ) {
            Text(
                text = "Delete Contacts",
            )

        }
        Text(
            text = "Delete all contacts older than a month",
            fontSize = 14.sp
        )
    }
}

@Composable
fun ShowProgress(
    uiState: UiState<SyncProgress>,
    loading: (Boolean) -> Unit
) {
    when (uiState) {
        is UiState.Success -> {
            loading(true)
            Log.i("HomeScreen", "ShowProgress: ${uiState.data.progress}")
            LinearProgressIndicator(progress = { uiState.data.progress })
        }

        is UiState.Complete -> {
            loading(false)
            Toast.makeText(
                LocalContext.current, "All Contacts synced",
                Toast.LENGTH_SHORT
            ).show()
        }

        else -> {}
    }
}

@Preview(apiLevel = 33)
@Composable
private fun PreviewHomeScreen() {
    WhatBytesAssignTheme {
        HomeScreen()
    }
}