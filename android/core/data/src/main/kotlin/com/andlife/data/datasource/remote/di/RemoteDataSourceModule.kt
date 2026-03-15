package com.andlife.data.datasource.remote.di

import com.andlife.data.datasource.remote.address.AddressRemoteDataSource
import com.andlife.data.datasource.remote.address.AddressRemoteDataSourceImpl
import com.andlife.data.datasource.remote.fcm.FcmTokenDataSource
import com.andlife.data.datasource.remote.fcm.FcmTokenDataSourceImpl
import com.andlife.data.datasource.remote.invitation.InvitationRemoteDataSource
import com.andlife.data.datasource.remote.invitation.InvitationRemoteDataSourceImpl
import com.andlife.data.datasource.remote.guestbook.GuestBookRemoteDataSource
import com.andlife.data.datasource.remote.guestbook.GuestBookRemoteDataSourceImpl
import com.andlife.data.datasource.remote.report.ReportRemoteDataSource
import com.andlife.data.datasource.remote.report.ReportRemoteDataSourceImpl
import com.andlife.data.datasource.remote.thankscard.ThanksCardRemoteDataSource
import com.andlife.data.datasource.remote.thankscard.ThanksCardRemoteDataSourceImpl
import com.andlife.data.datasource.remote.user.UserRemoteDataSource
import com.andlife.data.datasource.remote.user.UserRemoteDataSourceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal abstract class RemoteDataSourceModule {
    @Binds
    @Singleton
    abstract fun bindAddressRemoteDataSource(
        addressRemoteDataSourceImpl: AddressRemoteDataSourceImpl,
    ): AddressRemoteDataSource

    @Binds
    @Singleton
    abstract fun bindGuestBookRemoteDataSource(
        guestBookRemoteDataSourceImpl: GuestBookRemoteDataSourceImpl,
    ): GuestBookRemoteDataSource

    @Binds
    @Singleton
    abstract fun bindRemoteDataSource(
        detailRemoteDataSourceImpl: InvitationRemoteDataSourceImpl,
    ): InvitationRemoteDataSource

    @Binds
    @Singleton
    abstract fun bindUserRemoteDataSource(
        userRemoteDataSourceImpl: UserRemoteDataSourceImpl,
    ): UserRemoteDataSource

    @Binds
    @Singleton
    abstract fun bindThanksCardRemoteDataSource(
        thanksCardRemoteDataSourceImpl: ThanksCardRemoteDataSourceImpl,
    ): ThanksCardRemoteDataSource

    @Binds
    @Singleton
    abstract fun bindReportRemoteDataSource(
        reportRemoteDataSourceImpl: ReportRemoteDataSourceImpl
    ): ReportRemoteDataSource

    @Binds
    @Singleton
    abstract fun bindFcmTokenDataSource(
        fcmTokenDataSourceImpl: FcmTokenDataSourceImpl
    ): FcmTokenDataSource
}
