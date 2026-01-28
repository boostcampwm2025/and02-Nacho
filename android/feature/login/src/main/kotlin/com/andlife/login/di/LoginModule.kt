package com.andlife.login.di

import com.andlife.login.social.LoginManager
import com.andlife.login.social.LoginManagerImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class LoginModule {
    @Binds
    abstract fun bindLoginManager(loginManagerImpl: LoginManagerImpl): LoginManager
}
