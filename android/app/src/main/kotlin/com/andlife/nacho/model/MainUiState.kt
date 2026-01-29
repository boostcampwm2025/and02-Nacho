package com.andlife.nacho.model

import com.andlife.login.Login
import com.andlife.ui.base.BaseUiState
import kotlin.reflect.KClass

data class MainUiState(
    val isSplash: Boolean = true,
    val startDestination: KClass<*> = Login::class
) : BaseUiState
