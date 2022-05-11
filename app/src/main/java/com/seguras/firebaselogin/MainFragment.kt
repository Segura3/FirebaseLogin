package com.seguras.firebaselogin

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.Navigation
import com.google.android.material.navigation.NavigationBarItemView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.rpc.context.AttributeContext
import com.seguras.firebaselogin.databinding.FragmentMainBinding

class MainFragment : Fragment() {

    private lateinit var binding: FragmentMainBinding
    lateinit var autenticacion: FirebaseAuth
    lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        autenticacion = FirebaseAuth.getInstance()
        db = Firebase.firestore
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentMainBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onStart() {
        super.onStart()

        val user: FirebaseUser? = autenticacion.currentUser
        if(user == null){
            Navigation.findNavController(requireView()).navigate(R.id.action_mainFragment_to_loginFragment)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val user: FirebaseUser? = autenticacion.currentUser

        db.collection("usuarios")
            .whereEqualTo("id", user?.uid.toString())
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    Log.d(ContentValues.TAG, "${document.id} => ${document.data}")
                    binding.tvUserReg.text = document.data.get("Nombre").toString()
                }
            }
            .addOnFailureListener { exeption ->
                Log.w(ContentValues.TAG, "Error al obtener el usuario", exeption)
            }

        //binding.tvUserReg.text = user?.email ?: getString(R.string.user_def)

        binding.btnSignOut.setOnClickListener {
            autenticacion.signOut()
            if(autenticacion.currentUser==null){
                Navigation.findNavController(view).navigate(R.id.action_mainFragment_to_loginFragment)
            }else{
                Toast.makeText(activity,getString(R.string.fallo_logout), Toast.LENGTH_SHORT).show()
            }
        }
    }
}