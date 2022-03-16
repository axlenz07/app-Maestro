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
import com.uaeh.aame.cobmaestros.MainActivity
import com.uaeh.aame.cobmaestros.R
import helper.SessionManager
import org.json.JSONException
import org.json.JSONObject

class LoginActivity : AppCompatActivity() {

    // Logcat Tag
    private val TAG = LoginActivity::class.simpleName

    // Botones
    private lateinit var btnLogin: Button
    private lateinit var btnToRegister: Button

    // EditText
    private lateinit var inputEmail: EditText
    private lateinit var inputPassword: EditText
    private lateinit var pDialog: ProgressDialog
    private lateinit var session: SessionManager

    // Class Appconfig
    private val appConfig = AppConfig()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Find By View EditText
        inputEmail = findViewById(R.id.email)
        inputPassword = findViewById(R.id.contraseña)

        // Find By View Button
        btnLogin = findViewById(R.id.btnLogin)
        btnToRegister = findViewById(R.id.btnToRegister)

        // Progress Dialog
        pDialog = ProgressDialog(this)
        pDialog.setCancelable(false)

        // Session Manager
        session = SessionManager(applicationContext)

        // Verificar si el usuario ya ha iniciado sesion
        if (session.isLoggedIn()) {
            // El usuario ya inicio sesion, llevarlo al main activity
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        // Evento del Boton de Iniciar Sesion
        btnLogin.setOnClickListener {
            login()
        }

        // Evento del Boton de Llevarlo al Activity de Registro
        btnToRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    /*
    * Funcion para iniciar sesion y entrar en el main activity
    * */
    private fun login() {
        val email = inputEmail.text.toString()
        val password = inputPassword.text.toString()

        // Checar por datos vacios en el formulario
        if (email.isNotEmpty() && password.isNotEmpty()){
            // Iniciar Sesion del Maestro
            checkLogin(email, password)
        } else {
            // Mensaje al maestro para escribir sus datos
            Toast.makeText(applicationContext, "Por favor ingresa tus datos (Correo o Contraseña) para iniciar sesion", Toast.LENGTH_SHORT).show()
        }
    }

    /*
    * Funcion para verificar los datos del maestro en la base de datos
    * */
    private fun checkLogin(email: String, password: String) {
        // Tag usado para cancelar el request
        val tag_string_req = "req_login"

        pDialog.setMessage("Iniciando Sesion...")
        showDialog()

        val strReq: StringRequest = object : StringRequest(Method.POST, appConfig.URL_LOGIN, Response.Listener { response ->
            Log.d(TAG, "Login Response: $response")
            hideDialog()
            try {
                val jObj = JSONObject(response.substring(response.indexOf("{"), response.length))
                val error = jObj.getBoolean("error")
                Log.d(TAG, "Error response: $error")

                // Checar por un error en nodo de json
                if (!error) {
                    // Inicio de sesion exitoso
                    // Guardando datos para mostrar en main activity
                    val id = jObj.getString("id_prof")
                    val user = jObj.getJSONObject("user")
                    val nombre = user.getString("nombres")
                    val apellidos = user.getString("apellidos")
                    val materia = user.getString("materia")
                    val telefono = user.getString("telefono")
                    val mail = user.getString("email")
                    val cont = user.getString("contraseña")

                    // Guardar valor de sesion iniciada y los datos del maestro
                    session.setLogin(true)
                    session.saveDatosUsuario(id, nombre, apellidos, materia, telefono, mail, cont)

                    // Iniciar el main activity
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    // Error al iniciar sesion, obtener el mensaje de error
                    val errorMsg = jObj.getString("error_msg")
                    Toast.makeText(applicationContext, errorMsg, Toast.LENGTH_SHORT).show()
                }
            } catch (e: JSONException) {
                // Json Error
                e.printStackTrace()
                Toast.makeText(applicationContext, "Json error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }, Response.ErrorListener { error ->
            Log.d(TAG, "Login Error: ${error.message}")
            Toast.makeText(applicationContext, error.message, Toast.LENGTH_SHORT).show()
            hideDialog()
        }) {
            override fun getParams(): MutableMap<String, String> {
                // Agregando los parametros hacia el url login
                val params = HashMap<String, String>()
                params["email"] = email
                params["contraseña"] = password

                return params
            }
        }

        // Agregando request al request queue
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