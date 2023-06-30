package com.uts.homelab.utils.extension

import android.app.Activity
import android.content.Intent
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.uts.homelab.view.MainActivity
import com.uts.homelab.view.UserActivity

fun Activity.toastMessage(message: String) {
    val toast = Toast.makeText(this, message, Toast.LENGTH_LONG)
    toast.show()
}

fun Activity.intentToUserHome() {
    startActivity(Intent(this, UserActivity::class.java))
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
