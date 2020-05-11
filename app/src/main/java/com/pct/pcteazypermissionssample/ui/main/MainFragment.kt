package com.pct.pcteazypermissionssample.ui.main

import android.Manifest
import android.app.AlertDialog
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.pct.extraeazypermissions.common.model.PermissionResult
import com.pct.extraeazypermissions.livedatapermission.PermissionManager
import com.pct.extraeazypermissions.ui.AppSettingsDialog
//import com.eazypermissions.common.model.PermissionResult
//import com.eazypermissions.livedatapermission.PermissionManager
import com.pct.pcteazypermissionssample.R
import com.pct.pcteazypermissionssample.databinding.MainFragmentBinding
import kotlinx.android.synthetic.main.main_fragment.*

class MainFragment : Fragment(), PermissionManager.PermissionObserver {

    companion object {
        fun newInstance() = MainFragment()
    }

    private lateinit var viewModel: MainViewModel

    private lateinit var binding: MainFragmentBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        binding = MainFragmentBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)
        // TODO: Use the ViewModel
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        location_permission_btn.setOnClickListener {
            if (PermissionManager.hasPermissions(requireContext(),
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
                )) {
                Toast.makeText(requireContext(), "Has Location Permission", Toast.LENGTH_SHORT).show()
            } else {
                PermissionManager.requestPermissions(this, 1, Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }

        contact_permission_btn.setOnClickListener {
            PermissionManager.requestPermissions(this, 2, Manifest.permission.READ_CONTACTS)
        }

        camera_permission_btn.setOnClickListener {
            PermissionManager.requestPermissions(this, 3, Manifest.permission.CAMERA)
        }

        location_contact_camera_permission_btn.setOnClickListener {
            PermissionManager.requestPermissions(
                    this,
                    4,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.READ_CONTACTS,
                    Manifest.permission.CAMERA
            )
        }
    }

    override fun setupObserver(permissionResultLiveData: LiveData<PermissionResult>) {
        permissionResultLiveData.observe(this, Observer {
            when (it) {
                is PermissionResult.PermissionGranted -> {
                    Toast.makeText(requireContext(), "Granted", Toast.LENGTH_SHORT).show()
                }
                is PermissionResult.PermissionDenied -> {
                    Toast.makeText(requireContext(), "Denied", Toast.LENGTH_SHORT).show()
                }
                is PermissionResult.ShowRational -> {
                    val alertDialogBuilder = AlertDialog.Builder(requireContext())
                            .setMessage("We need permission")
                            .setTitle("Rational")
                            .setNegativeButton("Cancel") { dialog, _ ->
                                dialog.dismiss()
                            }
                    when (it.requestCode) {
                        1 -> {
                            alertDialogBuilder
                                    .setPositiveButton("OK") { _, _ ->
                                        PermissionManager.requestPermissions(
                                                this,
                                                1,
                                                Manifest.permission.ACCESS_FINE_LOCATION
                                        )
                                    }.create().show()
                        }
                        2 -> {
                            alertDialogBuilder
                                    .setPositiveButton("OK") { _, _ ->
                                        PermissionManager.requestPermissions(
                                                this,
                                                2,
                                                Manifest.permission.READ_CONTACTS
                                        )
                                    }.create().show()
                        }
                        3 -> {
                            alertDialogBuilder
                                    .setPositiveButton("OK") { _, _ ->
                                        PermissionManager.requestPermissions(
                                                this,
                                                3,
                                                Manifest.permission.CAMERA
                                        )
                                    }.create().show()
                        }
                        4 -> {
                            alertDialogBuilder
                                    .setPositiveButton("OK") { _, _ ->
                                        PermissionManager.requestPermissions(
                                                this,
                                                4,
                                                Manifest.permission.ACCESS_FINE_LOCATION,
                                                Manifest.permission.READ_CONTACTS,
                                                Manifest.permission.CAMERA
                                        )
                                    }.create().show()
                        }
                    }
                }
                is PermissionResult.PermissionDeniedPermanently -> {
//                    Toast.makeText(requireContext(), "Denied permanently", Toast.LENGTH_SHORT).show()
                    AppSettingsDialog.Builder(this).setTitle(R.string.title_settings_dialog_vi).setRationale(R.string.rational_ask_vi).build().show()
                }
            }
        })
    }
}
