package com.uts.homelab.network.dataclass

data class WorkingDayNurse(
     val geolocation: Geolocation,
     val active:Boolean = false
){
    var id:String = ""
}
