package entity;

import java.util.Set;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class Item {
	private String itemId;
	private String name;
	private double rating;
	private String address;
	private Set<String> categories;
	private String imageUrl;
	private String url;
	private double distance;	
	// Getters
	public String getItemId() {
		return itemId;
	}
	public String getName() {
		return name;
	}
	public double getRating() {
		return rating;
	}
	public String getAddress() {
		return address;
	}
	public Set<String> getCategories() {
		return categories;
	}
	public String getImageUrl() {
		return imageUrl;
	}
	public String getUrl() {
		return url;
	}
	public double getDistance() {
		return distance;
	}
	
	//=============================
	// 用builder pattern 易于扩展
	private Item(ItemBuilder builder) {
		this.itemId = builder.itemId;
		this.name = builder.name;
		this.rating = builder.rating;
		this.address = builder.address;
		this.categories = builder.categories;
		this.imageUrl = builder.imageUrl;
		this.url = builder.url;
		this.distance = builder.distance;	
	}		
	//==================================
	// 在类Item里再新建一个类ItemBuilder，把所有fields放入item builder
	// 主要用于当field比较多的时候进行整合操作 -- builder pattern
	public static class ItemBuilder {
		private String itemId;
		private String name;
		private double rating;
		private String address;
		private Set<String> categories;
		private String imageUrl;
		private String url;
		private double distance;		
		// Setters to all fields
		public ItemBuilder setItemId(String itemId) {
			this.itemId = itemId;
			return this;
		}
		public ItemBuilder setName(String name) {
			this.name = name;
			return this;
		}
		public ItemBuilder setRating(double rating) {
			this.rating = rating;
			return this;
		}
		public ItemBuilder setAddress(String address) {
			this.address = address;
			return this;
		}
		public ItemBuilder setCategories(Set<String> categories){			
			this.categories = categories;
			return this;
		}
		public ItemBuilder setImageUrl (String imageUrl) {
			this.imageUrl = imageUrl;
			return this;
		}
		public ItemBuilder setUrl (String url) {
			this.url = url;
			return this;
		}
		public ItemBuilder setDistance(double distance) {
			this.distance = distance;
			return this;
		}
		//定义一个方法 build（），用来create一个ItemBuilder object； 返回是item
		public Item build() {
			return new Item(this);
		// new一个item构造函数，里面接收到的是itembuilder
		// 把item builder里的值转化为item里面的对象
		// itemBuilder.build --> item instance (this==itembuilder:builder)
		}
	}
	
	/**
	 * This is a builder pattern in Java.
	 */
	// create a private constructor to use the defined builder pattern	

	//把item的list转化为jsonobject
	public JSONObject toJSONObject() {
		JSONObject object = new JSONObject();
		try {
			object.put("item_id", itemId);
			object.put("name", name);
			object.put("rating", rating);
			object.put("address", address);
			object.put("categories", new JSONArray(categories));
			object.put("url", url);
			object.put("image_url", imageUrl);
			object.put("distace", distance);
		}catch (JSONException e) {
			e.printStackTrace();
		}
		return object;
		}

}
