package com.example.cateringapp.ui.admin.home

import androidx.lifecycle.ViewModel
import com.example.cateringapp.core.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AdminHomeViewModel @Inject constructor(
    private val prefsRepos: UserPreferencesRepository,
) : ViewModel() {


    suspend fun getSelectedBusiness(): Int? {
        return prefsRepos.getSelectedBusiness()
    }
}