package com.aspark.whatbytesassign.network

import com.aspark.whatbytesassign.model.Contact
import retrofit2.Response
import retrofit2.http.GET

interface ApiService {

    @GET("4ca3-9aec-4e58-b627")
    suspend fun getTodayContacts(): Response<List<Contact>>
}