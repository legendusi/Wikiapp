package com.example.wikipediaapp.model

import com.google.gson.annotations.SerializedName

data class WikiResponse(
    @SerializedName("query")
    val query: Query
)

data class Query(
    @SerializedName("pages")
    val pages: Map<String, WikiPage>
)

data class WikiPage(
    @SerializedName("title")
    val title: String,
    @SerializedName("extract")
    val extract: String,
    @SerializedName("thumbnail")
    val thumbnail: Thumbnail?
)

data class Thumbnail(
    @SerializedName("source")
    val source: String
)
