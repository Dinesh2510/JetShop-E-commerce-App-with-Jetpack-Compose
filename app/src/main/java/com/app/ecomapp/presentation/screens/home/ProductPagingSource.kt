package com.app.ecomapp.presentation.screens.home
import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.app.ecomapp.data.models.home.ProductData
import com.app.ecomapp.data.remote.ApiService
import kotlinx.coroutines.delay
import java.io.IOException

class ProductPagingSource(
    private val apiService: ApiService
) : PagingSource<Int, ProductData>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ProductData> {
        return try {
            val currentPage = params.key ?: 1
            Log.d("ProductPagingSource", "Loading page: $currentPage") // Debug log
            // Add a delay to simulate loading effect
            delay(2000) // 2 sec seconds delay
           /* // Simulate an API failure for page 2
            if (currentPage == 2) {
                throw IOException("Simulated network error")
            }*/

            val response = apiService.getAllProducts(currentPage)

            if (response == null || response.products.isNullOrEmpty()) {
                Log.e("ProductPagingSource", "Null or empty response for page: $currentPage")
                return LoadResult.Page(
                    data = emptyList(),
                    prevKey = if (currentPage == 1) null else currentPage - 1,
                    nextKey = null
                )
            }

            Log.d("ProductPagingSource", "Loaded ${response.products.size} products")

            LoadResult.Page(
                data = response.products ?: emptyList(),
                prevKey = if (currentPage == 1) null else currentPage - 1,
                nextKey = if (currentPage < response.total_pages) currentPage + 1 else null
            )
        } catch (e: Exception) {
            Log.e("ProductPagingSource", "Error loading products", e)
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, ProductData>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
}
