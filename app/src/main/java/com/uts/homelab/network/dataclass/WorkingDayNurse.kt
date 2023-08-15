package com.uts.homelab.network.dataclass

data class WorkingDayNurse(

    private val geolocation: Geolocation,
    private val active:Boolean = false
){
    var id:String = ""
}
