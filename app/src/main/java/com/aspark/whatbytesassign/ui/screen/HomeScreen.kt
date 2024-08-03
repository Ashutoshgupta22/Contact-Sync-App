package com.aspark.whatbytesassign.ui.screen

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.ViewModelInitializer
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.viewModelFactory
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

    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Button(
            onClick = {homeViewModel.syncContacts()},
        ) {
            Text(text = "Sync Contact")
        }
        Text(
            text = "Add all new contacts",
            fontSize = 14.sp
        )

        ShowProgress(uiState)

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
fun ShowProgress(uiState: UiState<SyncProgress>) {

    when(uiState) {
        is UiState.Success -> {
            LinearProgressIndicator(progress = { uiState.data.progress })
        }
        else ->{}
    }
}

@Preview(apiLevel = 33)
@Composable
private fun PreviewHomeScreen() {
    WhatBytesAssignTheme {
        HomeScreen()
    }
}