package com.andlife.invitation_card.viewmodel

import androidx.lifecycle.ViewModel
import com.andlife.editor.state.EditorState
import com.andlife.editor.util.CreateCardSession
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CreateCardViewModel @Inject constructor(
    private val state: EditorState,
    private val createCardSession: CreateCardSession
) : ViewModel() {

    private var isLoaded = false

    fun getState(): EditorState {
        return state
    }

    fun loadEditable() {
        if (isLoaded) return
        val editable = createCardSession.editable ?: return
        state.setEditable(editable)
        state.setBackground(createCardSession.backgroundColor)
        isLoaded = true
    }

    fun saveCard() {
        createCardSession.save(
            state.editText?.text,
            state.currentTextStyle.backgroundColor,
            state.currentBackgroundImageUrl
        )
    }
}
