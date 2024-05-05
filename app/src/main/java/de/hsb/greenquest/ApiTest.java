import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import java.net.http.HttpRequest;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;


import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;


public class ApiTest {
    private static final String PROJECT = "all"; 
    private static final String APIKEY = "2b10RYl9lUThHrzmylGhx2juTO";
    private static final String GET_URL = "https://my-api.plantnet.org/v2/identify/all?images=https%3A%2F%2Fupload.wikimedia.org%2Fwikipedia%2Fcommons%2Fb%2Fbf%2FSucculent_plant.JPG&include-related-images=false&no-reject=false&lang=en&api-key=2b10RYl9lUThHrzmylGhx2juTO";
    
    private static final String POST_URL = "https://my-api.plantnet.org/v2/identify/all?include-related-images=false&no-reject=false&lang=en&api-key=2b10RYl9lUThHrzmylGhx2juTO";

    private static File binaryFile = new File("C:\\Users\\Burchard\\OneDrive\\Documents\\studium\\Stoff\\semster_6\\mobile_computing\\test\\src\\Succulent_plant.jpeg");
    private static final String IMAGE = "C:\\Users\\Burchard\\OneDrive\\Documents\\studium\\Stoff\\semster_6\\mobile_computing\\test\\src\\Succulent_plant.jpeg";
	private static final String URL = "https://my-api.plantnet.org/v2/identify/" + PROJECT + "?api-key=2b10RYl9lUThHrzmylGhx2juTO";


    public static void main(String[] args) {
        try {
            sendPOST();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private static void sendHttpGETRequest() throws IOException {
        URL obj = new URL(GET_URL);
        HttpURLConnection httpURLConnection = (HttpURLConnection) obj.openConnection();
        httpURLConnection.setRequestMethod("GET");
        System.out.println(httpURLConnection.getURL());
        int responseCode = httpURLConnection.getResponseCode();
        System.out.println("GET Response Code :: " + responseCode);
        if (responseCode == HttpURLConnection.HTTP_OK) { // success
            BufferedReader in = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in .readLine()) != null) {
                response.append(inputLine);
            } in .close();

            // print result
            System.out.println(response.toString());
        } else {
            System.out.println("GET request not worked");
        }
    }

    private static void sendPOST() throws IOException {
		File file1 = new File(IMAGE);

		HttpEntity entity = MultipartEntityBuilder.create()
			.addPart("images", new FileBody(file1)).addTextBody("organs", "flower")
			.build();

		HttpPost request = new HttpPost(URL);
		request.setEntity(entity);

		HttpClient client = HttpClientBuilder.create().build();
		HttpResponse response;

		try {
			response = client.execute(request);
			String jsonString = EntityUtils.toString(response.getEntity());
			System.out.println(jsonString);
		} catch (IOException e) {
			e.printStackTrace();
		
	    }
	}
}
