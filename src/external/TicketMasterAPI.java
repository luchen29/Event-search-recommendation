package external;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import entity.Item;
import entity.Item.ItemBuilder;


public class TicketMasterAPI {
	private static final String URL = "https://app.ticketmaster.com/discovery/v2/events.json";
	private static final String DEFAULT_KEYWORD = ""; // no restriction
	private static final String API_KEY = "JLytlt53T8fwyi4y00XKlpniMuxAaHiK";
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		TicketMasterAPI tmApi = new TicketMasterAPI();
		// Mountain View, CA
		// tmApi.queryAPI(37.38, -122.08);
		// London, UK
		// tmApi.queryAPI(51.503364, -0.12);
		// Houston, TX
		tmApi.queryAPI(29.682684, -95.295410);
	}
	
	//调用这个函数的时候 输入要查询的keyword 以供里面的query中输入使用
	public List<Item> search(double lat, double lon, String keyword) {
		if (keyword==null) {
			keyword=DEFAULT_KEYWORD;
		}
		try {
			// 把用户输入的任何东西转化成 UTF-8格式; 如果遇到空格->转化为%20 因为空格不可以在关键字里，他本身是一个关键字。
			keyword = URLEncoder.encode(keyword, "UTF-8");
		} catch(UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		//用户query时候的format, 用后面对应的值replace前面的%s
		//string 自带的函数format
		String query = String.format("apikey=%s&latlong=%s,%s&keyword=%s&radius=%s", API_KEY, lat, lon, keyword, 50);
		String url = URL + "?" + query;
		try {
			//先尝试链一下url看能否链接成功（即这里是否拼接正确）
			HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
			connection.setRequestMethod("GET");
			//这句话真正的才调用api call出去发请求；随后获得response code是否为200
			int responseCode = connection.getResponseCode();
			System.out.println("Sending request to url: " + url);
			System.out.println("Response code: " + responseCode);
			//查看response code是否为200 即check是成功访问到api server
			if (responseCode!=200) {
				return new ArrayList<>();
			}
			//一段一段的读返回回来的字符流 效率会快一些
			//必须把内容放到iput stream reader来面buffer才能读
			//分块，一块是8k的字符 此时内容已经在内存里了
			BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String line;
			StringBuilder response = new StringBuilder();
			//这里才真正读出来
			while((line = reader.readLine())!=null) {
				response.append(line);
			}
			//读完了
			reader.close();
			//把返回来的response 放到json object里面
			JSONObject obj = new JSONObject(response.toString());
			
			if (!obj.isNull("_embedded")) {
				JSONObject embedded = obj.getJSONObject("_embedded");
				return getItemList(embedded.getJSONArray("events"));
			}
		} catch (Exception e) {
			//如果能Handel这个error就catch住；否则就抛出去
			e.printStackTrace();
		}
		return new ArrayList<>();
	}

	//一个测试函数
	private void queryAPI(double lat, double lon) {
		List<Item> events = search(lat, lon, null);

		for (Item event : events) {
			System.out.println(event.toJSONObject());
		}
	}
	
	// ticketmaster 返回的内容太多了，这里要把其中有用的部分提取出来。
	// 在ticketmaster返回来的jsonarray中我们需要的部分取出来，变成item object
	private List<Item> getItemList(JSONArray events) throws JSONException{
		List<Item> itemList = new ArrayList<>();
		for (int i=0; i<events.length();++i) {
			JSONObject event = events.getJSONObject(i);
			ItemBuilder builder = new ItemBuilder();
			if(!event.isNull("id")) {
				builder.setItemId(event.getString("id"));
			}
			if(!event.isNull("name")) {
				builder.setName(event.getString("name"));
			}
			if(!event.isNull("url")) {
				builder.setUrl(event.getString("url"));
			}
			if(!event.isNull("distance")) {
				builder.setDistance(event.getDouble("distance"));
			}
//			System.out.println(getImageUrl(event));
			builder.setAddress(getAddress(event));
			builder.setImageUrl(getImageUrl(event));
			builder.setCategories(getCategories(event));
			
			itemList.add(builder.build());
			
		}
		return itemList;
	}
	
	
	private String getAddress(JSONObject event) throws JSONException{
		if (!event.isNull("_embedded")) {
			JSONObject embedded = event.getJSONObject("_embedded");
			if (!embedded.isNull("venues")) {
				JSONArray venues = embedded.getJSONArray("venues");
				for(int i=0;i<venues.length();++i) {
					JSONObject venue = venues.getJSONObject(i);
					StringBuilder builder = new StringBuilder();
					if (!venue.isNull("address")) {
						JSONObject address = venue.getJSONObject("address");
						if (!address.isNull("line1")) {
							builder.append(address.getString("line1"));
						}
						if (!address.isNull("line2")) {
							builder.append(",");
							builder.append(address.getString("line2"));
						}
						if (!address.isNull("line3")) {
							builder.append(",");
							builder.append(address.getString("line3"));
						}
					}
					if (!venue.isNull("city")) {
						JSONObject city = venue.getJSONObject("city");
						builder.append(",");
						builder.append(city.getString("name"));
					}
					//??????这里一定要变成string 返回吗？
					String result = builder.toString();
					if (!result.isEmpty()) {
						return result;
					}
				}
			}
		}
		return "";
	}
	
	private String getImageUrl(JSONObject event) throws JSONException{
		if (!event.isNull("images")) {
			JSONArray array = event.getJSONArray("images");
			for (int i=0; i<array.length();++i) {
				JSONObject image = array.getJSONObject(i);
				if (!image.isNull("url")) {
					return image.getString("url");
				}
			}			
		}
		return "";
	}
	
	private Set<String> getCategories(JSONObject event) throws JSONException {
		Set<String> categories = new HashSet<>();
		if(!event.isNull("classifications")) {
			JSONArray classifications = event.getJSONArray("classifications");
			for(int i=0;i<classifications.length();++i) {
				JSONObject classification = classifications.getJSONObject(i);
				if (!classification.isNull("segment")) {
					JSONObject segment = classification.getJSONObject("segment");
					if (!segment.isNull("name")) {
						categories.add(segment.getString("name"));
					}
				}
			}
		}
		return categories;
	}
	
	
}
