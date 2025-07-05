package com.app.ecomapp.presentation.screens.profile.referral

import android.content.Context
import android.provider.ContactsContract
import androidx.paging.PagingSource
import androidx.paging.PagingState

class ContactsPagingSource(
    private val context: Context
) : PagingSource<Int, ContactUiModel>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ContactUiModel> {
        val contacts = mutableListOf<ContactUiModel>()
        val contentResolver = context.contentResolver

        val projection = arrayOf(
            ContactsContract.Contacts._ID,
            ContactsContract.Contacts.DISPLAY_NAME,
            ContactsContract.Contacts.PHOTO_URI,
            ContactsContract.CommonDataKinds.Phone.NUMBER
        )

        val cursor = contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            projection,
            null,
            null,
            "${ContactsContract.Contacts.DISPLAY_NAME} ASC"
        )

        cursor?.use {
            while (it.moveToNext()) {
                val id = it.getLong(it.getColumnIndexOrThrow(ContactsContract.Contacts._ID))
                val name = it.getString(it.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME)) ?: ""
                val photoUri = it.getString(it.getColumnIndexOrThrow(ContactsContract.Contacts.PHOTO_URI))
                val number = it.getString(it.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER)) ?: ""

                val (first, last) = name.split(" ").let {
                    Pair(it.firstOrNull().orEmpty(), it.getOrNull(1).orEmpty())
                }

                contacts.add(ContactUiModel(id, first, last, number, photoUri))
            }
        }

        return LoadResult.Page(
            data = contacts,
            prevKey = null,
            nextKey = null
        )
    }

    override fun getRefreshKey(state: PagingState<Int, ContactUiModel>): Int? = null
}
