package com.andlife.deeplink

object InstallReferrerParser {
    fun parseInvitationId(referrerUrl: String): String? {
        val regex = """${DeepLinkConfig.PARAM_INVITE_ID}=(\d+)""".toRegex()
        return regex.find(referrerUrl)?.groupValues?.get(1)
    }
}
