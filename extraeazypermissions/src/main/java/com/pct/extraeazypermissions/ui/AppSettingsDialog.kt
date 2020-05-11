package com.pct.extraeazypermissions.ui

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Parcel
import android.os.Parcelable
import android.text.TextUtils
import android.util.Log
import androidx.annotation.Nullable
import androidx.annotation.StringRes
import androidx.annotation.StyleRes
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.pct.extraeazypermissions.R


/**
 * Created by Tri Pham on 03-May-20.
 */
class AppSettingsDialog() : Parcelable {
    @StyleRes
    private var mThemeResId = 0
    private var mRationale: String? = null
    private var mTitle: String? = null
    private var mPositiveButtonText: String? = null
    private var mNegativeButtonText: String? = null
    private var mRequestCode = 0
    private var mIntentFlags = 0

    private var mActivityOrFragment: Any? = null
    private var mContext: Context? = null

    constructor(parcel: Parcel) : this() {
        mThemeResId = parcel.readInt()
        mRationale = parcel.readString()
        mTitle = parcel.readString()
        mPositiveButtonText = parcel.readString()
        mNegativeButtonText = parcel.readString()
        mRequestCode = parcel.readInt()
        mIntentFlags = parcel.readInt()
    }

    private constructor(
        activityOrFragment: Any,
        @StyleRes themeResId: Int,
        @Nullable rationale: String,
        @Nullable title: String,
        @Nullable positiveButtonText: String,
        @Nullable negativeButtonText: String,
        requestCode: Int,
        intentFlags: Int
    ): this() {
        setActivityOrFragment(activityOrFragment)
        mThemeResId = themeResId
        mRationale = rationale
        mTitle = title
        mPositiveButtonText = positiveButtonText
        mNegativeButtonText = negativeButtonText
        mRequestCode = requestCode
        mIntentFlags = intentFlags
    }

    private fun setActivityOrFragment(activityOrFragment: Any) {
        mActivityOrFragment = activityOrFragment
        mContext = if (activityOrFragment is Activity) {
            activityOrFragment
        } else if (activityOrFragment is Fragment) {
            (activityOrFragment as Fragment).getContext()
        } else {
            throw IllegalStateException("Unknown object: $activityOrFragment")
        }
    }

    private fun startForResult(intent: Intent) {
        if (mActivityOrFragment is Activity) {
            (mActivityOrFragment as Activity).startActivityForResult(intent, mRequestCode)
        } else if (mActivityOrFragment is Fragment) {
            (mActivityOrFragment as Fragment?)?.startActivityForResult(intent, mRequestCode)
        }
    }

    /**
     * Display the built dialog.
     */
    fun show() {
        AppSettingsDialogHolderActivity.createShowDialogIntent(mContext, this)?.let {
            startForResult(
                it
            )
        }
    }

    /**
     * Show the dialog. [.show] is a wrapper to ensure backwards compatibility
     */
    fun showDialog(
        positiveListener: DialogInterface.OnClickListener?,
        negativeListener: DialogInterface.OnClickListener?
    ): AlertDialog? {
        val builder: AlertDialog.Builder
        if (mThemeResId != -1) {
            builder = AlertDialog.Builder(mContext!!, mThemeResId)
        } else {
            builder = AlertDialog.Builder(mContext!!)
        }
        return builder
            .setCancelable(false)
            .setTitle(mTitle)
            .setMessage(mRationale)
            .setPositiveButton(mPositiveButtonText, positiveListener)
            .setNegativeButton(mNegativeButtonText, negativeListener)
            .show()
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeInt(mThemeResId)
        dest.writeString(mRationale)
        dest.writeString(mTitle)
        dest.writeString(mPositiveButtonText)
        dest.writeString(mNegativeButtonText)
        dest.writeInt(mRequestCode)
        dest.writeInt(mIntentFlags)
    }

    fun getIntentFlags(): Int {
        return mIntentFlags
    }

    /**
     * Builder for an [AppSettingsDialog].
     */
    class Builder {
        private val mActivityOrFragment: Any
        private val mContext: Context

        @StyleRes
        private var mThemeResId = -1
        private var mRationale: String? = null
        private var mTitle: String? = null
        private var mPositiveButtonText: String? = null
        private var mNegativeButtonText: String? = null
        private var mRequestCode = -1
        private var mOpenInNewTask = false

        /**
         * Create a new Builder for an [AppSettingsDialog].
         *
         * @param activity the [Activity] in which to display the dialog.
         */
        constructor(activity: Activity) {
            mActivityOrFragment = activity
            mContext = activity
        }

        /**
         * Create a new Builder for an [AppSettingsDialog].
         *
         * @param fragment the [Fragment] in which to display the dialog.
         */
        constructor(fragment: Fragment) {
            mActivityOrFragment = fragment
            mContext = fragment.getContext()!!
        }

        /**
         * Set the dialog theme.
         */
        fun setThemeResId(@StyleRes themeResId: Int): Builder {
            mThemeResId = themeResId
            return this
        }

        /**
         * Set the title dialog. Default is "Permissions Required".
         */
        fun setTitle(@Nullable title: String?): Builder {
            mTitle = title
            return this
        }

        /**
         * Set the title dialog. Default is "Permissions Required".
         */
        fun setTitle(@StringRes title: Int): Builder {
            mTitle = mContext.getString(title)
            return this
        }

        /**
         * Set the rationale dialog. Default is
         * "This app may not work correctly without the requested permissions.
         * Open the app settings screen to modify app permissions."
         */
        fun setRationale(@Nullable rationale: String?): Builder {
            mRationale = rationale
            return this
        }

        /**
         * Set the rationale dialog. Default is
         * "This app may not work correctly without the requested permissions.
         * Open the app settings screen to modify app permissions."
         */
        fun setRationale(@StringRes rationale: Int): Builder {
            mRationale = mContext.getString(rationale)
            return this
        }

        /**
         * Set the positive button text, default is [android.R.string.ok].
         */
        fun setPositiveButton(@Nullable text: String?): Builder {
            mPositiveButtonText = text
            return this
        }

        /**
         * Set the positive button text, default is [android.R.string.ok].
         */
        fun setPositiveButton(@StringRes textId: Int): Builder {
            mPositiveButtonText = mContext.getString(textId)
            return this
        }

        /**
         * Set the negative button text, default is [android.R.string.cancel].
         *
         *
         * To know if a user cancelled the request, check if your permissions were given with [ ][EasyPermissions.hasPermissions] in {@see
         * * Activity#onActivityResult(int, int, Intent)}. If you still don't have the right
         * permissions, then the request was cancelled.
         */
        fun setNegativeButton(@Nullable text: String?): Builder {
            mNegativeButtonText = text
            return this
        }

        /**
         * Set the negative button text, default is [android.R.string.cancel].
         */
        fun setNegativeButton(@StringRes textId: Int): Builder {
            mNegativeButtonText = mContext.getString(textId)
            return this
        }

        /**
         * Set the request code use when launching the Settings screen for result, can be retrieved
         * in the calling Activity's {@see Activity#onActivityResult(int, int, Intent)} method.
         * Default is [.DEFAULT_SETTINGS_REQ_CODE].
         */
        fun setRequestCode(requestCode: Int): Builder {
            mRequestCode = requestCode
            return this
        }

        /**
         * Set whether the settings screen should be opened in a separate task. This is achieved by
         * setting [android.content.Intent.FLAG_ACTIVITY_NEW_TASK] on
         * the Intent used to open the settings screen.
         */
        fun setOpenInNewTask(openInNewTask: Boolean): Builder {
            mOpenInNewTask = openInNewTask
            return this
        }

        /**
         * Build the [AppSettingsDialog] from the specified options. Generally followed by a
         * call to [AppSettingsDialog.show].
         */
        fun build(): AppSettingsDialog {
            mRationale =
                if (TextUtils.isEmpty(mRationale)) mContext.getString(R.string.rationale_ask_again) else mRationale
            mTitle =
                if (TextUtils.isEmpty(mTitle)) mContext.getString(R.string.title_settings_dialog) else mTitle
            mPositiveButtonText =
                if (TextUtils.isEmpty(mPositiveButtonText)) mContext.getString(android.R.string.ok) else mPositiveButtonText
            mNegativeButtonText =
                if (TextUtils.isEmpty(mNegativeButtonText)) mContext.getString(android.R.string.cancel) else mNegativeButtonText
            mRequestCode = if (mRequestCode > 0) mRequestCode else DEFAULT_SETTINGS_REQ_CODE
            var intentFlags = 0
            if (mOpenInNewTask) {
                intentFlags = intentFlags or Intent.FLAG_ACTIVITY_NEW_TASK
            }
            return AppSettingsDialog(
                mActivityOrFragment,
                mThemeResId,
                mRationale!!,
                mTitle!!,
                mPositiveButtonText!!,
                mNegativeButtonText!!,
                mRequestCode,
                intentFlags
            )
        }
    }

    companion object CREATOR : Parcelable.Creator<AppSettingsDialog> {

        private const val TAG = "EEP-AppSettingsDialog"
        val DEFAULT_SETTINGS_REQ_CODE = 16061
        val EXTRA_APP_SETTINGS = "extra_app_settings"

        fun fromIntent(intent: Intent, activity: Activity): AppSettingsDialog? {
            var dialog: AppSettingsDialog =
                intent.getParcelableExtra(EXTRA_APP_SETTINGS)

            // It's not clear how this could happen, but in the case that it does we should try
            // to avoid a runtime crash and just use the default dialog.
            // https://github.com/googlesamples/easypermissions/issues/278
            if (dialog == null) {
                Log.e(
                    TAG, "Intent contains null value for EXTRA_APP_SETTINGS: "
                            + "intent=" + intent
                            + ", "
                            + "extras=" + intent.extras
                )
                dialog = Builder(activity).build()
            }
            dialog.setActivityOrFragment(activity)
            return dialog
        }

        override fun createFromParcel(parcel: Parcel): AppSettingsDialog {
            return AppSettingsDialog(parcel)
        }

        override fun newArray(size: Int): Array<AppSettingsDialog?> {
            return arrayOfNulls(size)
        }

    }
}