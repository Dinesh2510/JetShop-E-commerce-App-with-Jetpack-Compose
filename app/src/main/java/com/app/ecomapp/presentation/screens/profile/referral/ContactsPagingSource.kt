package com.app.ecomapp.presentation.screens.profile.referral

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.provider.ContactsContract
import androidx.core.content.ContextCompat
import androidx.paging.PagingSource
import androidx.paging.PagingState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ContactsPagingSource(
    private val context: Context
) : PagingSource<Int, ContactUiModel>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ContactUiModel> {
        val page = params.key ?: 0
        val pageSize = params.loadSize

        // Return empty page immediately if permission is not granted yet
        val hasPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.READ_CONTACTS
        ) == PackageManager.PERMISSION_GRANTED

        if (!hasPermission) {
            return LoadResult.Page(
                data = emptyList(),
                prevKey = null,
                nextKey = null
            )
        }

        return withContext(Dispatchers.IO) {
            try {
                val contacts = mutableListOf<ContactUiModel>()
                val seenNumbers = mutableSetOf<String>()
                val contentResolver = context.contentResolver

                val projection = arrayOf(
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
                    ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                    ContactsContract.CommonDataKinds.Phone.PHOTO_URI,
                    ContactsContract.CommonDataKinds.Phone.NUMBER
                )

                val cursor = contentResolver.query(
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    projection,
                    "${ContactsContract.CommonDataKinds.Phone.NUMBER} IS NOT NULL AND ${ContactsContract.CommonDataKinds.Phone.NUMBER} != ''",
                    null,
                    "${ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME} ASC"
                )

                cursor?.use {
                    val idCol = it.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.CONTACT_ID)
                    val nameCol = it.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
                    val photoCol = it.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.PHOTO_URI)
                    val numberCol = it.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER)

                    var currentIndex = 0
                    val startIndex = page * pageSize
                    val endIndex = startIndex + pageSize

                    while (it.moveToNext()) {
                        val rawNumber = it.getString(numberCol) ?: ""
                        val normalizedNumber = rawNumber.filter { char -> char.isDigit() }

                        // Deduplicate identical numbers
                        if (normalizedNumber.length >= 7 && seenNumbers.add(normalizedNumber)) {
                            if (currentIndex in startIndex until endIndex) {
                                val id = it.getLong(idCol)
                                val name = it.getString(nameCol) ?: "Unknown"
                                val photoUri = it.getString(photoCol)

                                val nameParts = name.trim().split(" ")
                                val first = nameParts.firstOrNull().orEmpty()
                                val last = if (nameParts.size > 1) nameParts.drop(1).joinToString(" ") else ""

                                contacts.add(
                                    ContactUiModel(
                                        id = id,
                                        firstName = first,
                                        lastName = last,
                                        number = rawNumber.trim(),
                                        photoUri = photoUri
                                    )
                                )
                            }
                            currentIndex++
                        }
                    }
                }

                val nextKey = if (contacts.size < pageSize) null else page + 1
                val prevKey = if (page == 0) null else page - 1

                LoadResult.Page(
                    data = contacts,
                    prevKey = prevKey,
                    nextKey = nextKey
                )
            } catch (e: Exception) {
                LoadResult.Error(e)
            }
        }
    }

    override fun getRefreshKey(state: PagingState<Int, ContactUiModel>): Int? {
        return state.anchorPosition?.let { anchor ->
            val anchorPage = state.closestPageToPosition(anchor)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }
}