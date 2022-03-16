package com.uaeh.aame.cobmaestros.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.uaeh.aame.cobmaestros.R
import helper.SessionManager

class DataUserFragment: Fragment() {

    // TAG
    private val TAG = DataUserFragment::class.simpleName

    // Variable session Manager
    private lateinit var session : SessionManager

    // Text View y Button
    private lateinit var tvNombre: TextView
    private lateinit var tvApellido: TextView
    private lateinit var tvMateria: TextView
    private lateinit var tvEmail: TextView
    private lateinit var tvTelefono: TextView
    private lateinit var btEditar: Button

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        session = SessionManager(requireActivity().applicationContext)
        val root = inflater.inflate(R.layout.fragment_data_user, container, false)

        // Text Views y Boton
        tvNombre = root.findViewById(R.id.tvNombres)
        tvApellido = root.findViewById(R.id.tvApellidos)
        tvMateria = root.findViewById(R.id.tvMateria)
        tvEmail = root.findViewById(R.id.tvEmail)
        tvTelefono = root.findViewById(R.id.tvTelefono)
        btEditar = root.findViewById(R.id.btnToEditData)

        // Set Datos del Usuario
        tvNombre.text = session.getNombre()
        tvApellido.text = session.getApellidos()
        tvMateria.text = session.getMateria()
        tvEmail.text = session.getMail()
        tvTelefono.text = session.getTel()

        btEditar.setOnClickListener() {
            val action = DataUserFragmentDirections.actionNavDataUserToNavUpdateUser()
            findNavController().navigate(action)
        }

        return root
    }
}