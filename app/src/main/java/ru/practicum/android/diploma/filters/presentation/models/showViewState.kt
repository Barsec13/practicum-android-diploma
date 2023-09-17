package ru.practicum.android.diploma.filters.presentation.models

sealed interface ShowViewState{

    object showClearIcon:ShowViewState
    object hideClearIcon:ShowViewState


    object showApplyButton:ShowViewState
    object showClearAllButton:ShowViewState
    object hideClearAllButton:ShowViewState

}