package com.et.simon.smartlight.udp;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.Arrays;

public class UDPSocketServer {

	private static final String TAG = "UDPSocketServer";
	private DatagramPacket mReceivePacket;
	private DatagramSocket mServerSocket;
	private Context mContext;
	private WifiManager.MulticastLock mLock;
	private final byte[] buffer;
	private volatile boolean mIsClosed;

	private synchronized void acquireLock() {
		if (mLock != null && !mLock.isHeld()) {
			mLock.acquire();
		}
	}

	private synchronized void releaseLock() {
		if (mLock != null && mLock.isHeld()) {
			try {
				mLock.release();
			} catch (Throwable th) {
                // ignoring this exception, probably wakeLock was already released
            }
		}
	}

    /**
     * UDP Socket 服务器构造函数
     * @param port
     *              Socket 服务器端口
     * @param socketTimeout
     *              Socket 读取超时
     * @param context
     *              应用上下文
     */
	public UDPSocketServer(int port, int socketTimeout, Context context) {
		this.mContext = context;
		this.buffer = new byte[64];
		this.mReceivePacket = new DatagramPacket(buffer, 64);
		try {
			this.mServerSocket = new DatagramSocket(port);
			this.mServerSocket.setSoTimeout(socketTimeout);
			this.mIsClosed = false;
			WifiManager manager = (WifiManager) mContext
					.getSystemService(Context.WIFI_SERVICE);
			mLock = manager.createMulticastLock("WIFI测试");
			Log.d(TAG, "Socket Server已创建, 读取 Socket 超时: "
					+ socketTimeout + ", 端口号:" + port);
		} catch (IOException e) {
			Log.e(TAG, "IOException");
			e.printStackTrace();
		}
	}

    /**
     * 设置 Socket 超时时长(单位:毫秒)
     * @param timeout
     *          超时时长,0为没有超时时长
     * @return true 设置超时成功
     */
	public boolean setSoTimeout(int timeout) {
		try {
			this.mServerSocket.setSoTimeout(timeout);
			return true;
		} catch (SocketException e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * Receive one byte from the port and convert it into String
	 * 
	 * @return
	 */
	public byte receiveOneByte() {
		Log.d(TAG, "receiveOneByte() entrance");
		try {
			acquireLock();
			mServerSocket.receive(mReceivePacket);
			Log.d(TAG, "receive: " + (0 + mReceivePacket.getData()[0]));
			return mReceivePacket.getData()[0];
		} catch (IOException e) {
			e.printStackTrace();
		}
		return Byte.MIN_VALUE;
	}
	
	/**
	 * Receive specific length bytes from the port and convert it into String
	 * 21,24,-2,52,-102,-93,-60
	 * 15,18,fe,34,9a,a3,c4
	 * @return
	 */
	public byte[] receiveSpecLenBytes(int len) {
		Log.d(TAG, "receiveSpecLenBytes() entrance: len = " + len);
		try {
			acquireLock();
			mServerSocket.receive(mReceivePacket);
			byte[] recDatas = Arrays.copyOf(mReceivePacket.getData(), mReceivePacket.getLength());
			Log.d(TAG, "received len : " + recDatas.length);
			for (int i = 0; i < recDatas.length; i++) {
				Log.e(TAG, "recDatas[" + i + "]:" + recDatas[i]);
			}
			Log.e(TAG, "receiveSpecLenBytes: " + new String(recDatas));
			if (recDatas.length != len) {
				Log.w(TAG,
						"received len is different from specific len, return null");
				return null;
			}
			return recDatas;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public void interrupt() {
		Log.i(TAG, "USPSocketServer is interrupt");
		close();
	}

	public synchronized void close() {
		if (!this.mIsClosed) {
			Log.e(TAG, "mServerSocket is closed");
			mServerSocket.close();
			releaseLock();
			this.mIsClosed = true;
		}
	}

	@Override
	protected void finalize() throws Throwable {
		close();
		super.finalize();
	}

}
