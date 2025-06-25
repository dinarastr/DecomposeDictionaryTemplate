package ru.dinarastepina.ulchidictionary.data.paging

import app.cash.paging.PagingSource
import app.cash.paging.PagingState

abstract class BasePagingSource<T : Any>(
    private val searchQuery: String = ""
) : PagingSource<Int, T>() {

    override fun getRefreshKey(state: PagingState<Int, T>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, T> {
        return try {
            val page = params.key ?: 0
            val pageSize = params.loadSize

            val entries = if (searchQuery.isEmpty()) {
                loadAllItems(pageSize, page * pageSize)
            } else {
                searchItems(searchQuery, pageSize, page * pageSize)
            }
            
            LoadResult.Page(
                data = entries,
                prevKey = if (page == 0) null else page - 1,
                nextKey = if (entries.isEmpty()) null else page + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    protected abstract suspend fun loadAllItems(pageSize: Int, offset: Int): List<T>
    protected abstract suspend fun searchItems(query: String, pageSize: Int, offset: Int): List<T>
} 