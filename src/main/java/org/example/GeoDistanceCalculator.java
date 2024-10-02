package org.example;


import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class GeoDistanceCalculator {

    public static void main(String[] args) throws Exception {
        // Входные JSON с координатами
        String jsonInput = "{ \"startPos\": [45.062609, 41.923656], \"endPos\": [57.165054, 65.498056] }";

        // Парсинг JSON
        JSONObject jsonObject = new JSONObject(jsonInput);

        // Извлечение координат
        JSONArray startPosArray = jsonObject.getJSONArray("startPos");
        JSONArray endPosArray = jsonObject.getJSONArray("endPos");

        // Преобразование JSONArray
        double[] startPos = new double[] {
                startPosArray.getDouble(0),
                startPosArray.getDouble(1)
        };
        double[] endPos = new double[] {
                endPosArray.getDouble(0),
                endPosArray.getDouble(1)
        };

        // Получение адресов через OpenStreetMap
        String startAddress = getAddressFromCoordinates(startPos[0], startPos[1]);
        String endAddress = getAddressFromCoordinates(endPos[0], endPos[1]);

        // Вывод в консоль
        System.out.println("Начальный адрес: " + startAddress);
        System.out.println("Конечный адрес: " + endAddress);

        // Расчет расстояния
        double distance = calculateDistance(startPos[0], startPos[1], endPos[0], endPos[1]);

        // Формировка ответа
        JSONObject response = new JSONObject();
        response.put("startPos", startPosArray);
        response.put("endPos", endPosArray);
        response.put("startAddress", startAddress);
        response.put("endAddress", endAddress);
        response.put("distance", distance);

        // Вывод результата
        System.out.println("Растояние: " + distance + " метров");
        System.out.println(response.toString(4));
    }

    // Метод для получения адреса с помощью API OpenStreetMap
    public static String getAddressFromCoordinates(double lat, double lon) throws Exception {
        String urlStr = "https://nominatim.openstreetmap.org/reverse?format=json&lat=" + lat + "&lon=" + lon + "&addressdetails=1";
        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("User-Agent", "Mozilla/5.0");

        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String inputLine;
        StringBuilder response = new StringBuilder();
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        // Парсинг ответа от Nominatim
        JSONObject jsonObject = new JSONObject(response.toString());
        JSONObject addressObject = jsonObject.getJSONObject("address");

        // Сборка полного адреса
        StringBuilder address = new StringBuilder();
        if (addressObject.has("road")) {
            address.append(addressObject.getString("road")).append(", ");
        }
        if (addressObject.has("city")) {
            address.append(addressObject.getString("city")).append(", ");
        }
        if (addressObject.has("state")) {
            address.append(addressObject.getString("state")).append(", ");
        }
        if (addressObject.has("country")) {
            address.append(addressObject.getString("country")).append(", ");
        }
        if (addressObject.has("postcode")) {
            address.append(addressObject.getString("postcode"));
        }

        return address.toString().trim();
    }

    // Метод для расчета расстояния с использованием формулы Haversine
    public static double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // Радиус Земли

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return R * c * 1000; // Расстояние в метрах
    }
}
