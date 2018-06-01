package com.example.sourabh.navigate

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.app.DialogFragment

object PermissionUtil {
    
    fun requestPermission(context: Activity, permission: String, requestCode: Int){
        ActivityCompat.requestPermissions(context, arrayOf(permission), requestCode)
    }
    fun isPermissionAvailable(context: Activity, permission: String): Boolean{
        return ActivityCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
    }

    fun isPermissionGranted(permissions: Array<out String>, grantResults: IntArray, permission: String): Boolean {
        permissions.forEachIndexed { index, pm ->
            if(pm == permission){
                return grantResults[index] == PackageManager.PERMISSION_GRANTED
            }
        }
        return false
    }

    class PermissionDeniedDialog: DialogFragment() {
        companion object {
            private val ARGUMENT_FINISH_ACTIVITY: String = "argument_finish_activity"

            fun newInstance(finishActivity: Boolean): PermissionDeniedDialog {
                val bundle = Bundle()
                bundle.putBoolean(ARGUMENT_FINISH_ACTIVITY, finishActivity)
                val dialog = PermissionDeniedDialog()
                dialog.arguments = bundle
                return dialog
            }
        }

        private var mFinishActivity: Boolean = false
        var message = "Permission Denied"
        private var mOnClickListener: DialogInterface.OnClickListener? = null

        fun setOnClickListener(listener: DialogInterface.OnClickListener?){
            mOnClickListener = listener
        }

        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
            mFinishActivity = arguments?.getBoolean(ARGUMENT_FINISH_ACTIVITY, false) ?: false
            return AlertDialog.Builder(activity)
                    .setMessage(message)
                    .setPositiveButton(android.R.string.ok, mOnClickListener)
                    .create()
        }

        override fun onDismiss(dialog: DialogInterface?) {
            super.onDismiss(dialog)
            if(mFinishActivity){
                activity?.finish()
            }
        }
    }

    class PermissionRationaleDialog: DialogFragment() {
        companion object {

        }
    }
}