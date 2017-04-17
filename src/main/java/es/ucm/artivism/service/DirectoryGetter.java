/**
 * 
 */
package es.ucm.artivism.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import es.ucm.artivism.data.PostVO;

/**
 * @author Ivan
 *
 */
public class DirectoryGetter {

	public List<PostVO> getPosts(final Integer maxPosts, final ServletContext servletContext) {
		
		
		String id = "GRUMPY";
		String title = "Grumpy cat";
		String imgUrl = servletContext.getRealPath("/")+"imgs/"+ "grumpyCatIco";
		String fileUrl = servletContext.getRealPath("/")+"imgs/" + "grumpyCatIco.jpg";
		String description = "This is an example, programmed post, for demo and debugging purposes.";
		String location = "Pastoor Peterstraat 127, Eindhoven";
		String author = "Ivan Mikovski";
		Float longitude = null;
		Float latitude = null;
		if((longitude == null || latitude == null) && location != null && !location.isEmpty()){
			try {
				Map<String, Float> geoData = invokeGeoService(location);
				longitude = geoData.get("longitude");
				latitude = geoData.get("latitude");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		PostVO example = new PostVO(id, title, imgUrl, fileUrl, description, location, author,longitude, latitude);
			
		ArrayList<PostVO> result = new ArrayList<PostVO>();
		result.add(example);
		return result;
	}
	
	
	private String urlEncode(final String value){
        try{
            return URLEncoder.encode(value, "UTF-8");
        }catch (UnsupportedEncodingException ex){
            throw new RuntimeException(ex.getMessage());
        }
    }
	
	protected Map<String, Float> invokeGeoService(final String address) throws IOException{
		String apikey = "AIzaSyA6XC8KV0_zapNHH6KEsII4fs7YbwsMHUc";
		String url = "https://maps.googleapis.com/maps/api/geocode/json?address=" + urlEncode(address) + "&key=" + apikey;
		
		String url2 = " http://nominatim.openstreetmap.org/search/" + urlEncode(address) + "?format=json&addressdetails=1&limit=1";
		
		URL website = new URL(url2);
        URLConnection connection = website.openConnection();
        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));

        StringBuilder response = new StringBuilder();
        String inputLine;

        while ((inputLine = in.readLine()) != null) 
            response.append(inputLine);
        
        in.close();
        
        System.out.println(response.toString());
        JsonParser parser = new JsonParser();
        JsonObject obj = parser.parse(response.toString()).getAsJsonArray().get(0).getAsJsonObject();
        String longitude = obj.get("lon").getAsString(); // works for url2
        String latitude = obj.get("lat").getAsString();
        

        Map<String, Float> result = new HashMap<String, Float>();
        result.put("longitude", Float.parseFloat(longitude));
        result.put("latitude", Float.parseFloat(latitude));
        
        return result;
    }
}
