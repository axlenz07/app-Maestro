package adapter

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.uaeh.aame.cobmaestros.R


class AvisosAdapter(private val _context: Activity, private val id: ArrayList<String>, private val title: ArrayList<String>, private val descripcion: ArrayList<String>, private val fecha: ArrayList<String>):
    ArrayAdapter<String>(_context, R.layout.list_avisos, id){

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val inflater = _context.layoutInflater
        val rowView = inflater.inflate(R.layout.list_avisos, null, true)

        val num = rowView.findViewById<TextView>(R.id.num)
        val titleText = rowView.findViewById<TextView>(R.id.title)
        val subtitleText = rowView.findViewById<TextView>(R.id.description)
        val fechas = rowView.findViewById<TextView>(R.id.fecha)
        val nume = "${position + 1}.-"

        titleText.text = title[position]
        subtitleText.text = descripcion[position]
        fechas.text = fecha[position]
        num.text = nume

        return rowView
    }
}