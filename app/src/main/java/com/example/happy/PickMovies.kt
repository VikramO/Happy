package com.example.happy

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.example.happy.databinding.FragmentMainBinding
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.android.gms.tasks.OnCompleteListener;
import android.view.View
import android.widget.EditText
import android.widget.TextView
import com.example.happy.databinding.FragmentMoviesBindingImpl
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import android.os.CountDownTimer
import com.google.firebase.firestore.DocumentSnapshot

class UserPickMovies : Fragment() {

    val db = Firebase.firestore
    val movies = db.collection("movies")
    val user_movies = db.collection("user_movies")
    val user_friends = db.collection("user_friends")

    val get_movies_list = movies.get().addOnCompleteListener(OnCompleteListener<QuerySnapshot?> {
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


    private lateinit var binding: FragmentMoviesBindingImpl

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_movies, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val timer = timer()
        timer.start()

        var movie_name = view.findViewById(R.id.movie_name) as TextView
        var btn_movie_get_movie = view.findViewById(R.id.btn_movie_get_movie) as Button

        btn_movie_get_movie.setOnClickListener {
            val movies_list = get_movies_list.result
            val size = movies_list?.size()
            val random = (0..size!!.minus(1)).random()
            val movie = movies_list?.documents?.get(random)

            movie_name.setText("" + (movie?.get("title") ?: String) + "")
        }

    }


    fun timer(): CountDownTimer = object : CountDownTimer(60000, 1000) {

        var time_text = view?.findViewById(R.id.time_text) as TextView

        override fun onTick(millisUntilFinished: Long) {
            time_text.setText("Seconds remaining to find a movie: " + millisUntilFinished / 1000)
        }

        override fun onFinish() {
            time_text.setText("Time's up!")
        }
    }

}









