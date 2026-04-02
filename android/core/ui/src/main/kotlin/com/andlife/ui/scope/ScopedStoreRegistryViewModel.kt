package com.andlife.ui.scope

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelStore
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ScopedStoreRegistryViewModel @Inject constructor() : ViewModel() {
    private val stores = mutableMapOf<String, ViewModelStore>()

    fun getOrCreate(id: String): ViewModelStore =
        stores.getOrPut(id) { ViewModelStore() }

    fun clear(id: String) {
        stores.remove(id)?.clear()
    }

    override fun onCleared() {
        super.onCleared()
        stores.values.forEach { it.clear() }
        stores.clear()
    }
}
