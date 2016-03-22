package com.dwin.activity;

import java.util.Date;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.ToggleButton;

import com.dwin.dwinapi.R;
import com.dwin.dwinapi.SerialPort;

/**
 * 主界面UI
 *
 * @author F
 *
 */
public class MainActivity extends Activity implements OnCheckedChangeListener,
		OnClickListener {

	private Spinner spiChooseSerialPort, spiChooseBaudRate, spiChooseDataBits,
			spiChooseStopBits, spiChooseParity;;

	private ArrayAdapter<String> adapSerialPort, adapBaudRate, adapDataBits,
			adapStopBits, adapParity;

	private EditText eTextShowMsg;
	private EditText eTextSendMsg;

	private ToggleButton togBtnSerial;
	private ToggleButton togBtnReciveData;
	private ToggleButton togBtnShowDataType;
	private ToggleButton togBtnSendDataType;
	private Button btnClear;
	private Button btnSend;

	private ToggleButton togBtnSendPer100ms;
	private ToggleButton togBtnSendPer500ms;
	private ToggleButton togBtnSendPer1000ms;

	private static final String[] Sserialport = { "COM0", "COM1", "COM2",
			"COM3", "COM4", "USB0", "USB1" };
	private static final String[] m_iSerialPort = { "S0", "S1", "S2", "S3",
			"S4", "USB0", "USB1" };

	private static final String[] Sbaudrate = { "115200", "57600", "38400",
			"19200", "9600", "4800", "2400", "1200", "300", };
	private static final int[] baudrate = { 115200, 57600, 38400, 19200, 9600,
			4800, 2400, 1200, 300, };

	private static final String[] Sdatabits = { "5", "6", "7", "8" };
	private static final int[] databits = { 5, 6, 7, 8 };

	private static final String[] Sstopbits = { "1", "2", };
	private static final int[] stopbits = { 1, 2, };

	private static final String[] Sparity = { "None", "Odd", "Even", "Mark",
			"Space" };
	private static final int[] paritys = { 'n', 'o', 'e', 'm', 's' };

	private String port;
	private int speed;
	private int dataBit;
	private int stopBit;
	private int parity;

	private TimerSendThread mTimerSendThread;

	/**
	 * 是否接收数据
	 */
	private boolean isReceive = false;

	/**
	 * 自定义串口工具
	 */
	SerialPort serialPort;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initView();

	}

	/**
	 * 初始化组件
	 */
	private void initView() {
		eTextShowMsg = (EditText) findViewById(R.id.showmsg);
		eTextSendMsg = (EditText) findViewById(R.id.sendmsg);
		// eTextSendMsg.setText("01 EF");
		togBtnReciveData = (ToggleButton) findViewById(R.id.togBtnReciveDate);

		togBtnShowDataType = (ToggleButton) findViewById(R.id.togBtnShowDateType);
		togBtnSendDataType = (ToggleButton) findViewById(R.id.togBtnSendDateType);
		togBtnSerial = (ToggleButton) findViewById(R.id.togBtnSerial);
		btnClear = (Button) findViewById(R.id.clearButton);
		btnSend = (Button) findViewById(R.id.sendButton);

		btnClear.setOnClickListener(this);
		btnSend.setOnClickListener(this);
		togBtnSendPer100ms = (ToggleButton) findViewById(R.id.togBtnSendPer100ms);
		togBtnSendPer500ms = (ToggleButton) findViewById(R.id.togBtnSendPer500ms);
		togBtnSendPer1000ms = (ToggleButton) findViewById(R.id.togBtnSendPer1000ms);

		// *****************************************
		// set SerialPort spinner
		// *****************************************
		spiChooseSerialPort = (Spinner) findViewById(R.id.choose_seriaPort_spinner);
		adapSerialPort = new ArrayAdapter<String>(this,
				android.R.layout.simple_dropdown_item_1line, Sserialport);
		adapSerialPort
				.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
		spiChooseSerialPort.setAdapter(adapSerialPort);

		// *****************************************
		// set BaudRate spinner
		// *****************************************
		spiChooseBaudRate = (Spinner) findViewById(R.id.choose_baudRate_spinner);
		adapBaudRate = new ArrayAdapter<String>(this,
				android.R.layout.simple_dropdown_item_1line, Sbaudrate);
		adapBaudRate
				.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
		spiChooseBaudRate.setAdapter(adapBaudRate);

		// *****************************************
		// set DataBit spinner
		// *****************************************
		spiChooseDataBits = (Spinner) findViewById(R.id.choose_databits_spinner);
		adapDataBits = new ArrayAdapter<String>(this,
				android.R.layout.simple_dropdown_item_1line, Sdatabits);
		adapDataBits
				.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
		spiChooseDataBits.setAdapter(adapDataBits);

		// *****************************************
		// set StopBit spinner
		// *****************************************
		spiChooseStopBits = (Spinner) findViewById(R.id.choose_stopbits_spinner);
		adapStopBits = new ArrayAdapter<String>(this,
				android.R.layout.simple_dropdown_item_1line, Sstopbits);
		adapStopBits
				.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
		spiChooseStopBits.setAdapter(adapStopBits);

		// *****************************************
		// set Parity spinner
		// *****************************************
		spiChooseParity = (Spinner) findViewById(R.id.choose_parity_spinner);
		adapParity = new ArrayAdapter<String>(this,
				android.R.layout.simple_dropdown_item_1line, Sparity);
		spiChooseParity.setAdapter(adapParity);

		spiChooseSerialPort.setSelection(0);
		spiChooseBaudRate.setSelection(0);
		spiChooseDataBits.setSelection(3);
		spiChooseStopBits.setSelection(0);
		spiChooseParity.setSelection(0);

		togBtnSerial.setOnCheckedChangeListener(this);
		togBtnReciveData.setOnCheckedChangeListener(this);
		togBtnSendPer100ms.setOnCheckedChangeListener(this);
		togBtnSendPer500ms.setOnCheckedChangeListener(this);
		togBtnSendPer1000ms.setOnCheckedChangeListener(this);

	}

	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
				case 1:
					Date date = new Date();
					eTextShowMsg.append("[" + date.getMinutes() + ":"
							+ date.getSeconds() + "] " + (CharSequence) msg.obj);
					break;
				default:
					break;
			}
		};
	};

	/**
	 * 接收数据线程
	 */
	class ReceiveThread extends Thread {
		public void run() {

			while (serialPort.isOpen) {
				if (isReceive) {
					String type = togBtnShowDataType.getText().toString()
							.trim();
					String data = serialPort.receiveData(type);
					if (data != null) {
						Message msg = new Message();
						msg.what = 1;
						msg.obj = data;
						System.out.println(data + "<<<<<<<<==========data");
						mHandler.sendMessage(msg);
					}
				}
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		switch (buttonView.getId()) {
			case R.id.togBtnSerial:// 打开关闭串口
				if (isChecked) {
					port = m_iSerialPort[spiChooseSerialPort
							.getSelectedItemPosition()];
					speed = baudrate[spiChooseBaudRate.getSelectedItemPosition()];
					dataBit = databits[spiChooseDataBits.getSelectedItemPosition()];
					stopBit = stopbits[spiChooseStopBits.getSelectedItemPosition()];
					parity = paritys[spiChooseParity.getSelectedItemPosition()];
					Log.i("MainActivity", "====>>>>打开串口");
					// 打开串口
					serialPort = new SerialPort(port, speed, dataBit, stopBit,
							parity);
					new ReceiveThread().start();
				} else {
					serialPort.closeSerial();
				}
				break;
			case R.id.togBtnReciveDate:
				if (isChecked) {
					isReceive = true;
				} else {
					isReceive = false;
				}
				break;
			case R.id.togBtnSendPer100ms:
				if (isChecked) {
					mTimerSendThread = new TimerSendThread();
					mTimerSendThread.setSleepTimer(100);
					mTimerSendThread.start();
					btnSend.setEnabled(false);
					togBtnSendPer500ms.setEnabled(false);
					togBtnSendPer1000ms.setEnabled(false);
				} else {
					if (mTimerSendThread != null) {
						mTimerSendThread.stopThread();
						mTimerSendThread = null;
						btnSend.setEnabled(true);
						togBtnSendPer500ms.setEnabled(true);
						togBtnSendPer1000ms.setEnabled(true);
					}
				}
				break;
			case R.id.togBtnSendPer500ms:
				if (isChecked) {
					mTimerSendThread = new TimerSendThread();
					mTimerSendThread.setSleepTimer(500);
					mTimerSendThread.start();
					btnSend.setEnabled(false);
					togBtnSendPer100ms.setEnabled(false);
					togBtnSendPer1000ms.setEnabled(false);
				} else {
					if (mTimerSendThread != null) {
						mTimerSendThread.stopThread();
						mTimerSendThread = null;
						btnSend.setEnabled(true);
						togBtnSendPer100ms.setEnabled(true);
						togBtnSendPer1000ms.setEnabled(true);
					}
				}
				break;
			case R.id.togBtnSendPer1000ms:
				if (isChecked) {
					mTimerSendThread = new TimerSendThread();
					mTimerSendThread.setSleepTimer(1000);
					mTimerSendThread.start();
					btnSend.setEnabled(false);
					togBtnSendPer500ms.setEnabled(false);
					togBtnSendPer100ms.setEnabled(false);
				} else {
					if (mTimerSendThread != null) {
						mTimerSendThread.stopThread();
						mTimerSendThread = null;
						btnSend.setEnabled(true);
						togBtnSendPer500ms.setEnabled(true);
						togBtnSendPer100ms.setEnabled(true);
					}
				}
				break;
		}

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.clearButton:
				eTextShowMsg.setText("");
				break;
			case R.id.sendButton:
				sendData();
				break;
		}
	}

	/**
	 * 调用串口发送数据
	 */
	private void sendData() {
		if (serialPort == null || !serialPort.isOpen)
			return;
		String type = togBtnSendDataType.getText().toString().trim();
		serialPort.sendData(eTextSendMsg.getText().toString().trim(), type);
	}

	private class TimerSendThread extends Thread {

		private long m_lTimer = 100; // default 100ms
		private boolean m_bRunFlag = true;

		@Override
		public void run() {
			// TODO Auto-generated method stub
			super.run();
			while (m_bRunFlag) {
				sendData();
				if (m_lTimer <= 0) { // must over 0ms
					m_lTimer = 100;
				}
				try {
					Thread.sleep(m_lTimer);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		public void setSleepTimer(long timer) {
			m_lTimer = timer;
		}

		public void stopThread() {
			m_bRunFlag = false;
		}
	}
}
