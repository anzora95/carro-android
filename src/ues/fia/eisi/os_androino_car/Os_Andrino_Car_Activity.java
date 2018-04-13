package ues.fia.eisi.os_androino_car;

import java.io.IOException;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

public class Os_Andrino_Car_Activity extends Activity implements
		OnTouchListener {
	ControlDB base;
	Location punto;
	Context contexto=this;
	public static TextView Recivi;
	public static TextView Envie;
	public static TextView EstadoBT;
	public static TextView SenDelantero;
	public static TextView SenTrasero;
	public static TextView alerta;
	public static String Distancia;

	static Button Adelante, Atras, Derecha, Izquierda, Conectar, Desconectar,
			speak;
	ToggleButton freno;
	//ImageView volante;
	ConecxionBluetooth CBT;
	private static final boolean D = true;
	BluetoothAdapter AdaptadorBT;
	Vibrator vibrador;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (D)
			Log.e("Os_ArdroIno_Car", "Oncreate");
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.activity_main2);
		
		//se crea la base
		base = new ControlDB(contexto);
		base.abrir();
		base.cerrar();
		configGPS();
		// Definimos posicion horizontal
		//setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		Adelante = (Button) findViewById(R.id.Adelante);
		Adelante.setOnTouchListener(this);
		Atras = (Button) findViewById(R.id.Atras);
		Atras.setOnTouchListener(this);
		Derecha = (Button) findViewById(R.id.Derecha);
		Derecha.setOnTouchListener(this);
		Izquierda = (Button) findViewById(R.id.Izquierda);
		Izquierda.setOnTouchListener(this);
		speak = (Button) findViewById(R.id.btnspeak);
		speak.setVisibility(View.GONE);
		freno = (ToggleButton) findViewById(R.id.btnfreno);
		freno.setVisibility(View.GONE);
		Recivi = (TextView) findViewById(R.id.VerGPS);
		EstadoBT = (TextView) findViewById(R.id.EstadoBT);
		SenDelantero = (TextView) findViewById(R.id.SenAdelante);
		SenTrasero = (TextView) findViewById(R.id.SenTrasero);
		SenDelantero.setText("");
		SenTrasero.setText("");
		alerta = (TextView) findViewById(R.id.alerta);
		alerta.setBackgroundColor(Color.GREEN);
		alerta.setText(getResources().getString(R.string.alerta));
		CBT = new ConecxionBluetooth();
		DeshabilitarBotones();
		if (D)
			Log.e("Os_ArdroIno_Car", "Conectar");
		AdaptadorBT = BluetoothAdapter.getDefaultAdapter();
		if (AdaptadorBT == null) /* VERIFICA SI EL DISPOSITIVO TIENE BT */
		{
			Os_Andrino_Car_Activity.EstadoBT
					.setText(getResources().getString(R.string.dispSinBT));
		} else if (!AdaptadorBT.isEnabled()) { /*
												 * SI EL BLUETOOTH NO ESTA
												 * ENCENDIDO, REALIZA EL INTENT
												 * PARA ENCENDERLO
												 */
			Toast.makeText(Os_Andrino_Car_Activity.this,
					getResources().getString(R.string.encBT), Toast.LENGTH_SHORT)
					.show();
			Intent enableBluetooth = new Intent(
					BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enableBluetooth, 0);
		} else {
			Conectar();
		}
		/*
		 * CBT.findBT(); try { CBT.openBT(); } catch (IOException e) { // TODO
		 * Auto-generated catch block e.printStackTrace(); }
		 * Toast.makeText(this, "BUSQUEDA DE DISPOSITIVOS\n EN PROCESO",
		 * Toast.LENGTH_SHORT) .show();
		 */
		//volante = (ImageView) findViewById(R.id.imgVolante);
		speak.setEnabled(false);
		freno.setEnabled(false);
	}

	protected void onDestroy() {

		try {
			CBT.closeBT();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finish();
		super.onDestroy();
	}

	public boolean onOptionsItemSelected(MenuItem elemento) {
		switch (elemento.getItemId()) {

		case R.id.ConectarBT:
			if (D)
				Log.e("Os_ArdroIno_Car", "Conectar");
			// openButton.setEnabled(false);
			AdaptadorBT = BluetoothAdapter.getDefaultAdapter();
			if (AdaptadorBT == null) /* VERIFICA SI EL DISPOSITIVO TIENE BT */
			{
				Os_Andrino_Car_Activity.EstadoBT
						.setText(getResources().getString(R.string.dispSinBT));
			} else if (!AdaptadorBT.isEnabled()) { /*
													 * SI EL BLUETOOTH NO ESTA
													 * ENCENDIDO, REALIZA EL
													 * INTENT PARA ENCENDERLO
													 */
				Toast.makeText(Os_Andrino_Car_Activity.this,
						getResources().getString(R.string.encBT), Toast.LENGTH_SHORT)
						.show();
				Intent enableBluetooth = new Intent(
						BluetoothAdapter.ACTION_REQUEST_ENABLE);
				startActivityForResult(enableBluetooth, 0);
			} else {
				Conectar();
			}
			break;

		case R.id.Desconectar:
			try {
				if (D)
					Log.e("Os_ArdroIno_Car", "Desconectar");
				DeshabilitarBotones();
				CBT.closeBT();
			} catch (IOException ex) {
			}
			break;

		case R.id.ctrlVoz:
			try {
				DeshabilitarBotones();
				CBT.closeBT();
				Class<?> clase;
				clase = Class
						.forName("ues.fia.eisi.os_androino_car.VoiceActivity");
				Intent inte = new Intent(this, clase);
				startActivity(inte);

			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return true;

		case R.id.ctrlAcel:
			try {
				DeshabilitarBotones();
				CBT.closeBT();
				Class<?> clase;
				clase = Class
						.forName("ues.fia.eisi.os_androino_car.AccelActivity");
				Intent inte = new Intent(this, clase);
				startActivity(inte);

			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return true;
		case R.id.ctrlBtn:
			try {
				DeshabilitarBotones();
				CBT.closeBT();
				Class<?> clase;
				clase = Class
						.forName("ues.fia.eisi.os_androino_car.Os_Andrino_Car_Activity");
				Intent inte = new Intent(this, clase);
				startActivity(inte);

			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return true;
		case R.id.ctrlUsu:
			try{
				Class<?> clase;
				clase = Class
						.forName("ues.fia.eisi.os_androino_car.GestionDatos");
				Intent inte = new Intent(this, clase);
				startActivity(inte);
			}catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return true;
		case R.id.exit:
			try {
				// DeshabilitarBotones();
				CBT.closeBT();
				finish();
				super.onDestroy();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return true;

		}
		return false;

	}

	// };

	void Conectar() {
		try {
			CBT.findBT();
			CBT.openBT();
			Toast.makeText(Os_Andrino_Car_Activity.this,
					getResources().getString(R.string.busquedaDisposi), Toast.LENGTH_SHORT)
					.show();
			HabilitarBotones();
		} catch (IOException e) {
			// TODO Bloque catch generado automaticamente
			Log.d("Conectar", "vieja");
			e.printStackTrace();
		}
	}

	public static void DeshabilitarBotones() {
		Adelante.setEnabled(false);
		Atras.setEnabled(false);
		Derecha.setEnabled(false);
		Izquierda.setEnabled(false);

	}

	public static void HabilitarBotones() {
		Adelante.setEnabled(true);
		Atras.setEnabled(true);
		Derecha.setEnabled(true);
		Izquierda.setEnabled(true);

	}

	/* ENCENDER EL BLUETOOTH Y COMENZAR A CONECTAR */
	protected void onActivityResult(int requestCode, int resultCod, Intent data) {
		if (requestCode == 0) {
			if (resultCod == RESULT_OK) {
				Conectar();
			} else {
				vibrador = (Vibrator) getSystemService(VIBRATOR_SERVICE);
				vibrador.vibrate(300);
				Toast.makeText(this, getResources().getString(R.string.encBT),
						Toast.LENGTH_SHORT).show();
				DeshabilitarBotones();

			}
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		/*
		 * A Avanzar R Retroceder D Derecha I Izquierda Y Parar(Freno) X
		 * Centrar(Soltar direccion)
		 */
		switch (v.getId()) {
		case R.id.Adelante:
			if (D)
				Log.e("DatosEnviados", "Adelante");
			//volante.setImageResource(R.drawable.adelante);
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				// v.setBackgroundColor(Color.GREEN);
				enviar("A");
				Adelante.setBackgroundResource(R.drawable.boton_arriba_clic);
				return true;
			} else if (event.getAction() == MotionEvent.ACTION_UP) {
				// v.setBackgroundColor(Color.GRAY);
				enviar("Y");
				Adelante.setBackgroundResource(R.drawable.boton_arriba);
				return true;
			}
			break;

		case R.id.Atras:
			if (D)
				Log.e("DatosEnviados", "Atras");
			//volante.setImageResource(R.drawable.atras);
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				Atras.setBackgroundResource(R.drawable.boton_abajo_clic);
				// v.setBackgroundColor(Color.GREEN);
				enviar("R");
				return true;
			} else if (event.getAction() == MotionEvent.ACTION_UP) {
				// v.setBackgroundColor(Color.GRAY);
				enviar("Y");
				Atras.setBackgroundResource(R.drawable.boton_abajo);
				return true;
			}
			break;

		case R.id.Derecha:
			if (D)
				Log.e("DatosEnviados", "Derecha");
			//volante.setImageResource(R.drawable.derecha);
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				// v.setBackgroundColor(Color.GREEN);
				enviar("D");
				Derecha.setBackgroundResource(R.drawable.boton_derecha_clic);
				return true;
			} else if (event.getAction() == MotionEvent.ACTION_UP) {
				// v.setBackgroundColor(Color.GRAY);
				enviar("X");
				Derecha.setBackgroundResource(R.drawable.boton_derecha);
			}
			break;

		case R.id.Izquierda:
			if (D)
				Log.e("DatosEnviados", "Izquierda");
			//volante.setImageResource(R.drawable.izquierda);
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				// v.setBackgroundColor(Color.GREEN);
				enviar("I");
				Izquierda.setBackgroundResource(R.drawable.boton_izquierda_clic);
				return true;
			} else if (event.getAction() == MotionEvent.ACTION_UP) {
				// v.setBackgroundColor(Color.GRAY);
				enviar("X");
				Izquierda.setBackgroundResource(R.drawable.boton_izquierda);
			}
			break;
		}
		return false;
	}

	public static void estaCerca(String mensaje, Context ctx) {
		Toast.makeText(ctx, mensaje, Toast.LENGTH_SHORT).show();
	}

	public void enviar(String Enviar) {
		try {
			if (D)
				Log.e("enviar", "Enviado: " + "\"" + Enviar + "\"");
			CBT.sendData(Enviar);
		} catch (IOException ex) {
		}
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
			String texto;
			
			texto = getResources().getString(R.string.posAct) +
					getResources().getString(R.string.latitud)+String.valueOf(punto.getLatitude())+
					getResources().getString(R.string.longitud)+String.valueOf(punto.getLongitude());
			base.abrir();
			String[] res = base.verificarRestriccion(location);
			base.cerrar();

			if(res!=null){
			texto=res[1];
			DeshabilitarBotones();
			} else HabilitarBotones();
			Recivi.setBackgroundColor(Color.TRANSPARENT);
			Recivi.setText(texto);
		//textoGPS.setWidth(pixels)
		}
		

		@Override
		public void onProviderDisabled(String provider) {
			// TODO Auto-generated method stub getResources().getString(R.string.)
			Recivi.setText(getResources().getString(R.string.solicitarGPS));
			Recivi.setBackgroundColor(Color.YELLOW);
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

}
