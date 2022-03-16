package com.uaeh.aame.cobmaestros.ui

import adapter.Alumno
import adapter.AsisAlumno
import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import app.AppConfig
import app.AppController
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.uaeh.aame.cobmaestros.R
import helper.SessionManager
import org.json.JSONException
import org.json.JSONObject

class AlumnoAsistenciasFrg : Fragment(){

    // Session TAG
    private val TAG = AlumnoAsistenciasFrg::class.simpleName
    private lateinit var session: SessionManager
    private val appConfig = AppConfig()
    private lateinit var tvNombre: TextView
    private lateinit var btnAsis: Button
    private lateinit var btnAusen: Button

    // Arrays Lista de alumnos con asistencia
    private lateinit var listAsistencias: ListView
    private lateinit var adapAsistencias: AsisAlumno
    private lateinit var idAlumno : ArrayList<String>
    private lateinit var alumNombre: ArrayList<String>
    private lateinit var alumAsistencia: ArrayList<Int>

    // Arrays Lista de alumnos sin asistencia
    private lateinit var listAlumno: ListView
    private lateinit var adapAlumno: Alumno
    private lateinit var idAlum: ArrayList<String>
    private lateinit var alumNom: ArrayList<String>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = layoutInflater.inflate(R.layout.fragment_asis_todos_alumno, container, false)
        val id_fecha = arguments?.getString("id_fecha")
        session = SessionManager(requireActivity().applicationContext)
        tvNombre = root.findViewById(R.id.Alum_Nombre)
        btnAsis = root.findViewById(R.id.btn_add_asist)
        btnAusen = root.findViewById(R.id.btn_add_ausente)

        var posicion = 0
        var lista = ""

        // Inicializar arrays Asistencias
        idAlumno = ArrayList()
        alumNombre = ArrayList()
        alumAsistencia = ArrayList()
        listAsistencias = root.findViewById(R.id.list_asis_alumno)
        adapAsistencias = AsisAlumno(activity as Activity, idAlumno, alumNombre, alumAsistencia)
        listAsistencias.adapter = adapAsistencias

        // Inicializar arrays lista alumnos
        idAlum = ArrayList()
        alumNom = ArrayList()
        listAlumno = root.findViewById(R.id.list_alumnos)
        adapAlumno = Alumno(activity as Activity, idAlum, alumNom)
        listAlumno.adapter = adapAlumno

        listAlumno.setOnItemClickListener{
            adapterView, View, position, id ->
            tvNombre.text = alumNom[position]
            posicion = position
            lista = "alumnos"
        }

        listAsistencias.setOnItemClickListener{
            adapterView, View, position, id ->
            tvNombre.text = alumNombre[position]
            posicion = position
            Toast.makeText(requireActivity().applicationContext, "Actualizar o Cambiar Asistencia del alumno", Toast.LENGTH_LONG).show()
            lista = "asistencias"
        }

        btnAsis.setOnClickListener{
            tvNombre.setText(R.string.tv_nombre_asistencia)
            if (lista == "alumnos")
                addAsistencia("asis", id_fecha.toString(), session.getId(), idAlum[posicion], posicion)
            else if (lista == "asistencias")
                updateAsistencia(id_fecha.toString(), idAlumno[posicion], "asis", posicion)
        }

        btnAusen.setOnClickListener{
            tvNombre.setText(R.string.tv_nombre_asistencia)
            if (lista == "alumnos")
                addAsistencia("ausen", id_fecha.toString(), session.getId(), idAlum[posicion], posicion)
            else if (lista == "asistencias")
                updateAsistencia(id_fecha.toString(), idAlumno[posicion], "ausen", posicion)
        }

        getAsistencias(id_fecha.toString())

        return root
    }

    private fun getAsistencias(id_fech: String) {
        val tag_string_req = "req_get_Asistencias"
        val strReq: StringRequest = object : StringRequest(Method.POST, appConfig.URL_GET_ASISTENCIAS, Response.Listener { response ->
            Log.d(TAG, "Get Fecha Respones: $response")
            try {
                val jObj = JSONObject(response.substring(response.indexOf("{"), response.length))
                val error = jObj.getBoolean("error_asis")
                Log.d(TAG, "Error response: $error")
                if (!error){
                    if (jObj.length() != 1) {
                        val tam = jObj.length() - 1
                        for (indice in 0 until tam) {
                            val asis = jObj.getJSONObject(indice.toString())
                            idAlumno.add(asis.getString("id_alumno"))
                            alumNombre.add(asis.getString("nombre_completo"))
                            alumAsistencia.add(asis.getInt("asistencia"))
                        }
                        listAsistencias.adapter = adapAsistencias
                    }
                } else {
                    if (jObj.length() != 1) {
                        val tam = jObj.length() - 1
                        for (i in 0 until tam) {
                            val alum = jObj.getJSONObject(i.toString())
                            idAlum.add(alum.getString("id_alumno"))
                            alumNom.add(alum.getString("nombre_completo"))
                        }
                        listAlumno.adapter = adapAlumno
                        Toast.makeText(
                            requireActivity().applicationContext,
                            "Puedes tomar lista ahora",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            } catch (e: JSONException){
                e.printStackTrace()
                Toast.makeText(requireActivity().applicationContext, "Json error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }, Response.ErrorListener { error ->
            Log.d(TAG, "Fecha Obtener Error: ${error.message}")
            Toast.makeText(requireActivity().applicationContext, "", Toast.LENGTH_SHORT).show()
        }) {
            override fun getParams(): MutableMap<String, String> {
                val params = HashMap<String, String>()
                params["id_fecha"] = id_fech
                return params
            }
        }
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req)
    }

    private fun addAsistencia(asistencia: String, id_fech: String, id_maes: String, id_alumn: String, posicion: Int){
        val tag_string_req = "req_Add_Asistencias"
        val strReq: StringRequest = object : StringRequest(Method.POST, appConfig.URL_ADD_ASISTENCIA, Response.Listener { response ->
            Log.d(TAG, "Add Asistencia Respones: $response")
            try {
                val jObj = JSONObject(response.substring(response.indexOf("{"), response.length))
                val error = jObj.getBoolean("error")
                Log.d(TAG, "Error response: $error")
                if (!error){
                    val asis = jObj.getInt("asis")
                    idAlumno.add(idAlum[posicion])
                    alumNombre.add(alumNom[posicion])
                    alumAsistencia.add(asis)

                    idAlum.removeAt(posicion)
                    alumNom.removeAt(posicion)
                    adapAlumno.notifyDataSetChanged()

                    adapAsistencias.notifyDataSetChanged()
                }
            } catch (e: JSONException){
                e.printStackTrace()
                Toast.makeText(requireActivity().applicationContext, "Json error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }, Response.ErrorListener { error ->
            Log.d(TAG, "Fecha Obtener Error: ${error.message}")
            Toast.makeText(requireActivity().applicationContext, "", Toast.LENGTH_SHORT).show()
        }) {
            override fun getParams(): MutableMap<String, String> {
                val params = HashMap<String, String>()
                params["id_fecha"] = id_fech
                params["alumno"] = id_alumn
                params["asistencia"] = asistencia
                params["maestro"] = id_maes
                return params
            }
        }
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req)
    }

    private fun updateAsistencia(id_fecha: String, alumno: String, asistencia: String, posicion: Int) {
        val tag_string_req = "req_Edit_Asistencias"
        val strReq: StringRequest = object : StringRequest(Method.POST, appConfig.URL_UPDATE_ASISTENCIA, Response.Listener { response ->
            Log.d(TAG, "Edit Asistencia Respones: $response")
            try {
                val jObj = JSONObject(response.substring(response.indexOf("{"), response.length))
                val error = jObj.getBoolean("error")
                Log.d(TAG, "Error response: $error")
                if (!error){
                    val asis = jObj.getInt("asis")
                    alumAsistencia[posicion] = asis

                    adapAsistencias.notifyDataSetChanged()
                    Toast.makeText(requireActivity().applicationContext, "La informacion se actualizo correctamente!", Toast.LENGTH_SHORT)
                        .show()
                }
            } catch (e: JSONException){
                e.printStackTrace()
                Toast.makeText(requireActivity().applicationContext, "Json error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }, Response.ErrorListener { error ->
            Log.d(TAG, "Asistencia Edit Error: ${error.message}")
            Toast.makeText(requireActivity().applicationContext, "", Toast.LENGTH_SHORT).show()
        }) {
            override fun getParams(): MutableMap<String, String> {
                val params = HashMap<String, String>()
                params["id_fecha"] = id_fecha
                params["alumno"] = alumno
                params["asistencia"] = asistencia
                return params
            }
        }
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req)
    }
}