package com.uts.homelab.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uts.homelab.model.UserModel
import com.uts.homelab.network.dataclass.AppointmentUserModel
import com.uts.homelab.network.dataclass.Job
import com.uts.homelab.network.dataclass.NurseRegister
import com.uts.homelab.network.dataclass.WorkingDayNurse
import com.uts.homelab.utils.Utils
import com.uts.homelab.utils.response.ManagerError
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AppointmentUserViewModel @Inject constructor(private val model: UserModel) : ViewModel() {

    val isProgress = MutableLiveData<Pair<Boolean, Int>>()
    var informationFragment = MutableLiveData<String>()
    val rvNurseAvailable = MutableLiveData<List<NurseRegister>>()

    val modelAppointment = MutableLiveData<AppointmentUserModel>()
    val modelWorkingDay = MutableLiveData<WorkingDayNurse>()


    fun setAppointment(
        valueAppointment: Array<String?>,
        appointmentUserModel: AppointmentUserModel
    ) {
        viewModelScope.launch {
            setAppointmentUser(
                valueAppointment.requireNoNulls(), appointmentUserModel
            )
        }
    }

    private suspend fun setAppointmentUser(
        valueAppointment: Array<String>,
        appointmentUserModel: AppointmentUserModel
    ) {
        isProgress.value = Pair(true, 1)
        when (val response =
            model.setAppointmentUserFirestore(valueAppointment, appointmentUserModel)) {
            is ManagerError.Success -> {
                isProgress.postValue(Pair(false, 1))
                informationFragment.postValue("0")
            }
            is ManagerError.Error -> {
                isProgress.postValue(Pair(false, 1))
                informationFragment.postValue(response.error)
            }
        }
    }

    fun getNurse(date: String, hour: Int) {
        isProgress.value = Pair(true, 2)
        viewModelScope.launch {
            when (val response = model.getNurseAvailable()) {
                is ManagerError.Success -> {
                    val filter = initFilter(
                        date,
                        hour,
                        response.modelSuccess as MutableList<Job>
                    )
                    if (filter.isNotEmpty()) {
                        getNurseChoose(filter)
                        isProgress.postValue(Pair(false, 2))
                    } else {
                        isProgress.postValue(Pair(true, 3))
                    }

                }
                is ManagerError.Error -> {
                    isProgress.postValue(Pair(false, 2))
                    informationFragment.postValue(response.error)
                }
            }
        }
    }

    private suspend fun getNurseChoose(initFilter: List<String>) {
        when (val response = model.getNurseAvailableFilter(initFilter)) {
            is ManagerError.Success -> {
                rvNurseAvailable.value = response.modelSuccess as List<NurseRegister>
            }
            is ManagerError.Error -> {
                informationFragment.postValue(response.error)
            }
        }
    }

    private var listOfAddNewDataJob: ArrayList<String> = ArrayList()
    
    private fun initFilter(date: String, hour: Int, jobs: MutableList<Job>): List<String> {
        var lst = mutableListOf<String>()

        val utils = Utils()
        listOfAddNewDataJob.clear()
        var count: Int

        var boolIsFalse= false
        for (job in jobs){

            count =0
            for (dataJob in job.job) {


                if (utils.validarFechaMayorIgualAFechaActual(date)) {

                    if (dataJob.date == date) {
                        if (utils.validarFechaMayorIgualAFechaActual(date)) {
                            val res = model.getConverterHour(hour, dataJob.hora)
                            if (res) {
                                lst.add(dataJob.uidNurse)
                                listOfAddNewDataJob = ArrayList(LinkedHashSet(listOfAddNewDataJob))
                                listOfAddNewDataJob.remove(dataJob.uidNurse)
                                break
                            }else{
                                boolIsFalse = true
                            }


                            if(count!=0){
                                lst  = ArrayList(LinkedHashSet(lst))
                                listOfAddNewDataJob = ArrayList(LinkedHashSet(listOfAddNewDataJob))
                                lst.remove(dataJob.uidNurse)
                                listOfAddNewDataJob.remove(dataJob.uidNurse)
                                break
                            }
                        }

                    } else {

                        if (utils.validarFechaMayorQueFechaActual(dataJob.date)) {

                            if(!utils.tst(date,dataJob.date)){

                                if(!boolIsFalse){
                                    val res = model.getConverterHour(hour, dataJob.hora)
                                    if (res) {
                                        lst.add(dataJob.uidNurse)
                                        listOfAddNewDataJob.add(dataJob.uidNurse)
                                    }
                                }

                            }else{
                                count++
                                lst.add(dataJob.uidNurse)
                                listOfAddNewDataJob.add(dataJob.uidNurse)
                            }

                        }else{
                            count++
                            lst.add(dataJob.uidNurse)
                            listOfAddNewDataJob.add(dataJob.uidNurse)
                        }


                    }


                }


            }

        }
        return lst

    }


    fun sendModel() {
        viewModelScope.launch {
            model.setNurseInsert(listOfAddNewDataJob)

            val response = model.getUSerAuth()
            val model = AppointmentUserModel()
            model.eps = response.eps
            model.phone = response.phone
            model.address = response.address
            model.geolocation = response.geolocation
            model.uidUser = response.uid
            model.modelUser = response
            modelAppointment.postValue(
                model
            )
        }

    }

    private val onCall = { appointment: AppointmentUserModel ->
        Log.i("ONCALL", "PASAMOS")
        modelAppointment.value = appointment
    }

    private val onCallWorkingDay = { workingNurse: WorkingDayNurse ->
        Log.i("ONCALL", "PASAMOS")
        modelWorkingDay.value = workingNurse
    }

    fun initAsyncAppointment(uidNurse: String, uidUser: String) {
        model.initAsyncAppointment(onCall,uidNurse,uidUser)
    }
    fun initAsyncWorkingDay(uidNurse: String) {
        model.initAsyncWorkingDay(onCallWorkingDay,uidNurse)
    }
}