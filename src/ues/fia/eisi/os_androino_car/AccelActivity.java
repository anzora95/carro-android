package ues.fia.eisi.os_androino_car;

import java.io.IOException;
import java.util.List;



import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

public class AccelActivity extends Activity implements SensorEventListener {

	static Button Adelante, Atras, Derecha, Izquierda, Conectar, Desconectar,
			btn;
	private int width, height, orientacion;
	private Sensor mAccelerometer;
	public static TextView EstadoBT;
	ToggleButton freno;
	String ultimoEnviado = "0";
	boolean flag = false;
	ConecxionBluetooth CBT;
	private static final boolean D = true;
	BluetoothAdapter AdaptadorBT;
	Vibrator vibrador;
	public static TextView SenDelantero;
	public static TextView SenTrasero;
	public static String Distancia;
	ControlDB base;
	Location punto;
	Context contexto=this;
	TextView Recivi;
	@SuppressWarnings("deprecation")
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Ocultar la barra de titulo de la aplicacion y hacer full screen
		// requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_main2);
		Recivi = (TextView) findViewById(R.id.VerGPS);
		btn = (Button) findViewById(R.id.btnspeak);
		btn.setEnabled(false);
		btn.setVisibility(View.GONE);

		freno = (ToggleButton) findViewById(R.id.btnfreno);
		freno.setChecked(true);
		freno.setVisibility(View.VISIBLE);
		WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		width = display.getWidth();
		height = display.getHeight();
		orientacion = display.getOrientation();
		// posicion landscape (0 vertical; 1 horizontal)
		// ES TABLET
		if (display.getOrientation() == 0 && (width > height)) {
			flag = true;
		}

		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

		Adelante = (Button) findViewById(R.id.Adelante);
		Atras = (Button) findViewById(R.id.Atras);
		Derecha = (Button) findViewById(R.id.Derecha);
		Izquierda = (Button) findViewById(R.id.Izquierda);

		EstadoBT = (TextView) findViewById(R.id.EstadoBT);
		SenDelantero = (TextView) findViewById(R.id.SenAdelante);
		SenTrasero = (TextView) findViewById(R.id.SenTrasero);
		SenDelantero.setText("");
		SenTrasero.setText("");
		CBT = new ConecxionBluetooth();
		CBT.findBT();
		try {
			CBT.openBT();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Toast.makeText(this, "BUSQUEDA DE DISPOSITIVOS\n EN PROCESO",
				Toast.LENGTH_SHORT).show();
		base = new ControlDB(contexto);
		base.abrir();
		base.cerrar();
		configGPS();

	}
	
	 protected void onDestroy(){
		 	
		 	try {
				CBT.closeBT();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    	finish();
	    	super.onDestroy();
	    }
	 
	protected void onResume() {
		super.onResume();
		SensorManager sm = (SensorManager) getSystemService(SENSOR_SERVICE);
		List<Sensor> sensors = sm.getSensorList(Sensor.TYPE_ACCELEROMETER);
		if (sensors.size() > 0) // dispositivo android tiene acelerometro
		{
			sm.registerListener(this, sensors.get(0),
					SensorManager.SENSOR_DELAY_FASTEST);
		}
	}

	protected void onPause() {
		SensorManager mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		mSensorManager.unregisterListener(this, mAccelerometer);
		super.onPause();
		try {
			CBT.closeBT();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	finish();
    	super.onDestroy();
		
	}

	protected void onStop() {
		SensorManager mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		mSensorManager.unregisterListener(this, mAccelerometer);
		super.onStop();
	}

	/**
	 * Evento del SensorManager (datos actualizados del acelerómetro)
	 */
	public void onSensorChanged(SensorEvent event) {
		if (!freno.isChecked()) { // tiene freno
			// Verificamos tipo de dispositivo
			if (flag) { // es tablet

				/*
				 * A Avanzar R Retroceder D Derecha I Izquierda Y Parar(Freno) X
				 * Centrar(Soltar direccion)
				 */

				// movimiento hacia adelante + derecha
				if (event.values[1] >= 0 && event.values[1] <= 9
						&& event.values[2] >= 1 && event.values[2] <= 10
						&& event.values[0] >= -10 && event.values[0] <= -1) {
					if (ultimoEnviado != "1") {
						clearColor();
						enviar("A");
						enviar("D");
						Atras.setBackgroundResource(R.drawable.boton_abajo);
						Izquierda.setBackgroundResource(R.drawable.boton_izquierda);
						Adelante.setBackgroundResource(R.drawable.boton_arriba_clic);
						Derecha.setBackgroundResource(R.drawable.boton_derecha_clic);
						ultimoEnviado = "1";
					}

					// movimiento hacia adelante + izquierda
				} else if (event.values[1] >= 0 && event.values[1] <= 9
						&& event.values[2] >= 1 && event.values[2] <= 10
						&& event.values[0] >= 1 && event.values[0] <= 10) {
					if (ultimoEnviado != "2") {
						clearColor();
						enviar("A");
						enviar("I");
						Atras.setBackgroundResource(R.drawable.boton_abajo);
						Izquierda.setBackgroundResource(R.drawable.boton_izquierda_clic);
						Adelante.setBackgroundResource(R.drawable.boton_arriba_clic);
						Derecha.setBackgroundResource(R.drawable.boton_derecha);
						ultimoEnviado = "2";
					}

					// movimiento hacia atras + derecha
				} else if (event.values[1] >= 1 && event.values[1] <= 11
						&& event.values[2] >= -10 && event.values[2] <= 4
						&& event.values[0] >= -10 && event.values[0] <= -1) {
					if (ultimoEnviado != "3") {
						clearColor();
						enviar("R");
						enviar("D");
						Atras.setBackgroundResource(R.drawable.boton_abajo_clic);
						Izquierda.setBackgroundResource(R.drawable.boton_izquierda);
						Adelante.setBackgroundResource(R.drawable.boton_arriba);
						Derecha.setBackgroundResource(R.drawable.boton_derecha_clic);
						ultimoEnviado = "3";
					}

					// movimiento hacia atras + izquierda
				} else if (event.values[1] >= 1 && event.values[1] <= 11
						&& event.values[2] >= -10 && event.values[2] <= 4
						&& event.values[0] >= 1 && event.values[0] <= 10) {
					if (ultimoEnviado != "4") {
						clearColor();
						enviar("R");
						enviar("I");
						Atras.setBackgroundResource(R.drawable.boton_abajo_clic);
						Izquierda.setBackgroundResource(R.drawable.boton_izquierda_clic);
						Adelante.setBackgroundResource(R.drawable.boton_arriba);
						Derecha.setBackgroundResource(R.drawable.boton_derecha);
						ultimoEnviado = "4";
					}

				} // movimiento hacia adelante
				else if (event.values[1] >= 0 && event.values[1] <= 9
						&& event.values[2] >= 1 && event.values[2] <= 10) {
					if (ultimoEnviado != "5") {
						clearColor();
						enviar("A");
						enviar("X");
						Atras.setBackgroundResource(R.drawable.boton_abajo);
						Izquierda.setBackgroundResource(R.drawable.boton_izquierda);
						Adelante.setBackgroundResource(R.drawable.boton_arriba_clic);
						Derecha.setBackgroundResource(R.drawable.boton_derecha);
						ultimoEnviado = "5";
					}

					// movimiento hacia atras
				} else if (event.values[1] >= 1 && event.values[1] <= 11
						&& event.values[2] >= -10 && event.values[2] <= 4) {
					if (ultimoEnviado != "6") {
						clearColor();
						enviar("R");
						enviar("X");
						Atras.setBackgroundResource(R.drawable.boton_abajo_clic);
						Izquierda.setBackgroundResource(R.drawable.boton_izquierda);
						Adelante.setBackgroundResource(R.drawable.boton_arriba);
						Derecha.setBackgroundResource(R.drawable.boton_derecha);
						ultimoEnviado = "6";
					}

				}
			} else {// es smartphone

				// movimiento hacia adelante + izquierda
				if ((event.values[0] <= 7 && event.values[0] >= 0)
						&& (event.values[2] <= 10 && event.values[2] >= 5)
						&& (event.values[1] <= -1 && event.values[1] >= -10)) {

					if (ultimoEnviado != "2") {
						clearColor();
						enviar("A");
						enviar("I");
						Atras.setBackgroundResource(R.drawable.boton_abajo);
						Izquierda.setBackgroundResource(R.drawable.boton_izquierda_clic);
						Adelante.setBackgroundResource(R.drawable.boton_arriba_clic);
						Derecha.setBackgroundResource(R.drawable.boton_derecha);
						ultimoEnviado = "2";
					}

					// movimiento hacia adelante + derecha
				} else if ((event.values[0] <= 7 && event.values[0] >= 0)
						&& (event.values[2] <= 10 && event.values[2] >= 5)
						&& (event.values[1] <= 10 && event.values[1] >= 1)) {
					if (ultimoEnviado != "1") {
						clearColor();
						enviar("A");
						enviar("D");
						Atras.setBackgroundResource(R.drawable.boton_abajo);
						Izquierda.setBackgroundResource(R.drawable.boton_izquierda);
						Adelante.setBackgroundResource(R.drawable.boton_arriba_clic);
						Derecha.setBackgroundResource(R.drawable.boton_derecha_clic);
						ultimoEnviado = "1";
					}

					// movimiento hacia atras + izquierda
				} else if ((event.values[0] <= 11 && event.values[0] >= 0)
						&& (event.values[2] <= 5 && event.values[2] >= -10)
						&& ((event.values[1] <= -1 && event.values[1] >= -10))) {
					if (ultimoEnviado != "4") {
						clearColor();
						enviar("R");
						enviar("I");
						Atras.setBackgroundResource(R.drawable.boton_abajo_clic);
						Izquierda.setBackgroundResource(R.drawable.boton_izquierda_clic);
						Adelante.setBackgroundResource(R.drawable.boton_arriba);
						Derecha.setBackgroundResource(R.drawable.boton_derecha);
						ultimoEnviado = "4";
					}

					// movimiento hacia atras + derecha
				} else if ((event.values[0] <= 11 && event.values[0] >= 0)
						&& (event.values[2] <= 5 && event.values[2] >= -10)
						&& (event.values[1] <= 10 && event.values[1] >= 1)) {
					if (ultimoEnviado != "3") {
						clearColor();
						enviar("R");
						enviar("D");
						Atras.setBackgroundResource(R.drawable.boton_abajo_clic);
						Izquierda.setBackgroundResource(R.drawable.boton_izquierda);
						Adelante.setBackgroundResource(R.drawable.boton_arriba);
						Derecha.setBackgroundResource(R.drawable.boton_derecha_clic);
						ultimoEnviado = "3";
					}

					// movimiento hacia adelante
				} else if ((event.values[0] <= 7 && event.values[0] >= 0)) {
					if (ultimoEnviado != "5") {
						clearColor();
						enviar("A");
						enviar("X");
						Atras.setBackgroundResource(R.drawable.boton_abajo);
						Izquierda.setBackgroundResource(R.drawable.boton_izquierda);
						Adelante.setBackgroundResource(R.drawable.boton_arriba_clic);
						Derecha.setBackgroundResource(R.drawable.boton_derecha);
						ultimoEnviado = "5";
					}

					// movimiento hacia atras
				} else if ((event.values[0] <= 11 && event.values[0] >= 0)
						&& (event.values[2] <= 5 && event.values[2] >= -10)) {
					if (ultimoEnviado != "6") {
						clearColor();
						enviar("R");
						enviar("X");
						Atras.setBackgroundResource(R.drawable.boton_abajo_clic);
						Izquierda.setBackgroundResource(R.drawable.boton_izquierda);
						Adelante.setBackgroundResource(R.drawable.boton_arriba);
						Derecha.setBackgroundResource(R.drawable.boton_derecha);
						ultimoEnviado = "6";
					}
				}
			}

		}// Del else

	}

	public void ItStop(View view) {
		if(freno.isChecked()) freno.setBackgroundResource(R.drawable.encendido);
		else if(!freno.isChecked()) freno.setBackgroundResource(R.drawable.apagado);
		clearColor();
		enviar("X");
		enviar("Y");
	}

	public void clearColor() {

		Atras.setBackgroundResource(R.drawable.boton_abajo);
		Izquierda.setBackgroundResource(R.drawable.boton_izquierda);
		Adelante.setBackgroundResource(R.drawable.boton_arriba);
		Derecha.setBackgroundResource(R.drawable.boton_derecha);

	}

	/**
	 * Evento del SensorManager (ha cambiado la precisión)
	 */
	public void onAccuracyChanged(Sensor sensor, int accuracy) {

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
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
						.setText("DISPOSITIVO SIN BLUETOOTH");
			} else if (!AdaptadorBT.isEnabled()) { /*
													 * SI EL BLUETOOTH NO ESTA
													 * ENCENDIDO, REALIZA EL
													 * INTENT PARA ENCENDERLO
													 */
				Toast.makeText(this, "NECESITA ENCENDER EL BLUETOOTH",
						Toast.LENGTH_SHORT).show();
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
				this.startActivity(inte);

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
				this.startActivity(inte);
				Toast.makeText(this, "cambio de activity", Toast.LENGTH_SHORT).show();

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

	void Conectar() {
		try {
			CBT.findBT();
			CBT.openBT();
			Toast.makeText(this, "BUSQUEDA DE DISPOSITIVOS\n EN PROCESO",
					Toast.LENGTH_SHORT).show();
			HabilitarBotones();
		} catch (IOException e) {
			// TODO Bloque catch generado automaticamente
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
				Toast.makeText(this, "!!!ENCIENDA EL BLUETOOTH!!!",
						Toast.LENGTH_SHORT).show();

			}
		}

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
			freno.setEnabled(false);
			} else {HabilitarBotones();freno.setEnabled(true);}
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
