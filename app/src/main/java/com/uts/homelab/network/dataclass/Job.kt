package com.uts.homelab.network.dataclass

import com.uts.homelab.utils.Utils


data class Job(
    var job: ArrayList<DataJob> = arrayListOf()

) {
    fun init(uid: String) {
        val model = DataJob()
        model.date = Utils().getCurrentDate()
        model.uidNurse = uid
        job.add(model)
    }
}

data class DataJob(
    var date: String = "",
    var uidNurse: String = "",
    var hora: HourJob = HourJob()
) {
    fun setNurseId(uid: String, date: String, hour: String) {
        this.date = date
        uidNurse = uid
        val splice = hour.split(" : ")[0].toInt()
        val lstRangeHour = listOf(splice, splice - 1, splice + 1)
        hora.map.forEach {
            lstRangeHour.forEach { range ->
                run {
                    if (it.key.equals(range.toString())) {
                        hora.map[it.key] = uid
                    }
                }
            }
        }
    }
}


class HourJob {
    var map = mutableMapOf(
        "8" to "",
        "9" to "",
        "10" to "",
        "11" to "",
        "12" to "",
        "13" to "",
        "14" to "",
        "15" to "",
        "16" to "",
        "17" to "",
        "18" to "",
        "19" to "",
        "20" to "",
        "21" to "",
        "22" to "",
    )

}
