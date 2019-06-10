package recommendation;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import db.DBConnection;
import db.DBConnectionFactory;
//import db.mysql.MySQLConnection;
import entity.Item;

public class GeoRecommendation {
	  public List<Item> recommendItems(String userId, double lat, double lon) {
			List<Item> recommendedItems = new ArrayList<>();

			// Step 1, get all favorited itemids
			DBConnection connection = DBConnectionFactory.getConnection();
			Set<String> favoritedItemIds = connection.getFavoriteItemIds(userId);

			// Step 2, get all categories, 收藏内容可能有重合 sort by count
			// {"sports": 5, "music": 3, "art": 2} 优先推荐收藏次数多的 需要创建一个hashmap来对应这些信息
			Map<String, Integer> allCategories = new HashMap<>();
			for (String itemId : favoritedItemIds) {
				Set<String> categories = connection.getCategories(itemId);
				for (String category : categories) {
					allCategories.put(category, allCategories.getOrDefault(category, 0) + 1);
				}
			}
			// 对无序的hashmap里面的数据进行排序 方法是转化成list
			List<Entry<String, Integer>> categoryList = new ArrayList<>(allCategories.entrySet());
			Collections.sort(categoryList, (Entry<String, Integer> e1, Entry<String, Integer> e2) -> {
				return Integer.compare(e2.getValue(), e1.getValue());
			});
			
			// Step 3, search based on category, filter out favorite items
			//用hashset来标注之前的搜索结构有没有当前item
			Set<String> visitedItemIds = new HashSet<>();
			for (Entry<String, Integer> category : categoryList) {
				List<Item> items = connection.searchItems(lat, lon, category.getKey());
				
				for (Item item : items) {
					//如果之前已经收藏了 那么就不推荐； 如果这个item被其他的category搜索并推荐过 也不再推荐 （因为一个item可能属于多个categories）
					if (!favoritedItemIds.contains(item.getItemId()) && !visitedItemIds.contains(item.getItemId())) {
						recommendedItems.add(item);
						visitedItemIds.add(item.getItemId());
					}
				}
			}
			
			connection.close();
			return recommendedItems;
}


}
