package com.uaeh.aame.cobmaestros.ui

import adapter.Calificacion
import android.app.Activity
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import app.AppConfig
import app.AppController
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.uaeh.aame.cobmaestros.R
import helper.SessionManager
import org.json.JSONException
import org.json.JSONObject

class CargarCalificacionesFrg : Fragment() {

    // TAG Session AppConfig
    private val TAG = CargarCalificacionesFrg::class.simpleName
    private val appConfig = AppConfig()
    private lateinit var session: SessionManager

    // Botones y Edit text
    private lateinit var btnAddCal: Button
    private lateinit var btnUpdcal: Button
    private lateinit var btnParcial1: Button
    private lateinit var btnParcial2: Button
    private lateinit var btnParcial3: Button
    private lateinit var etCalif: EditText
    private lateinit var tvNombre: TextView
    private lateinit var tvNumParcial: TextView
    private lateinit var tvAspecto: TextView
    private lateinit var lyCalif: ConstraintLayout
    private lateinit var lyEscala: LinearLayout

    // List view adaptador
    private lateinit var listCalif: ListView
    private lateinit var adaptador: Calificacion
    private val idAsp = ArrayList<String>()
    private val aspe = ArrayList<String>()
    private val valor = ArrayList<String>()
    private val calificacion = ArrayList<String>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = layoutInflater.inflate(R.layout.fragment_cargar_calificaciones, container, false)
        session = SessionManager(requireActivity().applicationContext)
        lyCalif = root.findViewById(R.id.ly_calificacion)
        lyCalif.visibility = View.INVISIBLE

        lyEscala = root.findViewById(R.id.ly_escala)
        lyEscala.visibility = View.INVISIBLE

        // Botones
        btnAddCal = root.findViewById(R.id.btn_add_calificacion)
        btnUpdcal = root.findViewById(R.id.btn_update_calificacion)
        btnParcial1 = root.findViewById(R.id.btn_1_parcial)
        btnParcial2 = root.findViewById(R.id.btn_2_parcial)
        btnParcial3 = root.findViewById(R.id.btn_3_parcial)

        // Text
        etCalif = root.findViewById(R.id.et_calificacion)
        tvNombre = root.findViewById(R.id.tv_alum_nombre)
        tvNumParcial = root.findViewById(R.id.tv_num_parcial)
        tvAspecto = root.findViewById(R.id.tv_aspecto)

        // List view
        listCalif = root.findViewById(R.id.list_escala)
        adaptador = Calificacion(activity as Activity, idAsp, aspe, valor, calificacion)
        listCalif.adapter = adaptador

        val id_alum = arguments?.getString("id_alumno")
        val nomb_alum = arguments?.getString("nombre")
        var parcial = ""
        var posicion = 0
        Log.d(TAG, "el id del alumno es ${id_alum.toString()}")
        tvNombre.text = nomb_alum.toString()

        btnParcial1.setOnClickListener {
            idAsp.clear()
            aspe.clear()
            valor.clear()
            calificacion.clear()
            lyEscala.visibility = View.VISIBLE
            lyCalif.visibility = View.INVISIBLE
            getCalificaciones(session.getId(), id_alum.toString(), "1")
            tvNumParcial.text = "1er Parcial"
        }

        btnParcial2.setOnClickListener{
            idAsp.clear()
            aspe.clear()
            valor.clear()
            calificacion.clear()
            lyEscala.visibility = View.VISIBLE
            lyCalif.visibility = View.INVISIBLE
            getCalificaciones(session.getId(), id_alum.toString(), "2")
            tvNumParcial.text = "2do Parcial"
        }

        btnParcial3.setOnClickListener{
            idAsp.clear()
            aspe.clear()
            valor.clear()
            calificacion.clear()
            lyEscala.visibility = View.VISIBLE
            lyCalif.visibility = View.INVISIBLE
            getCalificaciones(session.getId(), id_alum.toString(), "3")
            tvNumParcial.text = "3er Parcial"
        }

        listCalif.setOnItemClickListener{
            adapterView, View, position, id ->
            lyCalif.visibility = android.view.View.VISIBLE
            tvAspecto.text = aspe[position]
            btnUpdcal.visibility = android.view.View.INVISIBLE
            btnAddCal.visibility = android.view.View.VISIBLE
            posicion = position
        }

        listCalif.setOnItemLongClickListener {
            adapterView, View, position, id ->
            lyCalif.visibility = android.view.View.VISIBLE
            tvAspecto.text = aspe[position]
            btnUpdcal.visibility = android.view.View.VISIBLE
            btnAddCal.visibility = android.view.View.INVISIBLE
            posicion = position
            true
        }

        btnAddCal.setOnClickListener{
            val cal = etCalif.text.toString()
            addCalif(idAsp[posicion], id_alum.toString(), cal, session.getId())
        }

        btnUpdcal.setOnClickListener{
            val calif = etCalif.text.toString()
            updateCalif(idAsp[posicion], id_alum.toString(), calif, posicion)
        }

        return root
    }

    private fun getCalificaciones(maestro: String, alumno: String, parcial: String){
        val tag_string_req = "req_get_Calificacion"
        val strReq: StringRequest = object : StringRequest(Method.POST, appConfig.URL_GET_CALIF, Response.Listener { response ->
            Log.d(TAG, "calificacion Respones: $response")
            try {
                val jObj = JSONObject(response.substring(response.indexOf("{"), response.length))
                val error = jObj.getBoolean("error_calif")
                Log.d(TAG, "Error response: $error")
                if (!error){
                    val tam = jObj.length()-1
                    for (indice in 0 until tam) {
                        val calif = jObj.getJSONObject(indice.toString())
                        idAsp.add(calif.getString("id_aspecto"))
                        aspe.add(calif.getString("aspecto"))
                        valor.add(calif.getString("porcentaje"))
                        calificacion.add(calif.getString("calificacion"))
                        Toast.makeText(
                            requireActivity().applicationContext,
                            "numero de calificaion = ${calificacion.last()}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    listCalif.adapter = adaptador
                } else {
                    val tam = jObj.length() - 1
                    for (indice in 0 until tam) {
                        val calif = jObj.getJSONObject(indice.toString())
                        idAsp.add(calif.getString("id_aspecto"))
                        aspe.add(calif.getString("aspecto"))
                        valor.add(calif.getString("porcentaje"))
                    }
                    Toast.makeText(requireActivity().applicationContext, "Puedes registrar las calificaiones del alumno", Toast.LENGTH_LONG).show()
                    listCalif.adapter = adaptador
                }
            } catch (e: JSONException){
                e.printStackTrace()
                Toast.makeText(requireActivity().applicationContext, "Json error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }, Response.ErrorListener { error ->
            Log.d(TAG, "Calificaciones Obtener Error: ${error.message}")
            Toast.makeText(requireActivity().applicationContext, "", Toast.LENGTH_SHORT).show()
        }) {
            override fun getParams(): MutableMap<String, String> {
                val params = HashMap<String, String>()
                params["maestro"] = maestro
                params["alumno"] = alumno
                params["parcial"] = parcial
                return params
            }
        }
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req)
    }

    private fun addCalif(aspecto: String, alumno: String, calific: String, maestro: String){
        val tag_string_req = "req_add_Calificacion"
        val strReq: StringRequest = object : StringRequest(Method.POST, appConfig.URL_ADD_CALIF, Response.Listener { response ->
            Log.d(TAG, "calificacion Respones: $response")
            try {
                val jObj = JSONObject(response.substring(response.indexOf("{"), response.length))
                val error = jObj.getBoolean("error")
                Log.d(TAG, "Error response: $error")
                if (!error) {
                    val calif = jObj.getString("calif")

                    calificacion.add(calif)
                    listCalif.adapter = adaptador

                    etCalif.setText("")
                    lyCalif.visibility = View.INVISIBLE

                    Toast.makeText(requireActivity(), "Calificacion agregado correctamente!", Toast.LENGTH_SHORT).show()
                } else {
                    val errorMsg = jObj.getString("error_msg")
                    Toast.makeText(requireActivity().applicationContext, errorMsg, Toast.LENGTH_SHORT).show()
                }
            } catch (e: JSONException){
                e.printStackTrace()
                Toast.makeText(requireActivity().applicationContext, "Json error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }, Response.ErrorListener { error ->
            Log.d(TAG, "Calificaciones add Error: ${error.message}")
            Toast.makeText(requireActivity().applicationContext, "", Toast.LENGTH_SHORT).show()
        }) {
            override fun getParams(): MutableMap<String, String> {
                val params = HashMap<String, String>()
                params["aspecto"] = aspecto
                params["alumno"] = alumno
                params["calificacion"] = calific
                params["maestro"] = maestro
                return params
            }
        }
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req)
    }

    private fun updateCalif(aspecto: String, alumno: String, calific: String, pos: Int) {
        val tag_string_req = "req_update_Calificacion"
        val strReq: StringRequest = object : StringRequest(Method.POST, appConfig.URL_UPDATE_CALIF, Response.Listener { response ->
            Log.d(TAG, "calificacion Respones: $response")
            try {
                val jObj = JSONObject(response.substring(response.indexOf("{"), response.length))
                val error = jObj.getBoolean("error")
                Log.d(TAG, "Error response: $error")
                if (!error) {
                    val calif = jObj.getString("calif")

                    calificacion[pos] = calif
                    adaptador.notifyDataSetChanged()

                    etCalif.setText("")
                    lyCalif.visibility = View.INVISIBLE

                    Toast.makeText(requireActivity(), "Calificacion agregado correctamente!", Toast.LENGTH_SHORT).show()
                } else {
                    val errorMsg = jObj.getString("error_msg")
                    Toast.makeText(requireActivity().applicationContext, errorMsg, Toast.LENGTH_SHORT).show()
                }
            } catch (e: JSONException){
                e.printStackTrace()
                Toast.makeText(requireActivity().applicationContext, "Json error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }, Response.ErrorListener { error ->
            Log.d(TAG, "Calificaciones update Error: ${error.message}")
            Toast.makeText(requireActivity().applicationContext, "", Toast.LENGTH_SHORT).show()
        }) {
            override fun getParams(): MutableMap<String, String> {
                val params = HashMap<String, String>()
                params["aspecto"] = aspecto
                params["alumno"] = alumno
                params["calificacion"] = calific
                return params
            }
        }
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req)
    }
}