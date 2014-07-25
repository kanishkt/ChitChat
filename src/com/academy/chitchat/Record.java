package com.academy.chitchat;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import com.academy.chitchat.FileDownloader.FileDownloaderTask;
import com.academy.chitchat.FileDownloader.FlushedInputStream;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class Record extends Fragment {
	private static final int RECORDER_SAMPLERATE = 8000;
	private static final int RECORDER_CHANNELS = AudioFormat.CHANNEL_IN_MONO;
	private static final int RECORDER_AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT;
	private AudioRecord recorder = null;
	private Thread recordingThread = null;
	private boolean isRecording = false;
	static final String filePath = Environment.getExternalStorageDirectory()
			+ File.separator + "sound.pcm";
	

	private void setButtonHandlers() {
		((Button) getActivity().findViewById(R.id.recordButton))
				.setOnClickListener(btnClick);
		((Button) getActivity().findViewById(R.id.redoButton))
				.setOnClickListener(btnClick);
		
	}

	private void enableButton(int id, boolean isEnable) {
		((Button) getActivity().findViewById(id)).setEnabled(isEnable);
	}

	private void enableButtons(boolean isRecording) {
		enableButton(R.id.recordButton, !isRecording);
		enableButton(R.id.redoButton, isRecording);
	}

	int BufferElements2Rec = 1024; // want to play 2048 (2K) since 2 bytes we
									// use only 1024
	int BytesPerElement = 2; // 2 bytes in 16bit format

	private void startRecording() {

		recorder = new AudioRecord(MediaRecorder.AudioSource.MIC,
				RECORDER_SAMPLERATE, RECORDER_CHANNELS,
				RECORDER_AUDIO_ENCODING, BufferElements2Rec * BytesPerElement);

		recorder.startRecording();
		isRecording = true;
		recordingThread = new Thread(new Runnable() {
			public void run() {
				writeAudioDataToFile();
			}
		}, "AudioRecorder Thread");
		recordingThread.start();
	}

	// convert short to byte
	private byte[] short2byte(short[] sData) {
		int shortArrsize = sData.length;
		byte[] bytes = new byte[shortArrsize * 2];
		for (int i = 0; i < shortArrsize; i++) {
			bytes[i * 2] = (byte) (sData[i] & 0x00FF);
			bytes[(i * 2) + 1] = (byte) (sData[i] >> 8);
			sData[i] = 0;
		}
		return bytes;

	}

	private void writeAudioDataToFile() {
		// Write the output audio in byte

		short sData[] = new short[BufferElements2Rec];

		FileOutputStream os = null;
		try {
			os = new FileOutputStream(filePath);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		while (isRecording) {
			// gets the voice output from microphone to byte format

			recorder.read(sData, 0, BufferElements2Rec);
			System.out.println("Short wirting to file" + sData.toString());
			try {
				// // writes the data to file from buffer
				// // stores the voice buffer
				byte bData[] = short2byte(sData);
				os.write(bData, 0, BufferElements2Rec * BytesPerElement);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		try {
			os.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void stopRecording() {
		// stops the recording activity
		if (null != recorder) {
			isRecording = false;
			recorder.stop();
			recorder.release();
			recorder = null;
			recordingThread = null;
			MainActivity maObject = (MainActivity) getActivity();
			maObject.goToContacts();

			/*
			 * String s = "12178192553"; String r = "[1111111111,2222222222]";
			 * MainActivity.c.upload(filePath, s, r);
			 */
		}
	}

	private View.OnClickListener btnClick = new View.OnClickListener() {
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.recordButton: {
				enableButtons(true);
				startRecording();
				break;
			}
			case R.id.redoButton: {
				enableButtons(false);
				stopRecording();
				break;
			}
			}
			}
	};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.record, null);
	}

	@Override
	public void onStart() {
		setButtonHandlers();
		super.onStart();
	}
}
