package com.pct.extraeazypermissions.livedatapermission

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.annotation.MainThread
import androidx.annotation.NonNull
import androidx.annotation.Size
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import com.pct.extraeazypermissions.common.BasePermissionManager
import com.pct.extraeazypermissions.common.model.PermissionResult


/**
 * Permission manager which handles checking permission is granted or not and if not then will request permission.
 * A headless fragment which wraps the boilerplate code for checking and requesting permission
 * and expose the result of permission request as [LiveData].
 * A simple [Fragment] subclass.
 */
class PermissionManager : BasePermissionManager() {

    private val permissionResultLiveEvent: SingleLiveEvent<PermissionResult> by lazy {
        SingleLiveEvent<PermissionResult>()
    }

    override fun onPermissionResult(permissionResult: PermissionResult) {
        permissionResultLiveEvent.postValue(permissionResult)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (parentFragment != null) {
            (parentFragment as PermissionObserver).setupObserver(permissionResultLiveEvent)
        } else {
            (context as PermissionObserver).setupObserver(permissionResultLiveEvent)
        }
    }

    companion object {

        private const val TAG = "EEP-PermissionManager"

        /**
         * A static factory method to request permission from activity.
         * Your activity must implement [PermissionObserver]
         *
         * @param activity an instance of [AppCompatActivity] which is also [PermissionObserver]
         * @param requestId Request ID for permission request
         * @param permissions Permission(s) to request
         *
         * @throws [IllegalArgumentException] if your activity doesn't implement [PermissionObserver]
         */
        @JvmStatic
        @MainThread
        fun requestPermissions(
            activity: AppCompatActivity,
            requestId: Int,
            vararg permissions: String
        ) {
            _requestPermissions(
                activity,
                requestId,
                *permissions
            )
        }

        /**
         * A static factory method to request permission from fragment.
         * Your fragment must implement [PermissionObserver]
         *
         * @param fragment an instance of [Fragment] which is also [PermissionObserver]
         * @param requestId Request ID for permission request
         * @param permissions Permission(s) to request
         *
         * @throws [IllegalArgumentException] if your fragment doesn't implement [PermissionObserver]
         */
        @JvmStatic
        @MainThread
        fun requestPermissions(
            fragment: Fragment,
            requestId: Int,
            vararg permissions: String
        ) {
            _requestPermissions(
                fragment,
                requestId,
                *permissions
            )
        }

        /**
         * Check if the calling context has a set of permissions.
         *
         * @param context the calling context.
         * @param perms   one ore more permissions, such as {@link Manifest.permission#CAMERA}.
         * @return true if all permissions are already granted, false if at least one permission is not
         * yet granted.
         * @see Manifest.permission
         */
        @JvmStatic
        fun hasPermissions(
            @NonNull context: Context,
            @Size(min = 1) @NonNull perms: Array<out String>
        ): Boolean {
            // Always return true for SDK < M, let the system deal with the permissions

            // Always return true for SDK < M, let the system deal with the permissions
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                Log.w(TAG, "hasPermissions: API version < M, returning true by default")

                // DANGER ZONE!!! Changing this will break the library.
                return true
            }

            // Null context may be passed if we have detected Low API (less than M) so getting
            // to this point with a null context should not be possible.

            // Null context may be passed if we have detected Low API (less than M) so getting
            // to this point with a null context should not be possible.
            requireNotNull(context) { "Can't check permissions for null context" }

            for (perm in perms) {
                if (ContextCompat.checkSelfPermission(context, perm!!)
                    != PackageManager.PERMISSION_GRANTED
                ) {
                    return false
                }
            }

            return true
        }

        private fun _requestPermissions(
            activityOrFragment: Any,
            requestId: Int,
            vararg permissions: String
        ) {
            val fragmentManager = if (activityOrFragment is AppCompatActivity) {
                activityOrFragment.supportFragmentManager
            } else {
                (activityOrFragment as Fragment).childFragmentManager
            }
            if (fragmentManager.findFragmentByTag(TAG) != null) {
                (fragmentManager.findFragmentByTag(TAG) as PermissionManager).requestPermissions(
                    requestId,
                    *permissions
                )
            } else {
                require(activityOrFragment is PermissionObserver) {
                    "Activity/Fragment must implement PermissionObserver"
                }
                val permissionManager = PermissionManager()
                fragmentManager.beginTransaction().add(
                    permissionManager,
                    TAG
                ).commitNow()
                permissionManager.requestPermissions(requestId, *permissions)
            }
        }

    }

    /**
     * Interface definition for a callback to get [LiveData] of [PermissionResult]
     *
     * Implement this interface to get [LiveData] for observing permission request result.
     */
    interface PermissionObserver {
        fun setupObserver(permissionResultLiveData: LiveData<PermissionResult>)
    }


}