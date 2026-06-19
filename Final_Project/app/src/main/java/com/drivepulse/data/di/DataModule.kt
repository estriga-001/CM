/**
 * Dagger Hilt module for Data layer dependencies.
 *
 * Camada: Data
 * Feature: DI
 *
 * Inclui: Firebase Auth, Room Database, DAOs, Repositories.
 */
package com.drivepulse.data.di

import android.content.Context
import androidx.room.Room
import com.drivepulse.data.local.dao.CoordinateDao
import com.drivepulse.data.local.dao.RunDao
import com.drivepulse.data.local.database.DrivePulseDatabase
import com.drivepulse.data.repository.AuthRepositoryImpl
import com.drivepulse.data.repository.RunRepositoryImpl
import com.drivepulse.domain.repository.AuthRepository
import com.drivepulse.domain.repository.RunRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.drivepulse.domain.repository.PostRepository
import com.drivepulse.data.repository.PostRepositoryImpl
import com.drivepulse.domain.repository.UserRepository
import com.drivepulse.data.repository.UserRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth {
        return FirebaseAuth.getInstance()
    }

    @Provides
    @Singleton
    fun provideAuthRepository(
        firebaseAuth: FirebaseAuth,
        firestore: FirebaseFirestore
    ): AuthRepository {
        return AuthRepositoryImpl(firebaseAuth, firestore)
    }

    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore {
        return Firebase.firestore
    }

    @Provides
    @Singleton
    fun provideUserRepository(firestore: FirebaseFirestore): UserRepository {
        return UserRepositoryImpl(firestore)
    }

    // -------------------------------------------------------------------------
    // Room Database
    // -------------------------------------------------------------------------

    /**
     * Fornece a instância singleton da base de dados Room.
     * Usa fallbackToDestructiveMigration para simplificar migrações em fase de desenvolvimento.
     */
    @Provides
    @Singleton
    fun provideDrivePulseDatabase(
        @ApplicationContext context: Context
    ): DrivePulseDatabase {
        return Room.databaseBuilder(
            context,
            DrivePulseDatabase::class.java,
            DrivePulseDatabase.DATABASE_NAME
        ).fallbackToDestructiveMigration().build()
    }

    /** Fornece o DAO de runs a partir da base de dados singleton. */
    @Provides
    @Singleton
    fun provideRunDao(database: DrivePulseDatabase): RunDao {
        return database.runDao()
    }

    /** Fornece o DAO de coordenadas a partir da base de dados singleton. */
    @Provides
    @Singleton
    fun provideCoordinateDao(database: DrivePulseDatabase): CoordinateDao {
        return database.coordinateDao()
    }

    // -------------------------------------------------------------------------
    // Run Repository
    // -------------------------------------------------------------------------

    /** Liga a interface [RunRepository] à implementação [RunRepositoryImpl]. */
    @Provides
    @Singleton
    fun provideRunRepository(
        runRepositoryImpl: RunRepositoryImpl
    ): RunRepository {
        return runRepositoryImpl
    }

    // -------------------------------------------------------------------------
    // Post Repository
    // -------------------------------------------------------------------------

    @Provides
    @Singleton
    fun providePostRepository(
        postRepositoryImpl: PostRepositoryImpl
    ): PostRepository {
        return postRepositoryImpl
    }
}
