package com.andlife.ui.scope

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.savedstate.SavedStateRegistryOwner
import androidx.savedstate.compose.LocalSavedStateRegistryOwner
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
@Composable
fun DetailPaneScopedViewModel(
    scopedId: String = rememberSaveable { Uuid.random().toString() },
    content: @Composable () -> Unit
) {
    val registry = viewModel<ScopedStoreRegistryViewModel>()
    val savedStateRegistryOwner = LocalSavedStateRegistryOwner.current
    val viewModelStore = remember(scopedId) { registry.getOrCreate(scopedId) }

    val owner = remember(viewModelStore) {
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


    DisposableEffect(scopedId) {
        onDispose {
            registry.clear(scopedId)
        }
    }

    CompositionLocalProvider(LocalViewModelStoreOwner provides owner) {
        content()
    }
}

