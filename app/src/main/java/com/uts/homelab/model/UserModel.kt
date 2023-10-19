package com.uts.homelab.model

import android.os.Build
import androidx.annotation.RequiresApi
import com.google.firebase.firestore.ktx.toObject
import com.uts.homelab.network.FirebaseRepository
import com.uts.homelab.network.dataclass.*
import com.uts.homelab.network.db.Constants
import com.uts.homelab.network.db.DataBaseHome
import com.uts.homelab.utils.Opinion
import com.uts.homelab.utils.Rol
import com.uts.homelab.utils.State
import com.uts.homelab.utils.Utils
import com.uts.homelab.utils.response.ManagerAppointmentUserModel
import com.uts.homelab.utils.response.ManagerError
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserModel @Inject constructor(
    private val firebaseRepository: FirebaseRepository,
    private val roomRepository: DataBaseHome,
) {

    suspend fun setAppointmentUserFirestore(
        valueAppointment: Array<String>,
        appointmentUserModel: AppointmentUserModel,
    ): ManagerError {
        return runCatching {
            appointmentUserModel.typeOfExam = valueAppointment[0]
            appointmentUserModel.hour = valueAppointment[1]
            appointmentUserModel.date = valueAppointment[2]

            firebaseRepository.setAppointmentToFirestore(
                appointmentUserModel
            ).await()
        }.fold(
            onSuccess = {
                updateAppointmentAvailable(
                    appointmentUserModel.date,
                    appointmentUserModel.uidNurse,
                    appointmentUserModel.hour,
                    appointmentUserModel.uidUser
                )
            },
            onFailure = { ManagerError.Error(it.message!!) }
        )
    }

    suspend fun initView(): UserRegister {
        return roomRepository.userSessionDao().getUserAuth()
    }

    suspend fun closeSession(): Boolean {
        return try {
            roomRepository.userSessionDao()
                .deleteUserSession(roomRepository.userSessionDao().getUserAuth())
            firebaseRepository.closeSession()
            true
        } catch (e: Exception) {
            false
        }

    }

    suspend fun saveUserRoom(value: UserRegister) {
        roomRepository.userSessionDao().updateUserSession(value)
    }

    suspend fun saveUserFirestore(arrayOf: Array<String?>, modelUser: UserRegister?): ManagerError {
        val userRegister = modelUser ?: roomRepository.userSessionDao().getUserAuth()
        val map = HashMap<String, Any>()

        userRegister.geolocation.longitude = arrayOf[2]!!
        userRegister.geolocation.latitude = arrayOf[1]!!
        userRegister.address = arrayOf[0]!!

        map["geolocation"] = userRegister.geolocation
        map["age"] = userRegister.age
        map["address"] = userRegister.address
        map["eps"] = userRegister.eps
        map["gender"] = userRegister.gender
        map["newUser"] = false
        map["nacimiento"] = userRegister.nacimiento
        map["phone"] = userRegister.phone
        map["typeDocument"] = userRegister.typeDocument
        map["valueDocument"] = userRegister.valueDocument


        return kotlin.runCatching {
            firebaseRepository.updateUserFirestore(map).await()
        }.fold(
            onSuccess = {
                userRegister.newUser = false
                roomRepository.userSessionDao().updateUserSession(userRegister)
                ManagerError.Success("0")
            }, onFailure = {
                ManagerError.Error(it.message!!)
            }
        )

    }

    suspend fun getNurseAvailable(): ManagerError {
        return runCatching {
            firebaseRepository.getNurseAvailable()
        }.fold(
            onSuccess = {
                val res = it.toObjects(Job::class.java)
                ManagerError.Success(res)
            },
            onFailure = { ManagerError.Error(it.message!!) }
        )
    }

    fun getConverterHour(hour: Int, hora: HourJob): Boolean {

        var count = 0

        val clavesAObtener = listOf(hour.toString(), (hour - 1).toString(), (hour + 1).toString())


        for (clave in clavesAObtener) {
            if (hora.map.containsKey(clave)) {
                val valor = hora.map[clave]
                if (valor!!.isEmpty()) {
                    count++
                }
            }
        }


        return count == 3
    }

    suspend fun getNurseAvailableFilter(initFilter: List<String>): Any {
        return runCatching {
            firebaseRepository.getIdsNursesAvailable(initFilter as ArrayList<String>)
        }.fold(
            onSuccess = {
                val res = it.toObjects(NurseRegister::class.java)
                ManagerError.Success(res.toList())
            },
            onFailure = { ManagerError.Error(it.message!!) }
        )
    }

    suspend fun getUSerAuth(): UserRegister {
        return roomRepository.userSessionDao().getUserAuth()
    }

    suspend fun saveAppointment(
        arrayOf: Array<String?>,
        appointmentUserModel: AppointmentUserModel?,
    ): Any {
        return runCatching {
            appointmentUserModel!!.geolocation.longitude = arrayOf[2]!!
            appointmentUserModel.geolocation.latitude = arrayOf[1]!!
            appointmentUserModel.address = arrayOf[0]!!
            firebaseRepository.setAppointmentToFirestore(appointmentUserModel)
        }.fold(
            onSuccess = {
                ManagerError.Success("0")
            },
            onFailure = { ManagerError.Error(it.message!!) }
        )
    }

    suspend fun updateAppointmentAvailable(
        date: String,
        uidNurse: String,
        hour: String,
        uidUser: String
    ): ManagerError {
        var bool = false
        return runCatching {
            firebaseRepository.getIdNurseAvailable(uidNurse)
        }.fold(
            onSuccess = {
                val res = it.toObject(Job::class.java)
                print(res)

                lstNewDataJob.forEach { data ->
                    run {
                        if (data == uidNurse) {
                            bool = true
                        }
                    }

                }
                val modelNew: Job
                if (bool) {
                    res!!.job.add(DataJob().apply { setNurseId(uidNurse, uidUser, date, hour) })
                    firebaseRepository.updateAvailableFirestore(res, uidNurse)
                } else {
                    modelNew = res!!.copy(res!!.job)
                    for (itModel in modelNew.job) {

                        if (itModel.date == date) {


                            for (itMap in itModel.hora.map) {
                                val splice = hour.split(" : ")[0].toInt()
                                val lstRangeHour = listOf(splice, splice - 1, splice + 1)

                                for (range in lstRangeHour) {
                                    if (itMap.key.equals(range.toString())) {
                                        itModel.hora.map[itMap.key] = uidUser
                                    }
                                }
                            }
                        }

                    }
                    firebaseRepository.updateAvailableFirestore(modelNew, uidNurse)
                }

                ManagerError.Success("1")
            },
            onFailure = { ManagerError.Error(it.message!!) }
        )

    }

    private var lstNewDataJob = listOf<String>()
    fun setNurseInsert(listOfAddNewDataJob: ArrayList<String>) {
        lstNewDataJob = listOfAddNewDataJob
    }

    suspend fun getAppointmentByUser(): ManagerAppointmentUserModel {
        return runCatching {
            firebaseRepository.getAppointmentByDate(
                Utils().getCurrentDate(),
                Constants.APPOINTMENT_UID_USER
            )
        }.fold(
            onSuccess = {
                val lst = arrayListOf<AppointmentUserModel>()
                for (index in 0 until it.documents.size) {
                    val rta = it.documents[index].id
                    val res = it.documents[index].toObject(AppointmentUserModel::class.java)
                    if (res != null) {
                        res.dc = rta
                        lst.add(res)
                    }
                }

                ManagerAppointmentUserModel.Success(lst)
            },
            onFailure = { ManagerAppointmentUserModel.Error(Utils.messageErrorConverter(it.message!!)) }
        )
    }


    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun getAppointmentAllByUser(): ManagerError {
        return runCatching {
            firebaseRepository.getAllAppointment(Constants.APPOINTMENT_UID_USER)
        }.fold(
            onSuccess = {
                /*
                for (item in it.documents) {

                    val hour = item.get("hour").toString().split(" : ")[0]
                    val minute = item.get("hour").toString().split(" : ")[1]

                    val horaDada: LocalTime = LocalTime.of(hour.toInt(), minute.toInt())
                    val horaDespues: LocalTime = horaDada.plusHours(2)

                    val horaActual = LocalTime.now()
                    val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm")

                    val horaFormateada = horaActual.format(formatter)
                    val currentHour = horaFormateada.split(":")[0].toInt()

                    if (horaDespues.hour <= currentHour) {
                        CoroutineScope(Dispatchers.IO).launch {
                            val modelUpdate = item.toObject(AppointmentUserModel::class.java)
                            modelUpdate!!.dc = item.id
                            updateStateAppointment(modelUpdate.)
                        }
                    }
                }
                */

                val lst = arrayListOf<AppointmentUserModel>()
                for (index in 0 until it.documents.size) {
                    val rta = it.documents[index].id
                    val res = it.documents[index].toObject(AppointmentUserModel::class.java)
                    if (res != null) {
                        res.dc = rta
                        lst.add(res)
                    }
                }

                ManagerError.Success(lst)
            },
            onFailure = { ManagerError.Error(it.message!!) }
        )
    }

    suspend fun setOpinion(type: String, message: String, title: String): ManagerError {
        val model = CommentType()
        val utils = Utils()
        val roomModel = roomRepository.userSessionDao().getUserAuth()

        model.id = roomModel.uid
        model.message = message
        model.type = type
        model.name = roomModel.name + " " + roomModel.lastName
        model.title = title
        model.rol = roomModel.rol
        model.ts = utils.dateToLong(utils.getCurrentDate())

        return runCatching {
            firebaseRepository.setTypeComment(model).await()

        }.fold(
            onSuccess = {
                ManagerError.Success("1")
            },
            onFailure = { ManagerError.Error(it.message!!) }
        )
    }

    suspend fun sendRequestChangePassword(): ManagerError {
        return runCatching {
            firebaseRepository.requestChangePassword(firebaseRepository.getAuth().currentUser?.email!!)
        }.fold(
            onSuccess = {
                ManagerError.Success("1")
            },

            onFailure = {
//                TODO("ERROR AL ENVIAR EL RESTABLECIMIENTO DE CONTRASEÑA")
                ManagerError.Error(Utils.messageErrorConverter(it.message!!))
            }
        )
    }

    suspend fun updateDataUserFirestore(values: Array<String?>, userRegister: UserRegister): Any {

        userRegister.eps = values[0]!!
        userRegister.phone = values[1]!!.toLong()

        return runCatching {
            firebaseRepository.updateDataUserFirestore(userRegister)
        }.fold(
            onSuccess = {
                roomRepository.userSessionDao().updateUserSession(userRegister)
                ManagerError.Success(userRegister)
            },

            onFailure = {
                ManagerError.Error(Utils.messageErrorConverter(it.message!!))
            }
        )
    }

    suspend fun getAllAppointmentStateFinish(): ManagerAppointmentUserModel {
        return runCatching {
            firebaseRepository.getAppointmentStateFinish()
        }.fold(
            onSuccess = {
                val res = it.toObjects(AppointmentUserModel::class.java).toList()
                ManagerAppointmentUserModel.Success(res)
            },
            onFailure = { ManagerAppointmentUserModel.Error(Utils.messageErrorConverter(it.message!!)) }
        )
    }

    fun initAsyncAppointment(
        onCall: (AppointmentUserModel) -> Unit,
        uidNurse: String,
        uidUser: String,
        dc: String,
    ) {
        try {
            firebaseRepository.realTimeAppointment(onCall, uidNurse, uidUser, dc)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    fun initAsyncWorkingDay(onCallWorkingDay: (WorkingDayNurse) -> Unit, uidNurse: String) {
        try {
            firebaseRepository.realTimeWorkingDayOnly(onCallWorkingDay, uidNurse)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun updateStateAppointment(
        model: AppointmentUserModel,
        state: State,
        step: Int,
    ): ManagerError {
        return runCatching {
            model.state = state.name
            model.step = step
            firebaseRepository.updateAppointmentState(model)
        }.fold(
            onSuccess = {
                ManagerError.Success(1)
            },
            onFailure = { ManagerError.Error(it.message!!) }
        )
    }

    suspend fun setOpinionAppointment(
        modelAppointment: AppointmentUserModel,
        title: String,
        message: String
    ): Any {
        val model = CommentType()
        val utils = Utils()
        val roomModel = roomRepository.userSessionDao().getUserAuth()

        model.id = roomModel.uid
        model.message = message
        model.type = Opinion.PROBLEMAPPOINTMENT.name
        model.name = roomModel.name + " " + roomModel.lastName
        model.title = title
        model.rol = roomModel.rol
        model.ts = utils.dateToLong(utils.getCurrentDate())
        if (model.rol == Rol.USER.name) {
            model.idEnd = modelAppointment.modelNurse.uid
            model.nameEnd =
                modelAppointment.modelNurse.name + " " + modelAppointment.modelNurse.lastName
            model.typeEnd = modelAppointment.modelNurse.rol
        } else {
            model.idEnd = modelAppointment.modelUser.uid
            model.nameEnd =
                modelAppointment.modelUser.name + " " + modelAppointment.modelUser.lastName
            model.typeEnd = modelAppointment.modelUser.rol
        }

        return runCatching {
            firebaseRepository.setTypeComment(model).await()

        }.fold(
            onSuccess = {
                ManagerError.Success("1")
            },
            onFailure = { ManagerError.Error(Utils.messageErrorConverter(it.message!!)) }
        )
    }

    suspend fun getResulAppointmentByDocumentId(dc: String): ManagerError {
        return runCatching {
            firebaseRepository.getResultAppointment(dc)

        }.fold(
            onSuccess = {
                val model = it.toObject<ResultAppointment>()!!
                ManagerError.Success(model)
            },
            onFailure = { ManagerError.Error(Utils.messageErrorConverter(it.message!!)) }
        )
    }

    suspend fun sendOpinionDelayAppointment(appointmentUserModel: AppointmentUserModel): Any {
        val model = CommentType()
        val utils = Utils()

        model.id = appointmentUserModel.uidUser
        model.message =
            "Por favor dar respuesta lo mas rapido posible para continuar el procedimiento"
        model.type = Opinion.PROBLEMAPPOINTMENT.name
        model.name =
            appointmentUserModel.modelUser.name + " " + appointmentUserModel.modelUser.lastName
        model.title = "Retraso en los resultados de la prueba ${appointmentUserModel.typeOfExam}"
        model.rol = Rol.USER.name
        model.ts = utils.dateToLong(utils.getCurrentDate())

        model.idEnd = appointmentUserModel.modelNurse.uid
        model.nameEnd =
            appointmentUserModel.modelNurse.name + " " + appointmentUserModel.modelNurse.lastName
        model.typeEnd = appointmentUserModel.modelNurse.rol




        return runCatching {
            firebaseRepository.setTypeComment(model).await()

        }.fold(
            onSuccess = {
                ManagerError.Success("1")
            },
            onFailure = { ManagerError.Error(Utils.messageErrorConverter(it.message!!)) }
        )
    }
}