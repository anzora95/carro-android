package ues.fia.eisi.os_androino_car;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.graphics.Color;
import android.os.Handler;
import android.util.Log;

public class ConecxionBluetooth {

	BluetoothAdapter mBluetoothAdapter;
	BluetoothSocket mmSocket;
	BluetoothDevice mmDevice;
	OutputStream mmOutputStream;
	InputStream mmInputStream;
	Thread workerThread;
	byte[] readBuffer;
	int readBufferPosition;
	int counter;
	volatile boolean stopWorker;
	private static final boolean D = true;
	StringTokenizer st;

	public ConecxionBluetooth() {
		// TODO Ap�ndice de constructor generado autom�ticamente
	}

	/* ENCUENTRA EL DISPOSITIVO CON EL NOMBRE INDICADO */
	public void findBT() {

		if (D)
			Log.e("ConecxionBluetooth", "findBT");
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		/*
		 * if(mBluetoothAdapter == null) {
		 * Os_Andrino_Car_Activity.EstadoBT.setText
		 * ("No bluetooth adapter available"); }
		 * 
		 * /*SE HACE EN LA CLASE PRINCIPAL
		 */

		/*
		 * if(!mBluetoothAdapter.isEnabled()) { if(D)
		 * Log.e("ConecxionBluetooth","ANTES DEL INTENT"); Intent
		 * enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
		 * startActivityForResult(enableBluetooth, 0); }
		 */

		Set<BluetoothDevice> pairedDevices = mBluetoothAdapter
				.getBondedDevices();
		if (pairedDevices.size() > 0) {
			for (BluetoothDevice device : pairedDevices) {
				if (device.getName().equals("RN42-8D70")) /*
														 * NOMBRE DEL MODULO BT
														 * DESEADO
														 */
				{
					mmDevice = device;
					Os_Andrino_Car_Activity.EstadoBT
							.setText("Dispositivo BT Encontrado!!");
					break;
				}
			}
		}
	}

	/* METODO QUE ABRE LA COMUNICACION CON ARDUINO */
	public void openBT() throws IOException {
		if (D)
			Log.e("ConecxionBluetooth", "openBT");
		UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb"); // Standard
																				// SerialPortService
																				// ID
		mmSocket = mmDevice.createRfcommSocketToServiceRecord(uuid);
		mmSocket.connect();
		mmOutputStream = mmSocket.getOutputStream();
		mmInputStream = mmSocket.getInputStream();
		beginListenForData();
		Os_Andrino_Car_Activity.EstadoBT.setText("Bluetooth Conectado!!");
	}

	/* METODO QUE ESCUCHA CONSTANTEMENTE LOS DATOS ENVIADOS POR ARDUINO */
	void beginListenForData() {
		if (D)
			Log.e("ConecxionBluetooth", "beginListenForData");
		final Handler handler = new Handler();
		final byte delimiter = 10; // This is the ASCII code for a newline
									// character

		stopWorker = false;
		readBufferPosition = 0;
		readBuffer = new byte[1024];
		workerThread = new Thread(new Runnable() {
			public void run() {
				while (!Thread.currentThread().isInterrupted() && !stopWorker) {
					try {
						int bytesAvailable = mmInputStream.available();
						if (bytesAvailable > 0) {
							byte[] packetBytes = new byte[bytesAvailable];
							mmInputStream.read(packetBytes);
							for (int i = 0; i < bytesAvailable; i++) {
								byte b = packetBytes[i];
								if (b == delimiter) {
									final byte[] encodedBytes = new byte[readBufferPosition];
									System.arraycopy(readBuffer, 0,
											encodedBytes, 0,
											encodedBytes.length);
									final String data = new String(
											encodedBytes, "US-ASCII");
									readBufferPosition = 0;

									handler.post(new Runnable() {
										public void run() {

											String delantero = "", trasero = "";
											st = new StringTokenizer(data, "+");

											try {
												delantero = st.nextToken();
												trasero = st.nextToken();
											} catch (Exception e) {
												// TODO: handle exception
											}

											Os_Andrino_Car_Activity.SenDelantero
													.setText("S. Delan "
															+ delantero + " cm");
											Os_Andrino_Car_Activity.SenTrasero
													.setText("S. Tras "
															+ trasero + " cm");

											if (Float.parseFloat(delantero) > 6
													&& Float.parseFloat(delantero) <= 20) {
												Os_Andrino_Car_Activity.alerta
														.setBackgroundColor(Color.RED);
												Os_Andrino_Car_Activity.alerta
														.setText("Obstaculo adelante!!!");
											} else {
												Os_Andrino_Car_Activity.alerta
														.setBackgroundColor(Color.GREEN);
												Os_Andrino_Car_Activity.alerta
														.setText("Free Pass!!!");

												if (Float.parseFloat(trasero) > 6
														&& Float.parseFloat(trasero) <= 20) {
													Os_Andrino_Car_Activity.alerta
															.setBackgroundColor(Color.RED);
													Os_Andrino_Car_Activity.alerta
															.setText("Obstaculo trasero!!!");
												} else {
													Os_Andrino_Car_Activity.alerta
															.setBackgroundColor(Color.GREEN);
													Os_Andrino_Car_Activity.alerta
															.setText("Free Pass!!!");
												}
											}
										}
									});

								} else {
									readBuffer[readBufferPosition++] = b;
								}
							}

						}
					} catch (IOException ex) {
						stopWorker = true;
					}
				}
			}
		});

		workerThread.start();
	}

	/* METODO QUE ENVIA LOS DATOS AL ARDUINO */
	public void sendData(String enviar) throws IOException {
		if (D)
			Log.e("ConecxionBluetooth", "sendData");
		String msg = enviar;
		msg += "\n";
		mmOutputStream.write(msg.getBytes());
		// Os_Andrino_Car_Activity.Envie.setText("Se Envio: "+msg);
		if (D)
			Log.e("Conecxion", "seEnvioData");
	}

	/* METODO QUE TERMINA LA CONECXION BLUETOOTH */
	public void closeBT() throws IOException {
		if (D)
			Log.e("ConecxionBluetooth", "closeBT");
		stopWorker = true;
		mmOutputStream.close();
		mmInputStream.close();
		mmSocket.close();
		Os_Andrino_Car_Activity.EstadoBT.setText("Bluetooth Closed");
	}
}