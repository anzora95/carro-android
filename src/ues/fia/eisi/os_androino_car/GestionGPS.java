package ues.fia.eisi.os_androino_car;



import java.sql.Date;
import java.text.DateFormat;
import java.util.Locale;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import ues.fia.eisi.os_androino_car.R;

public class GestionGPS extends Activity {
	ControlDB base;
	Location loc1;
	ProgressDialog pd;
	Location punto=null;
	TextView textoGPS;
	Dialog listDialog;
	Context contexto=this;
	String seleccion,latitud,longitud;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		((Activity) contexto).getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.gestion_gps);
		// Definimos posicion horizontal
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

		base = new ControlDB(contexto);
		base.abrir();
		base.cerrar();
		textoGPS = (TextView)findViewById(R.id.textoGPS);
		textoGPS.setText("Obteniendo Posición GPS...\n\nPOR FAVOR ESPERE MIENTRAS SE MUESTRA SU POSICIÓN ACTUAL");
		textoGPS.setBackgroundColor(Color.RED);
		//setPd(ProgressDialog.show((Activity)contexto, "Por Favor espere", "Localizando posiciÃ³n GPS..."));


		configGPS();
		Button restringir = (Button)findViewById(R.id.btnGuaGPS);
		restringir.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if(punto!=null){
					AlertDInsert nuevo = new AlertDInsert(contexto,latitud,longitud);}
				else Toast.makeText(contexto, "No se ha leido un punto GPS", Toast.LENGTH_SHORT).show();
			}
		});
		Button modificar = (Button)findViewById(R.id.btnModGPS);
		modificar.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				base.abrir();
				String[] listaLugares=base.consultarTodasRestricciones();
				base.cerrar();
				if(listaLugares!=null){
					listDialog = new Dialog(contexto);
					LayoutInflater li;
					View view;

					listDialog.setTitle("Seleccionar Lugar a Modificar");
					li = (LayoutInflater)contexto.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
					view = li.inflate(R.layout.lista_lugares,null,false);
					listDialog.setContentView(view);
					listDialog.setCancelable(true);

					final ListView list1 = (ListView)listDialog.findViewById(R.id.listaL);
					list1.setAdapter(new ArrayAdapter<String>(contexto,android.R.layout.simple_list_item_1,listaLugares));
					list1.setOnItemClickListener(new OnItemClickListener() {

						@Override
						public void onItemClick(AdapterView<?> arg0, View arg1,
								int arg2, long arg3) {

							seleccion = list1.getItemAtPosition(arg2).toString();
							AlertDModificar mod = new AlertDModificar(contexto, seleccion,base);
							listDialog.cancel();
						}
					});
					listDialog.show();}

				else Toast.makeText(GestionGPS.this, "No se encontraron datos", Toast.LENGTH_SHORT).show();

			}


		});
		Button eliminar = (Button)findViewById(R.id.btnDelGPS);
		eliminar.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				base.abrir();
				String[] listaLugares=base.consultarTodasRestricciones();
				base.cerrar();
				if(listaLugares!=null){
					listDialog = new Dialog(contexto);
					LayoutInflater li;
					View view;

					listDialog.setTitle("Seleccionar Lugar a Eliminar");
					li = (LayoutInflater)contexto.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
					view = li.inflate(R.layout.lista_lugares,null,false);
					listDialog.setContentView(view);
					listDialog.setCancelable(true);

					final ListView list1 = (ListView)listDialog.findViewById(R.id.listaL);
					list1.setAdapter(new ArrayAdapter<String>(contexto,android.R.layout.simple_list_item_1,listaLugares));
					list1.setOnItemClickListener(new OnItemClickListener() {

						@Override
						public void onItemClick(AdapterView<?> arg0, View arg1,
								int arg2, long arg3) {

							seleccion = list1.getItemAtPosition(arg2).toString();
							base.abrir();
							if(base.eliminaRestriccion(seleccion)){
								Toast.makeText(contexto, seleccion +" ha sido eliminado", Toast.LENGTH_SHORT).show();
								listDialog.cancel();
							} else Toast.makeText(contexto, seleccion +" NO ha sido eliminado", Toast.LENGTH_SHORT).show();
							base.cerrar();

						}
					});
					listDialog.show();
				}
				else Toast.makeText(GestionGPS.this, "No se encontraron datos", Toast.LENGTH_SHORT).show();
			}


		});


	}

	public void configGPS(){

		LocationManager mLocationManager;
		LocationListener mLocationListener;

		mLocationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE); //utiliza el servicio de localizacion, NETWORK_SERVICE menos preciso
		mLocationListener = new MyLocationListener();
		mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0	,0,mLocationListener);
	}


	private class MyLocationListener implements LocationListener{

		@Override
		public void onLocationChanged(Location location) {
			punto = location;
			latitud = String.valueOf(punto.getLatitude());
			longitud = String.valueOf(punto.getLongitude());
			String tiempo = DateFormat.getTimeInstance(DateFormat.LONG).format(punto.getTime());
			String texto;
			//pd.dismiss();
			texto = "Punto Actual:\n" +
					"Latitud: "+latitud+
					"\nLongitud: "+longitud+
					"\ntiempo:"+tiempo;
			base.abrir();
			String[] res = base.verificarRestriccion(location);
			base.cerrar();


			if(res!=null){
				texto+="\n"+res[0]+"\n"+res[1]+"\n"+res[2]+"\n"+res[3];
			}
			textoGPS.setBackgroundColor(Color.WHITE);
			textoGPS.setText(texto);
			//textoGPS.setWidth(pixels)
		}


		@Override
		public void onProviderDisabled(String provider) {
			// TODO Auto-generated method stub
			textoGPS.setText("Favor Habiliar el GPS de forma manual");
			textoGPS.setBackgroundColor(Color.YELLOW);
		}

		@Override
		public void onProviderEnabled(String provider) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			// TODO Auto-generated method stub

		}


	}
	public ProgressDialog getPd() {
		return pd;
	}

	public void setPd(ProgressDialog pd) {
		this.pd = pd;
	}

}
