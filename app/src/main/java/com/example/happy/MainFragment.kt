package com.example.happy

import android.app.Activity
import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import android.content.pm.PackageManager
import com.example.happy.databinding.FragmentMainBinding
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.findNavController

class MainFragment : Fragment() {


    val TAG = "MainFragment"
    val SIGN_IN_RESULT_CODE = 1001


    // Get a reference to the ViewModel scoped to this Fragment
    private val viewModel by viewModels<LoginViewModel>()
    private lateinit var binding: FragmentMainBinding
    private lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_main, container, false)

        // TODO Remove the two lines below once observeAuthenticationState is implemented.
        binding.welcomeText.text = viewModel.getFactToDisplay(requireContext())
        binding.authButton.text = getString(R.string.login_btn)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeAuthenticationState()

        binding.authButton.setOnClickListener { launchSignInFlow() }

        var btn_settings = view.findViewById(R.id.btn_settings) as? Button
        var btn_map = view.findViewById(R.id.btn_map) as? Button
        var btn_movie = view.findViewById(R.id.btn_movie) as? Button

        btn_settings?.setOnClickListener { view ->
            view.findNavController().navigate(R.id.settings_fragment)
        }

        // set on-click listener
        btn_map?.setOnClickListener {
                view ->
            view.findNavController().navigate(R.id.map_activity)
        }

        btn_movie?.setOnClickListener {
                view ->
            view.findNavController().navigate(R.id.movies_fragment)
        }

        var location = view.findViewById(R.id.location) as? Button
        var storage = view.findViewById(R.id.storage) as? Button
        var network = view.findViewById(R.id.network) as? Button

        // Set Buttons on Click Listeners

        location?.setOnClickListener(View.OnClickListener {
            checkPermission(Manifest.permission.ACCESS_FINE_LOCATION,
                LOCATION_PERMISSION_CODE)
        })

        storage?.setOnClickListener(View.OnClickListener {
            checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                STORAGE_PERMISSION_CODE)
        })

        network?.setOnClickListener(View.OnClickListener {
            checkPermission(Manifest.permission.ACCESS_NETWORK_STATE,
                NETWORK_PERMISSION_CODE)
        })



    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SIGN_IN_RESULT_CODE) {
            val response = IdpResponse.fromResultIntent(data)
            if (resultCode == Activity.RESULT_OK) {
                // User successfully signed in
                Log.i(
                    TAG,
                    "Successfully signed in user ${FirebaseAuth.getInstance().currentUser?.displayName}!"
                )
            } else {
                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                // response.getError().getErrorCode() and handle the error.
                Log.i(TAG, "Sign in unsuccessful ${response?.error?.errorCode}")
            }
        }
    }

    /**
     * Observes the authentication state and changes the UI accordingly.
     * If there is a logged in user: (1) show a logout button and (2) display their name.
     * If there is no logged in user: show a login button
     */
    private fun observeAuthenticationState() {
        val factToDisplay = viewModel.getFactToDisplay(requireContext())

        viewModel.authenticationState.observe(viewLifecycleOwner, Observer { authenticationState ->
            when (authenticationState) {
                LoginViewModel.AuthenticationState.AUTHENTICATED -> {
                    binding.welcomeText.text = getWelcomeMessage(factToDisplay)

                    binding.authButton.text = getString(R.string.logout_button_text)
                    binding.authButton.setOnClickListener {
                        AuthUI.getInstance().signOut(requireContext())
                    }
                }
                else -> {
                    binding.welcomeText.text = factToDisplay

                    binding.authButton.text = getString(R.string.login_button_text)
                    binding.authButton.setOnClickListener {
                        launchSignInFlow()
                    }
                }
            }
        })
    }


    private fun getWelcomeMessage(fact: String): String {
        return String.format(
            resources.getString(
                R.string.welcome_message_authed,
                FirebaseAuth.getInstance().currentUser?.displayName
            )
        )
    }

    private fun launchSignInFlow() {
        // Give users the option to sign in / register with their email
        // If users choose to register with their email,
        // they will need to create a password as well
        val providers = arrayListOf(
            AuthUI.IdpConfig.EmailBuilder().build()
            //
        )

        // Create and launch sign-in intent.
        // We listen to the response of this activity with the
        // SIGN_IN_RESULT_CODE code
        startActivityForResult(
            AuthUI.getInstance().createSignInIntentBuilder().setAvailableProviders(
                providers
            ).build(), SIGN_IN_RESULT_CODE
        )
    }

    fun checkPermission(permission: String, requestCode: Int) {
        if (ContextCompat.checkSelfPermission(requireActivity(), permission)
            == PackageManager.PERMISSION_DENIED
        ) {

            // Requesting the permission
            requestPermissions(arrayOf(permission),
                requestCode)
        } else {
            Toast.makeText(requireActivity(),
                "Permission already granted",
                Toast.LENGTH_SHORT)
                .show()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray,
    ) {
        super
            .onRequestPermissionsResult(requestCode,
                permissions,
                grantResults)
        if (requestCode == MainFragment.Companion.LOCATION_PERMISSION_CODE) {
            if (grantResults.size > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED
            ) {
                Toast.makeText(requireActivity(),
                    "Camera Permission Granted",
                    Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireActivity(),
                    "Camera Permission Denied",
                    Toast.LENGTH_SHORT).show()
            }
        } else if (requestCode == MainFragment.Companion.STORAGE_PERMISSION_CODE) {
            if (grantResults.size > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED
            ) {
                Toast.makeText(requireActivity(),
                    "Storage Permission Granted",
                    Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireActivity(),
                    "Storage Permission Denied",
                    Toast.LENGTH_SHORT).show()
            }
        } else if (requestCode == MainFragment.Companion.NETWORK_PERMISSION_CODE) {
            if (grantResults.size > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED
            ) {
                Toast.makeText(requireActivity(),
                    "Network Permission Granted",
                    Toast.LENGTH_SHORT)
                    .show()
            } else {
                Toast.makeText(requireActivity(),
                    "Network Permission Denied",
                    Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    companion object {
        // Defining Permission codes.
        // We can give any value
        // but unique for each permission.
        private const val LOCATION_PERMISSION_CODE = 100
        private const val STORAGE_PERMISSION_CODE = 101
        private const val NETWORK_PERMISSION_CODE = 102
    }


}