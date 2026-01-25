package com.andlife.undo.state

import com.andlife.undo.model.EditSnapshot

class EditHistory(private val maxHistorySize: Int = 10) {

    private val undoStack = ArrayDeque<EditSnapshot>()
    private val redoStack = ArrayDeque<EditSnapshot>()

    val canUndo: Boolean
        get() = undoStack.size > 1

    val canRedo: Boolean
        get() = redoStack.isNotEmpty()

    fun saveState(snapshot: EditSnapshot) {
        undoStack.addLast(snapshot)
        redoStack.clear()

        while (undoStack.size > maxHistorySize) {
            undoStack.removeFirst()
        }
    }

    fun undo(): EditSnapshot? {
        if (!canUndo) return null

        val current = undoStack.removeLast()
        redoStack.addLast(current)
        return undoStack.lastOrNull()
    }

    fun redo(): EditSnapshot? {
        if (!canRedo) return null

        val snapshot = redoStack.removeLast()
        undoStack.addLast(snapshot)
        return snapshot
    }

    fun clear() {
        undoStack.clear()
        redoStack.clear()
    }

    fun initialize(snapshot: EditSnapshot) {
        clear()
        undoStack.addLast(snapshot)
    }
}
