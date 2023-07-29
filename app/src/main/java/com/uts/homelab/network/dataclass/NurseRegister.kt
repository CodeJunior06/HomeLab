package com.uts.homelab.network.dataclass

class NurseRegister {
    var name:String? = ""
    var lastName:String? = ""
    var email:String? = ""
    var valueDocument:String? = ""
    var gender:String? = ""
    var uid:String? =""
    var address:String? = ""
    var geolocation:Geolocation = Geolocation()
    var exp:Int = 0
    var age:Int = 0
    var isAutomobile:Boolean = false
    var isNew = true
}