package com.andlife.data.repository.di

import com.andlife.data.repository.address.AddressRepositoryImpl
import com.andlife.data.repository.fcm.FcmTokenRepositoryImpl
import com.andlife.data.repository.guestbook.GuestBookRepositoryImpl
import com.andlife.data.repository.invitation.InvitationRepositoryImpl
import com.andlife.data.repository.report.ReportRepositoryImpl
import com.andlife.data.repository.thankscard.ThanksCardRepositoryImpl
import com.andlife.data.repository.user.UserRepositoryImpl
import com.andlife.domain.repository.address.AddressRepository
import com.andlife.domain.repository.fcm.FcmTokenRepository
import com.andlife.domain.repository.guestbook.GuestBookRepository
import com.andlife.domain.repository.invitation.InvitationRepository
import com.andlife.domain.repository.report.ReportRepository
import com.andlife.domain.repository.thankscard.ThanksCardRepository
import com.andlife.domain.repository.user.UserRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindAddressRepository(impl: AddressRepositoryImpl): AddressRepository

    @Binds
    @Singleton
    abstract fun bindGuestBookRepository(guestBookRepositoryImpl: GuestBookRepositoryImpl): GuestBookRepository

    @Binds
    @Singleton
    abstract fun bindInvitationRepository(invitationRepositoryImpl: InvitationRepositoryImpl): InvitationRepository

    @Binds
    @Singleton
    abstract fun bindUserRepository(userRepositoryImpl: UserRepositoryImpl): UserRepository

    @Binds
    @Singleton
    abstract fun bindThanksCardRepository(thanksCardRepositoryImpl: ThanksCardRepositoryImpl): ThanksCardRepository

    @Binds
    @Singleton
    abstract fun bindReportRepository(reportRepositoryImpl: ReportRepositoryImpl): ReportRepository

    @Binds
    @Singleton
    abstract fun bindFcmTokenRepository(fcmTokenRepositoryImpl: FcmTokenRepositoryImpl): FcmTokenRepository
}
