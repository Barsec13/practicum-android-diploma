package ru.practicum.android.diploma.search.data.dto

data class SearchResponse(
    val items: List<VacancyDto>,
    val found: Int,
    val page: Int,
    val pages: Int,
    val per_page: Int,
) : Response()