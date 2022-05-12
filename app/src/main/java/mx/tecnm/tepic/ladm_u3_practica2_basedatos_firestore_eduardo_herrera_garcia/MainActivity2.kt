package mx.tecnm.tepic.ladm_u3_practica2_basedatos_firestore_eduardo_herrera_garcia

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.firebase.firestore.FirebaseFirestore
import mx.tecnm.tepic.ladm_u3_practica2_basedatos_firestore_eduardo_herrera_garcia.databinding.ActivityMain2Binding

class MainActivity2 : AppCompatActivity() {
    var idSeleccionado = ""
    lateinit var binding: ActivityMain2Binding
    val baseRemota = FirebaseFirestore.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMain2Binding.inflate(layoutInflater)
        setContentView(binding.root)

        idSeleccionado = intent.extras!!.getString("idSeleccionado")!!

        baseRemota.collection("auto")
            .document(idSeleccionado)
            .get()
            .addOnSuccessListener {
                binding.modelo.setText(it.getString("modelo"))
                binding.marca.setText(it.getString("marca"))
                binding.kilometrage.setText(it.getLong("kilometrage").toString())
            }
            .addOnFailureListener{
                AlertDialog.Builder(this)
                    .setMessage(it.message)
                    .show()
            }
        binding.regresar.setOnClickListener {
            finish()
        }
        binding.actualizar.setOnClickListener {
            baseRemota.collection("auto")
                .document(idSeleccionado)
                .update("marca" , binding.marca.text.toString(),
                    "modelo" , binding.modelo.text.toString(),
                    "kilometrage" , binding.kilometrage.text.toString().toInt())
                .addOnSuccessListener {
                    Toast.makeText(this, "EXITO SE ACTUALIZÓ", Toast.LENGTH_LONG)
                        .show()
                }
                .addOnFailureListener{
                    AlertDialog.Builder(this)
                        .setMessage(it.message)
                        .show()
                }
            binding.modelo.setText("")
            binding.marca.setText("")
            binding.kilometrage.setText("")
        }
    }
}