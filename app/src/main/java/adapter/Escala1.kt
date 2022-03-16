package adapter

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.uaeh.aame.cobmaestros.R

class Escala1(private val _context: Activity, private val id: ArrayList<String>, private val aspecto: ArrayList<String>, private val porcentaje: ArrayList<String>):
    ArrayAdapter<String>(_context, R.layout.list_escala, id) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val inflater = _context.layoutInflater
        val rowView = inflater.inflate(R.layout.list_escala, null, true)

        val tvAspecto = rowView.findViewById<TextView>(R.id.aspecto)
        val tvPorcentaje = rowView.findViewById<TextView>(R.id.porcentaje)

        tvAspecto.text = aspecto[position]
        tvPorcentaje.text = "${porcentaje[position]}%"

        return rowView
    }
}