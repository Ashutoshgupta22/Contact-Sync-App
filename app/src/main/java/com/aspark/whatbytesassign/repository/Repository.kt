package com.aspark.whatbytesassign.repository

import android.content.ContentProviderOperation
import android.content.Context
import android.provider.ContactsContract
import android.util.Log
import com.aspark.whatbytesassign.model.Contact
import com.aspark.whatbytesassign.network.ApiClient
import com.aspark.whatbytesassign.ui.UiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext

class Repository(
    private val context: Context
) {
    private val apiService = ApiClient.apiService

    fun getTodayContacts(): Flow<UiState<List<Contact>>> = flow {
        emit(UiState.Idle)

        try {
            val contacts = apiService.getTodayContacts().body()
            Log.i("Repo", "getTodayContacts: $contacts")
            if (contacts == null) {
                Log.i("Repo", "getTodayContacts: is null")

            }
            else emit(UiState.Success(contacts))

        } catch (e: Exception) {
            Log.e("Repo", "getTodayContacts: ",e )
            emit(UiState.Error(e))
        }
    }

     suspend fun addContactToDevice(contact: Contact): Boolean {
        return withContext(Dispatchers.IO) {
            val ops = ArrayList<ContentProviderOperation>()

            ops.add(ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
                .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
                .build())

            ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, contact.firstName)
                .build())

            ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, contact.phoneNumber)
                .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE)
                .build())

            try {
                context.contentResolver.applyBatch(ContactsContract.AUTHORITY, ops)
                true
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }
    }
}