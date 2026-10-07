package com.example.learning_dashboard.data.repository

import com.example.learning_dashboard.data.local.CourseDao
import com.example.learning_dashboard.data.local.SessionStore
import com.example.learning_dashboard.data.remote.AuthApi
import com.example.learning_dashboard.domain.repository.AuthRepository
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class DefaultAuthRepository @Inject constructor(
    private val api: AuthApi,
    private val session: SessionStore,
    private val courseDao: CourseDao,
) : AuthRepository {

    override val isLoggedIn: Flow<Boolean> = session.token.map { it != null }

    override suspend fun login(email: String, password: String) {
        val token = api.login(email, password)
        session.saveToken(token)
    }

    override suspend fun logout() {
        // Clearing the token navigates away and cancels the caller's scope, so the
        // cache wipe must not be cancellable. Token goes first so the dashboard never
        // renders its empty state before leaving.
        withContext(NonCancellable) {
            session.clear()
            courseDao.clear()
        }
    }
}
