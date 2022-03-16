package activity

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import app.AppConfig
import app.AppController
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.uaeh.aame.cobmaestros.R
import helper.SessionManager
import org.json.JSONException
import org.json.JSONObject

class RegisterActivity : AppCompatActivity() {

    // Logcat Tag
    private val TAG = RegisterActivity::class.simpleName

    // Botones
    private lateinit var btnRegistrar: Button
    private lateinit var btnToLogin: Button

    // Edit Text
    private lateinit var inputNombre: EditText
    private lateinit var inputApellido: EditText
    private lateinit var inputMateria: EditText
    private lateinit var inputTelefono: EditText
    private lateinit var inputEmail: EditText
    private lateinit var inputContraseña: EditText
    private lateinit var inputConfContraseña: EditText

    // Session Manager y DialogProgress
    private lateinit var pDialog: ProgressDialog
    private lateinit var session: SessionManager

    // Clase AppConfig
    private val appConfig = AppConfig()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // Find View Edit Text
        inputNombre = findViewById(R.id.et_nombres)
        inputApellido = findViewById(R.id.et_apellidos)
        inputMateria = findViewById(R.id.et_materia)
        inputTelefono = findViewById(R.id.et_telefono)
        inputEmail = findViewById(R.id.et_email)
        inputContraseña = findViewById(R.id.et_password)
        inputConfContraseña = findViewById(R.id.et_confirm_password)

        // Find View Buttons
        btnRegistrar = findViewById(R.id.btnRegistrar)
        btnToLogin = findViewById(R.id.btnToLogin)

        // Progress Dialog
        pDialog = ProgressDialog(this)
        pDialog.setCancelable(false)

        // Session Manager
        session = SessionManager(applicationContext)

        // Evento del boton toLogin
        btnToLogin.setOnClickListener {
            val intent = Intent(applicationContext, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        // Evento del boton Registrar
        btnRegistrar.setOnClickListener {
            val nombre = inputNombre.text.toString()
            val apellido = inputApellido.text.toString()
            val materia = inputMateria.text.toString()
            val celular = inputTelefono.text.toString()
            val email = inputEmail.text.toString()
            val contraseña = inputContraseña.text.toString()
            val confirm = inputConfContraseña.text.toString()

            if (nombre.isNotEmpty() && apellido.isNotEmpty() && materia.isNotEmpty() && celular.isNotEmpty() && email.isNotEmpty() && contraseña.isNotEmpty() && confirm.isNotEmpty()) {
                if (contraseña == confirm)
                    registrarMaestro(nombre, apellido, materia, celular, email, contraseña)
                else
                    Toast.makeText(applicationContext, "Tu Contraseña no coincide, por favor vuelve a intentar!", Toast.LENGTH_SHORT).show()
            } else
                Toast.makeText(applicationContext, "Por favor, ingresa todos los datos", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Funcion para almacenar los datos del maestro en la base de datos MySQL
     * Los parametros POST son tag, nombre, apellido, celular, email y contraseña al url de registro
     * */
    private fun registrarMaestro(nombre: String, apellido: String, materia: String, celular: String, email: String, contraseña: String) {
        // Tag usado para cancelar el request
        val tag_string_req = "req_register"

        pDialog.setMessage("Registrando usuario...")
        showDialog()

        val strReq: StringRequest = object : StringRequest(Method.POST, appConfig.URL_REGISTER, Response.Listener { response ->
            Log.d(TAG, "Register Response: $response")
            hideDialog()
            try {
                val jObj = JSONObject(response.substring(response.indexOf("{"), response.length))
                val error = jObj.getBoolean("error")
                if (!error) {
                    // Usuario registrado en la base de datos
                    Toast.makeText(applicationContext, "Usuario registrado exitosamente. Puede iniciar sesion ahora", Toast.LENGTH_SHORT).show()
                } else {
                    // Error Ocurrido durante el registro, obtener el mensaje de error
                    val errorMsg = jObj.getString("error_msg")
                    Toast.makeText(applicationContext, errorMsg, Toast.LENGTH_SHORT).show()
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }, Response.ErrorListener { error ->
            Log.e(TAG, "Registration Error: ${error.message}")
            Toast.makeText(applicationContext, error.message, Toast.LENGTH_SHORT).show()
            hideDialog()
        }) {
            override fun getParams(): MutableMap<String, String> {
                val params = HashMap<String, String>()
                params["nombres"] = nombre
                params["apellidos"] = apellido
                params["materia"] = materia
                params["telefono"] = celular
                params["email"] = email
                params["contraseña"] = contraseña
                
                return params
            }
        }

        // Añadiendo request al request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req)
    }

    private fun showDialog() {
        if (!pDialog.isShowing)
            pDialog.show()
    }

    private fun hideDialog() {
        if (pDialog.isShowing)
            pDialog.dismiss()
    }
}
