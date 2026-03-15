package com.andlife.ui.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.lifecycle.HasDefaultViewModelProviderFactory
import androidx.lifecycle.SAVED_STATE_REGISTRY_OWNER_KEY
import androidx.lifecycle.SavedStateViewModelFactory
import androidx.lifecycle.VIEW_MODEL_STORE_OWNER_KEY
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.enableSavedStateHandles
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.MutableCreationExtras
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.savedstate.SavedStateRegistryOwner
import androidx.savedstate.compose.LocalSavedStateRegistryOwner

@Composable
fun DetailPaneViewModelScope(
    content: @Composable () -> Unit
) {
    val viewModelStore = remember { ViewModelStore() }
    val savedStateRegistryOwner = LocalSavedStateRegistryOwner.current

    val viewModelStoreOwner = remember(viewModelStore) {
        object : ViewModelStoreOwner, HasDefaultViewModelProviderFactory,
            SavedStateRegistryOwner by savedStateRegistryOwner {
            override val viewModelStore: ViewModelStore
                get() = viewModelStore
            override val defaultViewModelProviderFactory: ViewModelProvider.Factory
                get() = SavedStateViewModelFactory()
            override val defaultViewModelCreationExtras: CreationExtras
                get() =
                    MutableCreationExtras().also {
                        it[SAVED_STATE_REGISTRY_OWNER_KEY] = this
                        it[VIEW_MODEL_STORE_OWNER_KEY] = this
                    }
            init {
                enableSavedStateHandles()
            }
        }
    }

    DisposableEffect(viewModelStoreOwner) {
        onDispose {
            viewModelStore.clear()
        }
    }

    CompositionLocalProvider(LocalViewModelStoreOwner provides viewModelStoreOwner) {
        content()
    }
}

