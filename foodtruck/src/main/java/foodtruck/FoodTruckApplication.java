package foodtruck;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class FoodTruckApplication {

	static void displayFoodTrucks(String foodTrucks) throws IOException {

		JSONArray arrayJson = new JSONArray(foodTrucks.toString());

		LocalDate localDate = LocalDate.now();
		LocalTime localTime = LocalTime.now();

		String pattern = "hha";
		String hour = String.valueOf(localTime.format(DateTimeFormatter.ofPattern(pattern)));
		String presentDayOfWeek = localDate.getDayOfWeek().name();

		int count = 0;
		List<JSONObject> sortingList = new ArrayList<JSONObject>();

		for (int i = 0; i < arrayJson.length(); i++) {

			JSONObject obj = arrayJson.getJSONObject(i);
			String jsonWeekDay = obj.getString("dayofweekstr");
			String hourOfTheDay = obj.getString("endtime");

			String value = null;

			//Filtering Json objects based on current Day of the week and Current Time
			//Checking the End time of the truck
			if (presentDayOfWeek.equalsIgnoreCase(jsonWeekDay) && hourOfTheDay.equalsIgnoreCase(hour)) {

				sortingList.add(obj);
				count++;
				if (count == 10) {
					sortTheListOnApplicants(sortingList);
					System.out.println("Please enter a key to continue");
					BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
					value = bufferedReader.readLine();
					if (value != null) {
						count = 0;
						sortingList.clear();
						continue;
					} else {
						break;
					}
				} else if (count < 10) {
					sortTheListOnApplicants(sortingList);
					sortingList.clear();
				}
			}
		}

	}

	// Sorting the JSON Objects based on Applicants key, as Json does not have
	// Truck name for sorting
	public static void sortTheListOnApplicants(List<JSONObject> sortingList) {

		if (sortingList != null) {
			Collections.sort(sortingList, new Comparator<JSONObject>() {
				@Override
				public int compare(JSONObject jsonObjectA, JSONObject jsonObjectB) {
					String keyA = new String();
					String keyB = new String();
					try {
						keyA = jsonObjectA.getString("applicant");
						keyB = jsonObjectB.getString("applicant");
					} catch (JSONException e) {
						e.printStackTrace();
					}
					return keyA.compareTo(keyB);
				}
			});

			sortingList.forEach(value -> System.out.println("Day Order of Truck " + value.getString("dayorder")
					+ " ADDRESS " + value.getString("location") + " Applicant  " + value.getString("applicant")));
		}

	}

	public static void main(String[] args) {
		try {
			StringBuilder result = new StringBuilder();
			URL url = new URL("https://data.sfgov.org/resource/jjew-r69b.json");
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String line;
			while ((line = rd.readLine()) != null) {
				result.append(line);
			}
			rd.close();

			displayFoodTrucks(result.toString());
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

	}

}
