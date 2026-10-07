package com.example.learning_dashboard.di

import com.example.learning_dashboard.data.remote.AuthApi
import com.example.learning_dashboard.data.remote.CourseApi
import com.example.learning_dashboard.data.remote.FakeAuthApi
import com.example.learning_dashboard.data.remote.FakeCourseApi
import com.example.learning_dashboard.data.repository.DefaultAuthRepository
import com.example.learning_dashboard.data.repository.DefaultCourseRepository
import com.example.learning_dashboard.domain.repository.AuthRepository
import com.example.learning_dashboard.domain.repository.CourseRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {

    // Swap these two for Retrofit-backed implementations once a real backend exists.
    @Binds
    abstract fun bindCourseApi(impl: FakeCourseApi): CourseApi

    @Binds
    abstract fun bindAuthApi(impl: FakeAuthApi): AuthApi

    @Binds
    @Singleton
    abstract fun bindCourseRepository(impl: DefaultCourseRepository): CourseRepository

    @Binds
    @Singleton
    abstract fun bindAuthRepository(impl: DefaultAuthRepository): AuthRepository
}
