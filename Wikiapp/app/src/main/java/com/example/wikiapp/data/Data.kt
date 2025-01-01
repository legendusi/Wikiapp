package com.example.wikiapp.data

data class WikipediaResponse(
    val query: Query
)

data class Query(
    val search: List<SearchResult>
)

data class SearchResult(
    val title: String,
    val snippet: String
)