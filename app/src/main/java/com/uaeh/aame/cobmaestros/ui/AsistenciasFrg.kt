package com.uaeh.aame.cobmaestros.ui

import adapter.Fecha
import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.os.bundleOf
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

class AsistenciasFrg : Fragment() {

    // Edit text
    private val TAG = AsistenciasFrg::class.simpleName
    private lateinit var etFecha: EditText
    private lateinit var tvInstruction: TextView
    private lateinit var session: SessionManager
    private val appConfig = AppConfig()

    // Botones
    private lateinit var btnAddFecha: Button
    private lateinit var btnUpdateFecha: Button
    private lateinit var btnDeleteFecha: Button

    // List view y arreglos
    private lateinit var Fechas: ListView
    private lateinit var adaptador: Fecha
    private lateinit var Id: ArrayList<String>
    private lateinit var Fecha: ArrayList<String>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_asistencias, container, false)
        etFecha = root.findViewById(R.id.et_Fecha)
        tvInstruction = root.findViewById(R.id.tvFecha)
        session = SessionManager(requireActivity().applicationContext)

        btnAddFecha = root.findViewById(R.id.btn_add_date)
        btnUpdateFecha = root.findViewById(R.id.btn_update_date)
        btnDeleteFecha = root.findViewById(R.id.btn_delete_date)

        btnUpdateFecha.visibility = View.INVISIBLE
        btnDeleteFecha.visibility = View.INVISIBLE

        // Iniciar array
        Id = ArrayList()
        Fecha = ArrayList()

        Fechas = root.findViewById(R.id.list_fechas)
        adaptador = Fecha(activity as Activity, Id, Fecha)
        Fechas.adapter = adaptador

        var posicion = 0
        Fechas.setOnItemClickListener{
                adapterView, View, position, id ->
            val fecha = adapterView.getItemAtPosition(position)
            val bundle = bundleOf("id_fecha" to fecha)
            findNavController().navigate(R.id.action_nav_asistencia_to_nav_alumn_asistencia, bundle)
            Id.clear()
            Fecha.clear()
            adaptador.notifyDataSetChanged()
        }

        Fechas.setOnItemLongClickListener{
            adapterView, View, position, id ->
            posicion = position
            etFecha.hint = Fecha[posicion]
            tvInstruction.text = "Actualiza o elimina la fecha"
            btnAddFecha.visibility = android.view.View.INVISIBLE
            btnUpdateFecha.visibility = android.view.View.VISIBLE
            btnDeleteFecha.visibility = android.view.View.VISIBLE
            true
        }

        btnAddFecha.setOnClickListener{
            if (etFecha.text.isEmpty()){
                Toast.makeText(requireActivity().applicationContext, "Falta la fecha para enviar.", Toast.LENGTH_SHORT).show()
            } else {
                val date = etFecha.text.toString()
                val maes = session.getId()
                addFecha(date, maes)
            }
        }

        
        /*btnEditFecha.setOnClickListener{
            if (etPosFecha.text.isEmpty())
                Toast.makeText(requireActivity().applicationContext, "Falta el numero del aviso a editar!", Toast.LENGTH_SHORT).show()
            else {
                val pos = etPosFecha.text.toString().toInt() - 1
                etFecha.hint = Fecha[pos]
                tvInstruction.text = "Actualiza o elimina la fecha"

            }
        }*/

        btnUpdateFecha.setOnClickListener{
            if (etFecha.text.isEmpty()) {
                Toast.makeText(requireActivity().applicationContext, "No se cambio la fecha", Toast.LENGTH_SHORT).show()
                etFecha.setText("")
                etFecha.hint = "31-12-20xx"
                tvInstruction.text = R.string.hint_add_fecha.toString()
                btnAddFecha.visibility = View.VISIBLE
                btnUpdateFecha.visibility = View.INVISIBLE
                btnDeleteFecha.visibility = View.INVISIBLE
            } else {
                val newDate = etFecha.text.toString()
                updateFecha(Id[posicion], newDate, posicion)
                Toast.makeText(requireActivity().applicationContext, "Se selecciono el item en la posicion $posicion", Toast.LENGTH_SHORT).show()
            }
        }

        btnDeleteFecha.setOnClickListener{
            etFecha.setText("")
            etFecha.hint = "31-12-20xx"
            tvInstruction.setText(R.string.hint_add_fecha)
            Toast.makeText(requireActivity().applicationContext, "id es ${Id[posicion]}", Toast.LENGTH_LONG).show()
            deleteFecha(Id[posicion], posicion)
        }

        getFechas(session.getId())

        return root
    }

    private fun getFechas(maestro: String){
        val tag_string_req = "req_get_Fecha"
        val strReq: StringRequest = object : StringRequest(Method.POST, appConfig.URL_GET_FECHAS, Response.Listener { response ->
            Log.d(TAG, "Get Fecha Respones: $response")
            try {
                val jObj = JSONObject(response.substring(response.indexOf("{"), response.length))
                val error = jObj.getBoolean("error")
                Log.d(TAG, "Error response: $error")
                if (!error){
                    if (jObj.length() != 1) {
                        val tam = jObj.length() - 1
                        for (indice in 0 until tam) {
                            val date = jObj.getJSONObject(indice.toString())
                            Id.add(date.getString("id_fecha"))
                            Fecha.add(date.getString("fecha"))
                        }
                        Fechas.adapter = adaptador
                    } else
                        Toast.makeText(
                            requireActivity().applicationContext,
                            "Aun no añaden fechas de asistencia.",
                            Toast.LENGTH_SHORT
                        ).show()
                } else {
                    val errorMsg = jObj.getString("error_msg")
                    Toast.makeText(requireActivity().applicationContext, errorMsg, Toast.LENGTH_SHORT).show()
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
                params["id_maes"] = maestro
                return params
            }
        }
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req)
    }

    private fun addFecha(date: String, maestro: String){
        val tag_string_req = "req_add_fecha"
        val strReq: StringRequest = object : StringRequest(Method.POST, appConfig.URL_ADD_FECHA, Response.Listener { response ->
            Log.d(TAG, "Fecha Add Response: $response")
            try {
                val jObj = JSONObject(response.substring(response.indexOf("{"), response.length))
                val error = jObj.getBoolean("error")
                Log.d(TAG, "Error response: $error")

                // Checar si es de a debis
                if (!error) {
                    val id_fecha = jObj.getString("id_fecha")
                    val fecha = jObj.getJSONObject("fecha")
                    val date = fecha.getString("fecha")

                    Id.add(id_fecha)
                    Fecha.add(date)
                    etFecha.setText("")
                    Fechas.adapter = adaptador

                    Toast.makeText(requireActivity(), "Fecha agregado correctamente!", Toast.LENGTH_SHORT).show()
                } else {
                    val errorMsg = jObj.getString("error_msg")
                    Toast.makeText(requireActivity().applicationContext, errorMsg, Toast.LENGTH_SHORT).show()
                }
            } catch (e: JSONException){
                e.printStackTrace()
                Toast.makeText(requireActivity().applicationContext, "Json error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }, Response.ErrorListener { error ->
            Log.d(TAG, "Fecha Agregar Error: ${error.message}")
            Toast.makeText(requireActivity().applicationContext, error.message, Toast.LENGTH_SHORT).show()
        }) {
            override fun getParams(): MutableMap<String, String> {
                val params = HashMap<String, String>()
                params["fecha"] = date
                params["maestro"] = maestro
                return params
            }
        }
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req)
    }

    private fun updateFecha(idFecha: String, newFecha: String, pos: Int) {
        val tag_string_req = "req_update_fecha"
        val strReq: StringRequest = object : StringRequest(Method.POST, appConfig.URL_UPDATE_FECHA, Response.Listener { response ->
            Log.d(TAG, "Fecha Update Response: $response")
            try {
                val jObj = JSONObject(response.substring(response.indexOf("{"), response.length))
                val error = jObj.getBoolean("error")
                Log.d(TAG, "Error response: $error")

                // Checar si es de a debis
                if (!error) {
                    val date = jObj.getString("fecha")
                    Fecha[pos] = date
                    etFecha.setText("")
                    etFecha.hint = "31-12-20xx"
                    Fechas.adapter = adaptador
                    btnAddFecha.visibility = View.VISIBLE
                    btnUpdateFecha.visibility = View.INVISIBLE
                    btnDeleteFecha.visibility = View.INVISIBLE

                    Toast.makeText(requireActivity(), "Fecha actualizada correctamente!", Toast.LENGTH_SHORT).show()
                } else {
                    val errorMsg = jObj.getString("error_msg")
                    Toast.makeText(requireActivity().applicationContext, errorMsg, Toast.LENGTH_SHORT).show()
                }
            } catch (e: JSONException){
                e.printStackTrace()
                Toast.makeText(requireActivity().applicationContext, "Json error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }, Response.ErrorListener { error ->
            Log.d(TAG, "Fecha Update Error: ${error.message}")
            Toast.makeText(requireActivity().applicationContext, error.message, Toast.LENGTH_SHORT).show()
        }) {
            override fun getParams(): MutableMap<String, String> {
                val params = HashMap<String, String>()
                params["id_fecha"] = idFecha
                params["fecha"] = newFecha
                return params
            }
        }
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req)
    }

    private fun deleteFecha(idFecha: String, pos: Int){
        val tag_string_req = "req_delete_fecha"
        val strReq: StringRequest = object : StringRequest(Method.POST, appConfig.URL_DELETE_FECHA, Response.Listener { response ->
            Log.d(TAG, "Fecha delete Response: $response")
            try {
                val jObj = JSONObject(response.substring(response.indexOf("{"), response.length))
                val error = jObj.getBoolean("error")
                Log.d(TAG, "Error response: $error")

                // Checar si es de a debis
                if (!error) {
                    Id.removeAt(pos)
                    Fecha.removeAt(pos)
                    adaptador.notifyDataSetChanged()

                    Fechas.adapter = adaptador
                    btnAddFecha.visibility = View.VISIBLE
                    btnUpdateFecha.visibility = View.INVISIBLE
                    btnDeleteFecha.visibility = View.INVISIBLE

                    Toast.makeText(requireActivity(), "Fecha eliminado correctamente!", Toast.LENGTH_SHORT).show()
                } else {
                    val errorMsg = jObj.getString("error_msg")
                    Toast.makeText(requireActivity().applicationContext, errorMsg, Toast.LENGTH_SHORT).show()
                }
            } catch (e: JSONException){
                e.printStackTrace()
                Toast.makeText(requireActivity().applicationContext, "Json error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }, Response.ErrorListener { error ->
            Log.d(TAG, "Fecha Delete Error: ${error.message}")
            Toast.makeText(requireActivity().applicationContext, error.message, Toast.LENGTH_SHORT).show()
        }) {
            override fun getParams(): MutableMap<String, String> {
                val params = HashMap<String, String>()
                params["id_fecha"] = idFecha
                return params
            }
        }
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req)
    }
}