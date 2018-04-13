package ues.fia.eisi.os_androino_car;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

public class VoiceActivity extends Activity implements
		TextToSpeech.OnInitListener {

	static Button Adelante, Atras, Derecha, Izquierda, Conectar, Desconectar,
			btn;
	private static final int VOICE_RECOGNITION_REQUEST_CODE = 1001;
	private Button btSpeak;
	ToggleButton freno;
	public static TextView Recivi;
	private TextToSpeech tts;
	ConecxionBluetooth CBT;
	private static final boolean D = true;
	BluetoothAdapter AdaptadorBT;
	Vibrator vibrador;
	// ImageView //volante;
	public static TextView SenDelantero;
	public static TextView SenTrasero;
	public static String Distancia;
	public static TextView EstadoBT;
	Context contexto = this;
	View vista;
	ControlDB base;
	Location punto;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Ocultar la barra de titulo de la aplicacion y hacer full screen
		// requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		// Definimos posicion horizontal
		// setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		base = new ControlDB(contexto);
		base.abrir();
		base.cerrar();
		configGPS();
		setContentView(R.layout.activity_main2);

		tts = new TextToSpeech(this, this);
		btn = (Button) findViewById(R.id.btnspeak);
		btn.setEnabled(true);
		btn.setVisibility(View.VISIBLE);
		Recivi = (TextView) findViewById(R.id.VerGPS);
		Recivi.setText("");
		Adelante = (Button) findViewById(R.id.Adelante);
		Atras = (Button) findViewById(R.id.Atras);
		Derecha = (Button) findViewById(R.id.Derecha);
		Izquierda = (Button) findViewById(R.id.Izquierda);

		// //volante = (ImageView) findViewById(R.id.img//volante);
		freno = (ToggleButton) findViewById(R.id.btnfreno);
		freno.setEnabled(false);
		freno.setVisibility(View.GONE);
		DeshabilitarBotones();
		CBT = new ConecxionBluetooth();
		EstadoBT = (TextView) findViewById(R.id.EstadoBT);
		SenDelantero = (TextView) findViewById(R.id.SenAdelante);
		SenTrasero = (TextView) findViewById(R.id.SenTrasero);
		SenDelantero.setText("");
		SenTrasero.setText("");

		CBT.findBT();
		try {
			CBT.openBT();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Toast.makeText(this, "BUSQUEDA DE DISPOSITIVOS\n EN PROCESO",
				Toast.LENGTH_SHORT).show();

	}

	public void checkVoiceRecognition() {
		// Check if voice recognition is present
		PackageManager pm = getPackageManager();
		List<ResolveInfo> activities = pm.queryIntentActivities(new Intent(
				RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);
		if (activities.size() == 0) {
			btSpeak.setEnabled(false);
			Toast.makeText(this, "Reconocimiento de voz no disponible",
					Toast.LENGTH_SHORT).show();
		}
	}

	public void speak(View view) {
		Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
		// Specify the calling package to identify your application
		intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getClass()
				.getPackage().getName());

		// Obtiene el lenguaje para la aplicacion
		intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, "en_US");

		intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Diga, NO para cancelar");

		startActivityForResult(intent, VOICE_RECOGNITION_REQUEST_CODE);

	}

	@Override
	public void onDestroy() {
		// Don't forget to shutdown tts!
		if (tts != null) {
			tts.stop();
			tts.shutdown();
		}
		try {
			CBT.closeBT();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finish();

		super.onDestroy();
	}

	@Override
	public void onInit(int status) {

		if (status == TextToSpeech.SUCCESS) {

			int result = tts.setLanguage(Locale.getDefault());
			if (result == TextToSpeech.LANG_MISSING_DATA
					|| result == TextToSpeech.LANG_NOT_SUPPORTED) {
				Log.e("TTS", "This Language is not supported");
				showToastMessage("This Language is not supported");
			} else {

			}

		} else {
			Log.e("TTS", "Initilization Failed!");
		}

	}

	private void speakOut(String msg) {

		tts.speak(msg, TextToSpeech.QUEUE_FLUSH, null);
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		String msg;
		if (requestCode == VOICE_RECOGNITION_REQUEST_CODE) {

			// If Voice recognition is successful then it returns RESULT_OK
			if (resultCode == RESULT_OK) {
				// ejecucion del codigo de la aplicacion
				ArrayList<String> ListadePalabras = data
						.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

				/*
				 * A Avanzar R Retroceder D Derecha I Izquierda Y Parar(Freno) X
				 * Centrar(Soltar direccion)
				 */
				if (!ListadePalabras.get(0).toString().equalsIgnoreCase("NO")) {
					if (ListadePalabras.get(0).toString()
							.equalsIgnoreCase("adelante")
							|| ListadePalabras.get(0).toString()
									.equalsIgnoreCase("go")
							|| ListadePalabras.get(0).toString()
									.equalsIgnoreCase("forward")) {
						// //volante.setImageResource(R.drawable.adelante);
						enviar("X");
						enviar("A");
						try {
							Thread.sleep(1500);
						} catch (Exception e) {
							// TODO: handle exception
						}
						enviar("Y");
						speakOut(getResources().getString(R.string.btnAdelante));
						speakOut(getResources().getString(R.string.otroComando));
						speak(vista);
					} else if (ListadePalabras.get(0).toString()
							.equalsIgnoreCase("reversa")
							|| ListadePalabras.get(0).toString()
									.equalsIgnoreCase("back")
							|| ListadePalabras.get(0).toString()
									.equalsIgnoreCase("reverse")) {
						// //volante.setImageResource(R.drawable.atras);
						enviar("X");
						enviar("R");

						try {
							Thread.sleep(1500);
						} catch (Exception e) {
							// TODO: handle exception
						}
						enviar("Y");
						speakOut(getResources().getString(R.string.btnAtras));
						speakOut(getResources().getString(R.string.otroComando));
						speak(vista);
					} else if (ListadePalabras.get(0).toString()
							.equalsIgnoreCase("derecha")
							|| ListadePalabras.get(0).toString()
									.equalsIgnoreCase("right")) {
						// volante.setImageResource(R.drawable.derecha);
						enviar("D");
						enviar("A");
						try {
							Thread.sleep(1500);
						} catch (Exception e) {
							// TODO: handle exception
						}
						enviar("Y");
						speakOut(getResources().getString(R.string.btnDerecha));
						speakOut(getResources().getString(R.string.otroComando));
						speak(vista);
					} else if (ListadePalabras.get(0).toString()
							.equalsIgnoreCase("izquierda")
							|| ListadePalabras.get(0).toString()
									.equalsIgnoreCase("left")) {
						// volante.setImageResource(R.drawable.izquierda);
						enviar("I");
						enviar("A");
						try {
							Thread.sleep(1500);
						} catch (Exception e) {
							// TODO: handle exception
						}
						enviar("Y");
						speakOut(getResources()
								.getString(R.string.btnIzquierda));
						speakOut(getResources().getString(R.string.otroComando));
						speak(vista);
					} else if (ListadePalabras.get(0).toString()
							.equalsIgnoreCase("detener")
							|| ListadePalabras.get(0).toString()
									.equalsIgnoreCase("stop")) {
						// volante.setImageResource(R.drawable.adelante);
						enviar("Y");
						enviar("X");
						speakOut(getResources().getString(R.string.isStop));
						speakOut(getResources().getString(R.string.otroComando));
						speak(vista);
					} else if (ListadePalabras.get(0).toString()
							.equalsIgnoreCase("atras izquierda")
							|| ListadePalabras.get(0).toString()
									.equalsIgnoreCase("atrás izquierda")
							|| ListadePalabras.get(0).toString()
									.equalsIgnoreCase("back left")) {
						// volante.setImageResource(R.drawable.izquierda);
						enviar("I");
						enviar("R");
						try {
							Thread.sleep(1500);
						} catch (Exception e) {
							// TODO: handle exception
						}
						enviar("Y");
						speakOut(ListadePalabras.get(0).toString());
						speakOut(getResources().getString(R.string.otroComando));
						speak(vista);
					} else if (ListadePalabras.get(0).toString()
							.equalsIgnoreCase("atras derecha")
							|| ListadePalabras.get(0).toString()
									.equalsIgnoreCase("atrás derecha")
							|| ListadePalabras.get(0).toString()
									.equalsIgnoreCase("back right")) {
						// volante.setImageResource(R.drawable.izquierda);
						enviar("D");
						enviar("R");
						try {
							Thread.sleep(1500);
						} catch (Exception e) {
							// TODO: handle exception
						}
						enviar("Y");
						speakOut(ListadePalabras.get(0).toString());
						speakOut(getResources().getString(R.string.otroComando));
						speak(vista);
					} else {
						speakOut(getResources().getString(R.string.noValido));
						speakOut(getResources().getString(R.string.otroComando));
						speak(vista);
					}

					// Result code for various error.
				} else if (resultCode == RecognizerIntent.RESULT_AUDIO_ERROR) {
					showToastMessage("Audio Error");
				} else if (resultCode == RecognizerIntent.RESULT_CLIENT_ERROR) {
					showToastMessage("Client Error");
				} else if (resultCode == RecognizerIntent.RESULT_NETWORK_ERROR) {
					showToastMessage("Network Error");
				} else if (resultCode == RecognizerIntent.RESULT_NO_MATCH) {
					showToastMessage("No Match");
				} else if (resultCode == RecognizerIntent.RESULT_SERVER_ERROR) {
					showToastMessage("Server Error");
				}
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	void showToastMessage(String message) {
		Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
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
			try {
				Class<?> clase;
				clase = Class
						.forName("ues.fia.eisi.os_androino_car.GestionDatos");
				Intent inte = new Intent(this, clase);
				startActivity(inte);
			} catch (ClassNotFoundException e) {
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
	protected void onActivityResult1(int requestCode, int resultCod, Intent data) {
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

	public void configGPS() {

		LocationManager mLocationManager;
		LocationListener mLocationListener;

		mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE); // utiliza
																							// el
																							// servicio
																							// de
																							// localizacion,
																							// NETWORK_SERVICE
																							// menos
																							// preciso
		mLocationListener = new MyLocationListener();
		mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
				0, 0, mLocationListener);
	}

	private class MyLocationListener implements LocationListener {

		@Override
		public void onLocationChanged(Location location) {
			punto = location;
			String texto;

			texto = getResources().getString(R.string.posAct)
					+ getResources().getString(R.string.latitud)
					+ String.valueOf(punto.getLatitude())
					+ getResources().getString(R.string.longitud)
					+ String.valueOf(punto.getLongitude());
			base.abrir();
			String[] res = base.verificarRestriccion(location);
			base.cerrar();

			if (res != null) {
				texto = res[1];
				DeshabilitarBotones();
				btn.setEnabled(false);
			} else {
				HabilitarBotones();
				btn.setEnabled(true);
			}
			Recivi.setBackgroundColor(Color.TRANSPARENT);
			Recivi.setText(texto);
			// textoGPS.setWidth(pixels)
		}

		@Override
		public void onProviderDisabled(String provider) {
			// TODO Auto-generated method stub
			// getResources().getString(R.string.)
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
