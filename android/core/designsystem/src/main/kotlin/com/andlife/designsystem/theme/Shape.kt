package com.andlife.designsystem.theme

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Immutable
data class InvitationShapes(
    val extraSmall: CornerBasedShape,
    val small: CornerBasedShape,
    val medium: CornerBasedShape,
    val large: CornerBasedShape,
    val extraLarge: CornerBasedShape
)

val invitationShapes = InvitationShapes(
    extraSmall = RoundedCornerShape(4.dp),
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(24.dp),
    extraLarge = RoundedCornerShape(32.dp)
)

internal val LocalInvitationShapes = staticCompositionLocalOf { invitationShapes }

@Preview
@Composable
private fun InvitationShapesPreview() {
    InvitationTheme {
        Column(
            modifier = Modifier
        ) {
            Button(
                onClick = {},
                shape = InvitationTheme.shapes.extraSmall
            ) {}

            Button(
                onClick = {},
                shape = InvitationTheme.shapes.small
            ) {}

            Button(
                onClick = {},
                shape = InvitationTheme.shapes.medium
            ) {}

            Button(
                onClick = {},
                shape = InvitationTheme.shapes.large
            ) {}

            Button(
                onClick = {},
                shape = InvitationTheme.shapes.extraLarge
            ) {}
        }
    }
}
