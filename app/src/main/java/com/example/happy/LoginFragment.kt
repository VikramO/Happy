package com.example.happy

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.activity.addCallback
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.example.happy.databinding.FragmentLoginBinding
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.ktx.Firebase
import com.google.android.gms.tasks.OnCompleteListener
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import kotlinx.coroutines.tasks.await

class LoginFragment : Fragment() {

    companion object {
        const val TAG = "LoginFragment"
        const val SIGN_IN_RESULT_CODE = 1001
    }

    // Get a reference to the ViewModel scoped to this Fragment.
    val viewModel by viewModels<LoginViewModel>()
    private lateinit var navController: NavController

    val db = Firebase.firestore
    val users = db.collection("users")

    val get_user_list = users.get().addOnCompleteListener(OnCompleteListener<QuerySnapshot?> {
        fun onComplete(task: Task<QuerySnapshot>) {
            if (task.isSuccessful) {
                val list: MutableList<String> = ArrayList()
                for (document in task.result!!) {
                    list.add(document.id)
                }
            } else {
            }
        }
    }
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment.
        val binding = DataBindingUtil.inflate<FragmentLoginBinding>(
            inflater, R.layout.fragment_login, container, false
        )

        binding.authButton.setOnClickListener { launchSignInFlow() }

        return binding.root


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    private fun launchSignInFlow() {
        // Give users the option to sign in / register with their email or Google account. If users
        // choose to register with their email, they will need to create a password as well.
        val providers = arrayListOf(
            AuthUI.IdpConfig.EmailBuilder().build(), AuthUI.IdpConfig.GoogleBuilder().build()
        )

        // Create and launch sign-in intent. We listen to the response of this activity with the
        // SIGN_IN_RESULT_CODE code.
        startActivityForResult(
            AuthUI.getInstance().createSignInIntentBuilder().setAvailableProviders(
                providers
            ).build(), SIGN_IN_RESULT_CODE
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SIGN_IN_RESULT_CODE) {

            GlobalScope.launch {
                navController = findNavController()
                navController.navigate(R.id.main_fragment)

                val response = IdpResponse.fromResultIntent(data)

                val name = FirebaseAuth.getInstance().currentUser?.getDisplayName().toString()
                val identifier = FirebaseAuth.getInstance().currentUser?.getEmail().toString()

                val user_list = get_user_list.await().documents

                val get_current_user = users.whereEqualTo("email", identifier).get()
                    .addOnCompleteListener(OnCompleteListener<QuerySnapshot?> {
                        fun onComplete(task: Task<QuerySnapshot>) {
                            if (task.isSuccessful) {
                                val list: MutableList<String> = ArrayList()
                                for (document in task.result!!) {
                                    list.add(document.id)
                                }
                            }
                        }
                    }
                    )

                val current_user = get_current_user.await().documents.get(0).id

                val user_query = users.document(current_user).get().await().exists()

                val user_list_size = user_list.size

                if (user_list.orEmpty().isEmpty()) {
                    val user = hashMapOf(
                        "full_name" to name,
                        "email" to identifier,
                        "index" to user_list_size
                    )
                    users.add(user)
                    db.collection("user_count").document("count")
                        .update("count", FieldValue.increment(1))

                } else if (user_query == false) {
                    val user = hashMapOf(
                        "full_name" to current_user,
                        "email" to identifier,
                        "index" to user_list_size
                    )
                    users.add(user)
                    db.collection("user_count").document("count")
                        .update("count", FieldValue.increment(1))
                }

                if (resultCode == Activity.RESULT_OK) {
                    // Successfully signed in user.
                    Log.i(
                        TAG,
                        "Successfully signed in user " +
                                "${FirebaseAuth.getInstance().currentUser?.displayName}!"

                    )

                } else {
                    // Sign in failed. If response is null the user canceled the sign-in flow using
                    // the back button. Otherwise check response.getError().getErrorCode() and handle
                    // the error.
                    Log.i(TAG, "Sign in unsuccessful ${response?.error?.errorCode}")
                }
            }
        }
    }
}
