package com.pct.extraeazypermissions.ui

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity


/**
 * Created by Tri Pham on 03-May-20.
 */
class AppSettingsDialogHolderActivity: AppCompatActivity(), DialogInterface.OnClickListener {
    private val APP_SETTINGS_RC = 7534

    private var mDialog: AlertDialog? = null
    private var mIntentFlags = 0

    companion object {
        fun createShowDialogIntent(context: Context?, dialog: AppSettingsDialog?): Intent? {
            val intent = Intent(context, AppSettingsDialogHolderActivity::class.java)
            intent.putExtra(AppSettingsDialog.EXTRA_APP_SETTINGS, dialog)
            return intent
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val appSettingsDialog: AppSettingsDialog? = AppSettingsDialog.fromIntent(intent, this)
        mIntentFlags = appSettingsDialog?.getIntentFlags()!!
        mDialog = appSettingsDialog.showDialog(this, this)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (mDialog != null && mDialog!!.isShowing()) {
            mDialog!!.dismiss()
        }
    }

    override fun onClick(dialog: DialogInterface?, which: Int) {
        if (which == Dialog.BUTTON_POSITIVE) {
            val intent: Intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                .setData(Uri.fromParts("package", packageName, null))
            intent.addFlags(mIntentFlags)
            startActivityForResult(intent, APP_SETTINGS_RC)
        } else if (which == Dialog.BUTTON_NEGATIVE) {
            setResult(Activity.RESULT_CANCELED)
            finish()
        } else {
            throw IllegalStateException("Unknown button type: $which")
        }
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)
        setResult(resultCode, data)
        finish()
    }
}