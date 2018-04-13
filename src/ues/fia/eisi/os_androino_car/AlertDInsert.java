package ues.fia.eisi.os_androino_car;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.location.Location;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import ues.fia.eisi.os_androino_car.R;


public class AlertDInsert{

	TextView texto;
	EditText nombre;
	EditText radio;
	Context contexto;
	String lat, lon;
	
	public AlertDInsert(Context context,String latitud, String longitud) {
		lat = latitud;
		lon = longitud;
		contexto = context;
		AlertDialog a;
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		LayoutInflater inf = ((Activity) context).getLayoutInflater();
		View layout = inf.inflate(R.layout.insertar,null);
		texto = (TextView)layout.findViewById(R.id.tv1);
		texto.setText("Latitud: "+lat+"\nLongitud: "+lon);
		nombre = (EditText)layout.findViewById(R.id.edit1);
		radio = (EditText)layout.findViewById(R.id.edit2);
		builder.setView(layout);
		builder.setTitle("Restringir lugar");
		
		
		
		builder.setPositiveButton("Agregar", new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				String[] datos = {nombre.getText().toString(),lat,
						lon,radio.getText().toString()};
				insertar(datos);
				nombre.setText(null);
				radio.setText(null);
			}
		});
		builder.setNegativeButton("Cancelar", new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		builder.setCancelable(false);
		a = builder.create();
		a.show();
	}


	public void insertar(String[] datos){
		ControlDB base = new ControlDB(contexto);
		base.abrir();
		
		if(base.insertar(datos)){
			Toast.makeText((Activity)contexto, "Exito", Toast.LENGTH_SHORT).show();
		} else {
			Toast.makeText((Activity)contexto, "Fallo insertar", Toast.LENGTH_SHORT).show();
		}
		
		base.cerrar();
		
	}
}
