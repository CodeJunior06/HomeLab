package com.uts.homelab.utils.extension

import android.app.Activity
import android.content.Intent
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.uts.homelab.view.AdminActivity
import com.uts.homelab.view.MainActivity
import com.uts.homelab.view.NurseActivity
import com.uts.homelab.view.UserActivity

fun Activity.toastMessage(message: String) {
    val toast = Toast.makeText(this, message, Toast.LENGTH_LONG)
    toast.show()
}

fun Activity.intentToUserHome() {
    startActivity(Intent(this, UserActivity::class.java))
}

fun Activity.intentToAdminHome() {
    startActivity(Intent(this, AdminActivity::class.java))
    finish()
}
fun Activity.intentToNurseHome() {
    startActivity(Intent(this, NurseActivity::class.java))
    finish()
}

fun Activity.intentToMain(){
    startActivity(Intent(this,MainActivity::class.java))
    finish()
}

fun Fragment.toastMessage(message: String) {
    val toast = Toast.makeText(requireContext(), message, Toast.LENGTH_LONG)
    toast.show()
}

fun Fragment.intentToMain() {
    startActivity(Intent(requireContext(), MainActivity::class.java))
    requireActivity().finish()
}

fun Fragment.intentToUserHome() {
    startActivity(Intent(requireContext(), UserActivity::class.java))
    requireActivity().finish()
}

fun Fragment.intentToAdminHome() {
    startActivity(Intent(requireContext(), AdminActivity::class.java))
    requireActivity().finish()
}

fun Fragment.intentToNurseHome() {
    startActivity(Intent(requireContext(), NurseActivity::class.java))
    requireActivity().finish()
}
