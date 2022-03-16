package com.uaeh.aame.cobmaestros

import activity.LoginActivity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.se.omapi.Session
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView
import helper.SessionManager

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration

    // Declaracion de botones y textView
    private lateinit var txtNombre: TextView
    private lateinit var txtEmail: TextView

    // Declaracion de Session
    private lateinit var session: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Encontrar por id Drawer Layout
        val drawerLayout = findViewById<DrawerLayout>(R.id.drawer_layout)
        val navView = findViewById<NavigationView>(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment)

        // Obtener nav header view
        val headerView: View = navView.getHeaderView(0)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        // Encotrar por id textView
        txtNombre = headerView.findViewById(R.id.nv_Name)
        txtEmail = headerView.findViewById(R.id.nv_Email)

        // Session Manager
        session = SessionManager(applicationContext)

        if (!session.isLoggedIn())
            logoutUser()

        /**
         * Passing each menu ID as a set of Ids because each
         * menu should be considered as top level destination
         */
        appBarConfiguration = AppBarConfiguration(setOf(R.id.nav_home, R.id.nav_list_alum, R.id.nav_activities, R.id.nav_asistencia,
            R.id.nav_scale, R.id.nav_avisos, R.id.nav_data_user, R.id.nav_about), drawerLayout)
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        userDetails()
    }

    /**
     * Cerrar sesion del usuario. Colocara bandera isLoggedIn a falso en preferencias
     * compartidas
     */
    private fun logoutUser(){
        session.setLogin(false)

        // Lanzando activity login
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        // Inflate the menu: this adds items to the action bar if it is present
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            R.id.action_logout -> {
                logoutUser()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    /**
     * Funcion para obtener los datos de inicio de sesion
     * Nombre, Email
     */
    private fun userDetails() {
        val nombre = session.getNombre()
        val email = session.getMail()

        // Agregando los datos obtenidos en el header view
        txtNombre.text = nombre
        txtEmail.text = email
    }
}