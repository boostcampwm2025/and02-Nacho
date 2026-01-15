package com.andlife.designsystem.component.timepicker

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue

@Stable
interface NachoTimePickerState {
    var hour: Int // 0..23
    var minute: Int
    val minuteInterval: Int
    var isPm: Boolean
    val hour12: Int

    fun updateHour12(newHour12: Int)
}

private class NachoTimePickerStateImpl(
    initialHour: Int,
    initialMinute: Int,
    override val minuteInterval: Int,
) : NachoTimePickerState {
    private var _hour by mutableIntStateOf(initialHour)
    private var _minute by mutableIntStateOf(initialMinute)

    override var hour: Int
        get() = _hour
        set(value) {
            val newValue = value.coerceIn(0, 23)
            if (_hour != newValue) {
                _hour = newValue
            }
        }

    override var minute: Int
        get() = _minute
        set(value) {
            val newValue = value.coerceIn(0, 59)
            if (_minute != newValue) {
                _minute = newValue
            }
        }

    override var isPm: Boolean
        get() = hour >= 12
        set(value) {
            val newHour =
                when {
                    value && _hour < 12 -> _hour + 12
                    !value && _hour >= 12 -> _hour - 12
                    else -> _hour
                }

            if (_hour != newHour) {
                _hour = newHour
            }
        }

    override val hour12: Int
        get() =
            when (val h = hour % 12) {
                0 -> 12
                else -> h
            }

    override fun updateHour12(newHour12: Int) {
        hour =
            when {
                isPm && newHour12 != 12 -> newHour12 + 12
                !isPm && newHour12 == 12 -> 0
                else -> newHour12
            }
    }

    companion object {
        fun Saver() =
            Saver<NachoTimePickerStateImpl, List<Int>>(
                save = { listOf(it.hour, it.minute, it.minuteInterval) },
                restore = { NachoTimePickerStateImpl(it[0], it[1], it[2]) },
            )
    }
}

@Composable
fun rememberInvitationTimePickerState(
    initialHour: Int = 0,
    initialMinute: Int = 0,
    minuteInterval: Int = NachoTimePickerDefaults.MINUTE_INTERVAL_5,
): NachoTimePickerState =
    rememberSaveable(saver = NachoTimePickerStateImpl.Saver()) {
        NachoTimePickerStateImpl(
            initialHour = initialHour,
            initialMinute = initialMinute,
            minuteInterval = minuteInterval,
        )
    }
