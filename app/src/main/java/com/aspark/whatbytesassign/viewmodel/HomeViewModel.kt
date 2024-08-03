package com.aspark.whatbytesassign.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aspark.whatbytesassign.MyApplication
import com.aspark.whatbytesassign.model.Contact
import com.aspark.whatbytesassign.repository.Repository
import com.aspark.whatbytesassign.ui.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {

    private val repo = Repository(MyApplication.instance)
    private val _uiState = MutableStateFlow<UiState<SyncProgress>>(UiState.Idle)
    val uiState: StateFlow<UiState<SyncProgress>> = _uiState

    fun syncContacts() {
        viewModelScope.launch {
            _uiState.value = UiState.Success(SyncProgress(0, 0))

            Log.i("ViewModel", "syncContacts: called")
            repo.getTodayContacts()
                .collect { state ->
                    when (state) {
                        is UiState.Success -> addContactsToDevice(state.data)
                        else -> {}
                    }
                }
        }
    }

    private suspend fun addContactsToDevice(contacts: List<Contact>) {
        val totalContacts = contacts.size
        var syncedContacts = 0

        Log.i("HomeViewModel", "addContactsToDevice: $contacts")

        contacts.forEach { contact ->
            if (repo.addContactToDevice(contact)) {
                syncedContacts++
                if (syncedContacts == totalContacts) {
                    _uiState.value = UiState.Complete
                    return
                }
                _uiState.value = UiState.Success(SyncProgress(syncedContacts, totalContacts))
            }
        }
    }
}

data class SyncProgress(
    private val synced: Int,
    private val total: Int
) {
    val progress: Float
        get() = if (total > 0) (synced.toFloat() / total * 100) else 0f
}
