package com.uts.homelab.network

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject

class FirebaseRepository @Inject constructor(private val auth:FirebaseAuth,private val firestore:FirebaseFirestore) : IFirebaseRepository{
}