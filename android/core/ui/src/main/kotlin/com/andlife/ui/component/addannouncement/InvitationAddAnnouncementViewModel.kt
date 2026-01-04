package com.andlife.ui.component.addannouncement

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AddAnnouncementViewModel : ViewModel() {
    private val _announcementDraft = MutableStateFlow(AnnouncementDraft())
    val announcementDraft: StateFlow<AnnouncementDraft> = _announcementDraft.asStateFlow()

    fun updateTitle(title: String) {
        _announcementDraft.value =
            _announcementDraft.value.copy(title = title)
    }

    fun updateContent(content: String) {
        _announcementDraft.value =
            _announcementDraft.value.copy(content = content)
    }

    fun clearDraft() {
        _announcementDraft.value = AnnouncementDraft()
    }
}
