package com.uts.homelab.network.dataclass

import com.uts.homelab.utils.Utils


data class Job (
     var job:ArrayList<DataJob> = arrayListOf()

){
    fun init(){
        val model = DataJob()
        model.date = Utils().getCurrentDate()
        job.add(model)
    }
}

data class DataJob(
    var date:String ="",
    var hora:HourJob = HourJob()
)


class  HourJob  {
    val ocho:String =""
    val nueve:String =""
    val diez:String =""
    val once:String =""
    val doce:String =""
    val una:String =""
    val dos:String =""
    val tres:String =""
    val cuatro:String =""
    val cinco:String =""
}
