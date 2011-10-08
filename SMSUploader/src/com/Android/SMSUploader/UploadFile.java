package com.Android.SMSUploader;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class UploadFile {
		HttpURLConnection connection = null;
		DataOutputStream outputStream = null;
		DataInputStream inputStream = null;
	
		static String defaultPathToFile = "/data/file_to_send.mp3";
		static String defaultUrlServer = "http://192.168.1.1/handle_upload.php";
		
		private static final String lineEnd = "\r\n";
		private static final  String twoHyphens = "--";
		private static final  String boundary =  "*****";
	
		int bytesRead, bytesAvailable, bufferSize;
		byte[] buffer;
		int maxBufferSize = 1024*1024;
		
		/**
		 * Holds Response or Error of Upload Connection.
		 */
		public String response = "";
		
		public UploadFile(String pathToFile, String urlServer) {
			try
			{
			FileInputStream fileInputStream = new FileInputStream(new File(pathToFile) );
		
			URL url = new URL(urlServer);
			connection = (HttpURLConnection) url.openConnection();
		
			// Allow Inputs & Outputs
			connection.setDoInput(true);
			connection.setDoOutput(true);
			connection.setUseCaches(false);
		
			// Enable POST method
			connection.setRequestMethod("POST");
		
			connection.setRequestProperty("Connection", "Keep-Alive");
			connection.setRequestProperty("Content-Type", "multipart/form-data;boundary="+boundary);
		
			outputStream = new DataOutputStream( connection.getOutputStream() );
			outputStream.writeBytes(twoHyphens + boundary + lineEnd);
			outputStream.writeBytes("Content-Disposition: form-data; name=\"uploadedfile\";filename=\"" + pathToFile +"\"" + lineEnd);
			outputStream.writeBytes(lineEnd);
		
			bytesAvailable = fileInputStream.available();
			bufferSize = Math.min(bytesAvailable, maxBufferSize);
			buffer = new byte[bufferSize];
		
			// Read file
			bytesRead = fileInputStream.read(buffer, 0, bufferSize);
		
			while (bytesRead > 0)
			{
			outputStream.write(buffer, 0, bufferSize);
			bytesAvailable = fileInputStream.available();
			bufferSize = Math.min(bytesAvailable, maxBufferSize);
			bytesRead = fileInputStream.read(buffer, 0, bufferSize);
			}
		
			outputStream.writeBytes(lineEnd);
			outputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
		
			// Responses from the server (code and message)
			int serverResponseCode = connection.getResponseCode();
			String serverResponseMessage = connection.getResponseMessage();
			response = serverResponseMessage + "CODE: " + serverResponseCode;
		
			fileInputStream.close();
			outputStream.flush();
			outputStream.close();
			}
			catch (Exception ex)
			{
				response = ex.toString();
			}
			
		}
		
		public UploadFile() {
			this(defaultPathToFile, defaultUrlServer);
		}
			
}

