package com.uts.homelab.di


import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.uts.homelab.network.FirebaseRepository
import com.uts.homelab.network.IFirebaseRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@InstallIn(SingletonComponent::class)
@Module
object FirebaseModule {

    @Provides
    @Singleton
    fun provideFirebaseAuthInstance(): FirebaseAuth {
        return FirebaseAuth.getInstance()
    }

  /* @Provides
    @Singleton
    fun provideFirebaseStorageInstance(): FirebaseStorage {
        return Firebase.storage
    } */

    @Provides
    @Singleton
    fun provideFirestoreInstance(): FirebaseFirestore {
        return Firebase.firestore
    }

    @Provides
    @Singleton
    fun provideInstanceInject(
        auth: FirebaseAuth,
        dataBase: FirebaseFirestore
    ): IFirebaseRepository {
        return FirebaseRepository(auth, dataBase)
    }

}