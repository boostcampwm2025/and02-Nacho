package com.andlife.designsystem.component.timepicker

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun test() {
    val state = rememberTimePickerState(
        initialHour = 10,
        initialMinute = 30,
    )
}

interface InvitationTimePickerState {

    var hour: Int
    var minute: Int
    val minuteInterval: Int
    var isPm: Boolean
}

val InvitationTimePickerState.hour12: Int
    get() = when (val h = hour % 12) {
        0 -> 12
        else -> h
    }

private class InvitationTimePickerStateImpl(
    initialHour: Int,
    initialMinute: Int,
    override val minuteInterval: Int
) : InvitationTimePickerState {

    init {
        require(initialHour in 0..23) { "initialHour should be in [0..23] range" }
        require(initialMinute in 0..59) { "initialMinute should be in [0..59] range" }
        require(minuteInterval > 0 && 60 % minuteInterval == 0) {
            "minuteInterval should be a divisor of 60"
        }
    }

    private val _hour = mutableIntStateOf(initialHour)
    private val _minute = mutableIntStateOf(initialMinute)

    override var hour: Int
        get() = _hour.intValue
        set(value) {
            _hour.intValue = value
        }

    override var minute: Int
        get() = _minute.intValue
        set(value) {
            _minute.intValue = value
        }

    override var isPm: Boolean
        get() = hour >= 12
        set(value) {
            hour = if (value) {
                if (hour < 12) hour + 12 else hour
            } else {
                if (hour >= 12) hour - 12 else hour
            }
        }

    companion object {
        fun Saver() = Saver<InvitationTimePickerStateImpl, List<Int>>(
            save = { listOf(it.hour, it.minute, it.minuteInterval) },
            restore = {
                InvitationTimePickerStateImpl(
                    initialHour = it[0],
                    initialMinute = it[1],
                    minuteInterval = it[2]
                )
            }
        )
    }
}

@Composable
fun rememberInvitationTimePickerState(
    initialHour: Int = 0,
    initialMinute: Int = 0,
    minuteInterval: Int = 1
): InvitationTimePickerState {
    return rememberSaveable(saver = InvitationTimePickerStateImpl.Saver()) {
        InvitationTimePickerStateImpl(
            initialHour = initialHour,
            initialMinute = initialMinute,
            minuteInterval = minuteInterval
        )
    }
}