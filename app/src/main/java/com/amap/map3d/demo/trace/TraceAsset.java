package com.amap.map3d.demo.trace;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.amap.api.trace.TraceLocation;

import android.content.res.AssetManager;

public class TraceAsset {
	public static List<TraceLocation> parseLocationsData(
			AssetManager mAssetManager, String filePath) {
		List<TraceLocation> locLists = new ArrayList<TraceLocation>();
		InputStream input = null;
		InputStreamReader inputReader = null;
		BufferedReader bufReader = null;
		try {
			input = mAssetManager.open(filePath);
			inputReader = new InputStreamReader(input);
			bufReader = new BufferedReader(inputReader);
			String line = "";
			while ((line = bufReader.readLine()) != null) {
				JSONObject traceItem = new JSONObject(line);
				TraceLocation location = new TraceLocation();
				location.setLatitude(traceItem.optDouble("lat"));
				location.setLongitude(traceItem.optDouble("lon"));
				location.setSpeed((float) traceItem.optDouble("speed"));
				location.setBearing((float) traceItem.optDouble("bearing"));
				location.setTime(traceItem.optLong("loctime"));
				locLists.add(location);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (bufReader != null) {
					bufReader.close();
					bufReader = null;
				}
				if (inputReader != null) {
					inputReader.close();
					inputReader = null;
				}
				if (input != null) {
					input.close();
					input = null;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return locLists;
	}

	public static String[] recordNames(AssetManager mAssetManager) {
		try {
			return mAssetManager.list("traceRecord");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}
