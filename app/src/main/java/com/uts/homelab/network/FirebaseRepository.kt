package com.uts.homelab.network

import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import com.uts.homelab.network.dataclass.*
import com.uts.homelab.network.db.Constants
import com.uts.homelab.utils.State
import com.uts.homelab.utils.Utils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

class FirebaseRepository @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : IFirebaseRepository {

    override suspend fun isSetAuthentication(email: String, password: String): AuthResult {
        return withContext(Dispatchers.IO) {
            auth.createUserWithEmailAndPassword(email.trim(), password.trim()).await()
        }
    }

    override suspend fun isSetAuthenticationToken(token: String): AuthResult {
        return withContext(Dispatchers.IO) {
            auth.signInWithCustomToken(token).await()
        }
    }

    override suspend fun getToken(): String {
        return withContext(Dispatchers.IO) {
            auth.currentUser!!.getIdToken(true).await().token!!.trim()
        }
    }

    override suspend fun setRegisterUserToFirestore(model: UserRegister): Task<Void> {
        return withContext(Dispatchers.IO) {
            firestore.collection("Users").document(model.uid).set(model)
        }
    }

    override suspend fun setAppointmentToFirestore(appointmentUserModel: AppointmentUserModel): Task<*> {
        return withContext(Dispatchers.IO) {
            firestore.collection("Appointment").document().set(appointmentUserModel)
        }
    }

    override suspend fun setRegisterNurseToFirestore(model: NurseRegister): Task<Void> {
        return withContext(Dispatchers.IO) {
            firestore.collection("Nurses").document(model.uid).set(model)
        }
    }

    override suspend fun setRegisterWorkingNurse(model: WorkingDayNurse): Task<*> {
        model.id = auth.uid!!
        return withContext(Dispatchers.IO) {
            firestore.collection("WorkingDay").document().set(model)
        }
    }

    override suspend fun setRegisterAvailableAppointment(modelJob: Job): Task<*> {
        return withContext(Dispatchers.IO) {
            firestore.collection("AvailableNurse").document(auth.uid!!).set(modelJob)
        }
    }


    override suspend fun updateNurseFirestore(map: NurseRegister): Task<*> {
        return withContext(Dispatchers.IO) {
            firestore.collection("Nurses").document(auth.uid!!).set(map, SetOptions.merge())
        }
    }

    override suspend fun updateUserFirestore(map: Map<String, Any>): Task<*> {
        return withContext(Dispatchers.IO) {
            firestore.collection("Users").document(auth.uid!!).update(map)
        }
    }

    override suspend fun updateAvailableFirestore(job: Job, uidNurse: String): Task<*> {
        return withContext(Dispatchers.IO) {
            firestore.collection("AvailableNurse").document(uidNurse).set(job, SetOptions.merge())
        }
    }

    override suspend fun isAuth(email: String, password: String): AuthResult {
        return withContext(Dispatchers.IO) {
            auth.signInWithEmailAndPassword(email.trim(), password.trim()).await()
        }
    }

    override suspend fun isUserAdminFirestore(email: Any): QuerySnapshot {
        return withContext(Dispatchers.IO) {
            firestore.collection(Constants.COLLECT_ADMIN).whereEqualTo("email", email).get().await()
        }
    }

    override suspend fun updateAdmin(adminSession: AdminSession): Task<*> {
        return withContext(Dispatchers.IO) {
            firestore.collection(Constants.COLLECT_ADMIN)
                .document(isUserAdminFirestore(auth.currentUser!!.email!!).documents[0].id).set(
                adminSession,
                SetOptions.merge()
            )
        }
    }

    override suspend fun isUserNurseFirestore(email: Any): QuerySnapshot {
        return withContext(Dispatchers.IO) {
            firestore.collection("Nurses").whereEqualTo("email", email).get().await()
        }
    }

    override suspend fun isUserPatientFirestore(email: Any): QuerySnapshot {
        return withContext(Dispatchers.IO) {
            firestore.collection("Users").whereEqualTo("email", email).get().await()
        }
    }

    override suspend fun getNurseAvailable(): QuerySnapshot {
        return withContext(Dispatchers.IO) {
            firestore.collection("AvailableNurse").get().await()
        }
    }

    override suspend fun getIdNurseAvailable(uid: String): DocumentSnapshot {
        return withContext(Dispatchers.IO) {
            firestore.collection("AvailableNurse").document(uid).get().await()
        }
    }

    override suspend fun getIdsNursesAvailable(list: ArrayList<String>): QuerySnapshot {
        return withContext(Dispatchers.IO) {
            firestore.collection("Nurses").whereIn(FieldPath.documentId(), list).get().await()
        }
    }

    override suspend fun getAllNurses(): QuerySnapshot {
        return withContext(Dispatchers.IO) {
            firestore.collection(Constants.COLLECT_NURSE).get().await()
        }
    }

    override suspend fun getAppointmentByDate(date: String, typeUser: String): QuerySnapshot {
        return withContext(Dispatchers.IO) {
            firestore.collection(Constants.COLLECT_APPOINTMENT).whereEqualTo(typeUser, auth.uid)
                .whereEqualTo("date", date).get().await()
        }
    }

    override suspend fun getAllAppointment(typeUser: String): QuerySnapshot {
        return withContext(Dispatchers.IO) {
            firestore.collection(Constants.COLLECT_APPOINTMENT).whereEqualTo(typeUser, auth.uid)
                .get().await()
        }
    }

    override suspend fun getAppointmentStateLaboratory(): QuerySnapshot {
        return withContext(Dispatchers.IO) {
            firestore.collection(Constants.COLLECT_APPOINTMENT)
                .whereEqualTo("state", State.LABORATORIO.name).get().await()
        }
    }

    override suspend fun getAppointmentStateFinish(): QuerySnapshot {
        return withContext(Dispatchers.IO) {
            firestore.collection(Constants.COLLECT_APPOINTMENT)
                .whereEqualTo("state", State.FINALIZADO.name)
                .whereEqualTo("uidUser", auth.currentUser!!.email).get().await()
        }
    }

    override suspend fun updateAppointmentState(model: AppointmentUserModel): Task<*> {
        return withContext(Dispatchers.IO) {
            firestore.collection(Constants.COLLECT_APPOINTMENT).document(model.dc).set(model,
                SetOptions.merge())
        }
    }

    override fun closeSession() {
        auth.signOut()
    }

    override suspend fun setTypeComment(commentType: CommentType): Task<*> {
        return withContext(Dispatchers.IO) {
            firestore.collection("Opinion").document().set(commentType)
        }
    }

    override suspend fun getAllTypeComment(): QuerySnapshot {
        return withContext(Dispatchers.IO) {
            firestore.collection("Opinion").get().await()
        }
    }

    override suspend fun requestChangePassword(email: String) {
        return withContext(Dispatchers.IO) {
            auth.sendPasswordResetEmail(email).await()
        }
    }

    override suspend fun updateDataUserFirestore(userRegister: UserRegister): Task<*> {
        return withContext(Dispatchers.IO) {
            firestore.collection("Users").document(auth.currentUser!!.uid)
                .set(userRegister, SetOptions.merge())
        }
    }

    override suspend fun getJournal(): QuerySnapshot {
        return withContext(Dispatchers.IO) {
            firestore.collection("WorkingDay").whereEqualTo("id", auth.currentUser!!.uid).get()
                .await()
        }
    }

    override suspend fun updateJournal(workingDayNurse: WorkingDayNurse, idDoc: String): Task<*> {
        return withContext(Dispatchers.IO) {
            firestore.collection("WorkingDay").document(idDoc)
                .set(workingDayNurse, SetOptions.merge())
        }
    }

    override suspend fun getNursesActiveByJournal(): QuerySnapshot {
        return withContext(Dispatchers.IO) {
            firestore.collection(Constants.COLLECT_WORKING_DAY).whereEqualTo("active".trim(),true).get().await()
        }
    }

    override suspend fun getAllNursesByJournal(): QuerySnapshot {
        return withContext(Dispatchers.IO) {
            firestore.collection(Constants.COLLECT_WORKING_DAY).get().await()
        }
    }

    override suspend fun setResultAppointment(resultAppointment: ResultAppointment): Task<*> {
        return withContext(Dispatchers.IO) {
            firestore.collection(Constants.COLLECT_RESULT_APPOINTMENT).document(resultAppointment.appointmentUserModel.dc).set(resultAppointment)
        }
    }

    private lateinit var rl:ListenerRegistration
    fun realTimeWorkingDayAllCollection(onCall: (WorkingDayNurse) -> Unit) {
        rl = firestore.collection(Constants.COLLECT_WORKING_DAY).addSnapshotListener { snapshot, e ->
            if (e != null) {
                return@addSnapshotListener
            }

            for (dc in snapshot!!.documentChanges) {
                when (dc.type) {
                    DocumentChange.Type.ADDED -> Log.i(
                        javaClass.name,
                        "New document from WorkingDay: ${dc.document.id}"
                    )
                    DocumentChange.Type.MODIFIED -> {
                        Log.i(
                            javaClass.name,
                            "Modified document from WorkingDay: ${dc.document.id}"
                        )

                        val model = dc.document.toObject(WorkingDayNurse::class.java)
                        onCall(model)
                    }
                    DocumentChange.Type.REMOVED -> Log.i(
                        javaClass.name,
                        "Removed document from WorkingDay: ${dc.document.id}"
                    )
                }
            }

        }
    }

    fun realTimeWorkingDayOnly(onCall: (WorkingDayNurse) -> Unit, uidNurse: String) {
        firestore.collection(Constants.COLLECT_WORKING_DAY).whereEqualTo("id", uidNurse)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    return@addSnapshotListener
                }

                for (dc in snapshot!!.documentChanges) {
                    when (dc.type) {
                        DocumentChange.Type.ADDED -> Log.i(
                            javaClass.name,
                            "New document from WorkingDay: ${dc.document.id}"
                        )
                        DocumentChange.Type.MODIFIED -> {
                            Log.i(
                                javaClass.name,
                                "Modified document from WorkingDay: ${dc.document.id}"
                            )
                            val model = dc.document.toObject(WorkingDayNurse::class.java)
                            onCall(model)
                        }
                        DocumentChange.Type.REMOVED -> Log.i(
                            javaClass.name,
                            "Removed document from WorkingDay: ${dc.document.id}"
                        )
                    }
                }

            }
    }

    fun getAuth(): FirebaseAuth {
        return auth
    }

    fun realTimeAppointment(
        onCall: (AppointmentUserModel) -> Unit,
        uidNurse: String,
        uidUser: String,
        dc: String
    ) {
        firestore.collection(Constants.COLLECT_APPOINTMENT)
            .whereEqualTo(Constants.APPOINTMENT_UID_USER, uidUser)
            .whereEqualTo(Constants.APPOINTMENT_UID_NURSE, uidNurse)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    return@addSnapshotListener
                }

                for (dc in snapshot!!.documentChanges) {
                    when (dc.type) {
                        DocumentChange.Type.ADDED -> Log.i(
                            javaClass.name,
                            "New document from WorkingDay: ${dc.document.id}"
                        )
                        DocumentChange.Type.MODIFIED -> {
                            Log.i(
                                javaClass.name,
                                "realTimeAppointment modified: ${dc.document.id}"
                            )
                            val model = dc.document.toObject(AppointmentUserModel::class.java)
                            onCall(model)
                        }
                        DocumentChange.Type.REMOVED -> Log.i(
                            javaClass.name,
                            "Removed document from WorkingDay: ${dc.document.id}"
                        )
                    }
                }

            }
    }

    var t = ""
    fun realTimeAppointmentByNurse(
        onCall: (AppointmentUserModel) -> Unit,
        uidNurse: String
    ) {
        firestore.collection(Constants.COLLECT_APPOINTMENT)
            .whereEqualTo(Constants.APPOINTMENT_UID_NURSE, uidNurse)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    return@addSnapshotListener
                }

                for (dc in snapshot!!.documentChanges) {
                    when (dc.type) {
                        DocumentChange.Type.ADDED -> {

                            if(t==dc.document.id){
                                return@addSnapshotListener
                            }else{
                                t= dc.document.id
                                val model = dc.document.toObject(AppointmentUserModel::class.java)
                                model.dc = dc.document.id


                                if(model.date == Utils().getCurrentDate()){
                                    onCall(model)
                            }

                            Log.i(
                                javaClass.name,
                                "Santiago Rueda: ${dc.document.id}"
                            )
                            }
                        }
                        DocumentChange.Type.MODIFIED -> {
                            Log.i(
                                javaClass.name,
                                "Modified document from WorkingDay: ${dc.document.id}"
                            )
                        }
                        DocumentChange.Type.REMOVED -> Log.i(
                            javaClass.name,
                            "Removed document from WorkingDay: ${dc.document.id}"
                        )
                    }
                }

            }
    }

    fun stopAsyncWorkingDay() {
        rl.remove()
    }


}