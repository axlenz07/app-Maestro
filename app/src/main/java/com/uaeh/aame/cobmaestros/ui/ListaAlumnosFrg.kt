package com.uaeh.aame.cobmaestros.ui

import adapter.Alumno
import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import app.AppConfig
import app.AppController
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.uaeh.aame.cobmaestros.R
import helper.SessionManager
import org.json.JSONException
import org.json.JSONObject

class ListaAlumnosFrg : Fragment() {

    // TAG
    private val TAG = ListaAlumnosFrg::class.simpleName
    private val appConfig = AppConfig()
    private lateinit var session: SessionManager

    // Arreglos
    private lateinit var Id: ArrayList<String>
    private lateinit var Nombre: ArrayList<String>
    private lateinit var Padre: ArrayList<String>

    // Adaptador y List View
    private lateinit var listView: ListView
    private lateinit var adaptador: Alumno

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_lista_alumnos, container, false)

        // Inicializacion de arreglos
        Id = ArrayList()
        Nombre = ArrayList()
        Padre = ArrayList()

        // findView para lista de alumnos
        listView = root.findViewById(R.id.listaAlumnos)
        session = SessionManager(requireActivity().applicationContext)

        adaptador = Alumno(activity as Activity, Id, Nombre)
        listView.adapter = adaptador

        var Pos = 0
        listView.setOnItemClickListener{
            adapterView, View, position, id ->
            session.saveIdPadre(Padre[position])
            val action = ListaAlumnosFrgDirections.actionNavListAlumToNavDetallePadre()
            findNavController().navigate(action)
        }
        getAlumnos()
        return root
    }

    private fun getAlumnos(){
        val tag_string_req = "req_get_Alumnos"
        val strReq: StringRequest = object : StringRequest(Method.POST, appConfig.URL_OBTENER_ALUMNOS, Response.Listener { response ->
            Log.d(TAG, "Alumnos Respones: $response")
            try {
                val jObj = JSONObject(response.substring(response.indexOf("{"), response.length))
                val error = jObj.getBoolean("error")
                Log.d(TAG, "Error response: $error")
                if (!error){
                    val tam = jObj.length()-1
                    for (indice in 0 until tam) {
                        val alumno = jObj.getJSONObject(indice.toString())
                        Id.add(alumno.getString("id_alumno"))
                        Nombre.add(alumno.getString("nombre_completo"))
                        Padre.add(alumno.getString("padre"))
                    }
                    listView.adapter = adaptador
                } else {
                    val errorMsg = jObj.getString("error_msg")
                    Toast.makeText(requireActivity().applicationContext, errorMsg, Toast.LENGTH_SHORT).show()
                }
            } catch (e: JSONException){
                e.printStackTrace()
                Toast.makeText(requireActivity().applicationContext, "Json error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }, Response.ErrorListener { error ->
            Log.d(TAG, "Alumnos Obtener Error: ${error.message}")
            Toast.makeText(requireActivity().applicationContext, "", Toast.LENGTH_SHORT).show()
        }) {
        }
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req)
    }
}