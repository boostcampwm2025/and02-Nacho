package com.andlife.invitationzzang.navigation

import com.andlife.home.Home
import com.andlife.invitation.Invitation
import com.andlife.invitationzzang.R
import com.andlife.myinvitation.MyInvitation
import kotlin.reflect.KClass

enum class MainBottomTab(
    val iconResId: Int,
    val labelResId: Int,
    val route: KClass<*>
) {
    HOME(
        R.drawable.ic_launcher_background,
        R.string.home,
        Home::class
    ),
    INVITATION(
        R.drawable.ic_launcher_background,
        R.string.invitation,
        Invitation::class
    ),
    MY_INVITATION(
        R.drawable.ic_launcher_background,
        R.string.my_invitation,
        MyInvitation::class
    );

}