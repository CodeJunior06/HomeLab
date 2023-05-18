package com.uts.homelab.utils.extension

import android.app.Activity
import android.widget.Toast
import androidx.fragment.app.Fragment

fun Activity.toastMessage(message: String) {
    val toast = Toast.makeText(this, message, Toast.LENGTH_LONG)
    toast.show()
}

fun Fragment.toastMessage(message: String) {
    val toast = Toast.makeText(requireContext(), message, Toast.LENGTH_LONG)
    toast.show()
}