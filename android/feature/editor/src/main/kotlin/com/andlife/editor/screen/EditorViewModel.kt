package com.andlife.editor.screen

import androidx.lifecycle.ViewModel
import com.andlife.editor.state.EditorState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class EditorViewModel @Inject constructor(
     val editorState: EditorState,
): ViewModel()
