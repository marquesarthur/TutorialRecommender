package Utilities;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class SortedMapByValues<K  extends Comparable<K>,V  extends Comparable<V>> {

        HashMap<K,V> map;
        TreeMap<K,V> sorted_map;
        ValueComparator<K,V> bvc;
        
        public SortedMapByValues()
        {
        	map = new HashMap<K,V>();
            bvc =  new ValueComparator<K,V>(map);
            sorted_map = new TreeMap<K,V>(bvc);
        }
        
        public void put(K key, V value)
        {
        	map.put(key, value);
        	sorted_map.put(key, value);
        }
        
        public Map<K, V> topN(int n)
        {
        	Map<K, V> subMap = new TreeMap<K, V>(bvc);
        	
        	int index = 0;
        	for(K key:sorted_map.keySet())
        	{
        		if (index >= n)
        			break;
        		index++;
        		subMap.put(key, sorted_map.get(key));
        	}
        	
        	return subMap;
        }
        
        public V getNValue(int n)
        {
        	K key = (K) sorted_map.keySet().toArray()[n];
        			
        	return sorted_map.get(key);
        }
        
        public Map<K, V> getByThreshold(V minValue)
        {
        	Map<K, V> subMap = new TreeMap<K, V>(bvc);
      
        	for(K key:sorted_map.keySet())
        		if (sorted_map.get(key).compareTo(minValue) >= 0)        			
        			subMap.put(key, sorted_map.get(key));
        	
        	return subMap;
        }
        
        public void printAll()
        {
        	for(K key:sorted_map.keySet())
        		System.out.println(key + "-" + sorted_map.get(key).toString());
        }
        
                
  }

class ValueComparator<K extends Comparable<K>,V extends Comparable<V>> implements Comparator<K> {

    Map<K, V> base;
    public ValueComparator(Map<K, V> base) {
        this.base = base;
    }

    // Note: this comparator imposes orderings that are inconsistent with equals.    
    public int compare(K a, K b) {
    	int res = base.get(a).compareTo(base.get(b));
        if (a.equals(b)) {
            return res; // Code will now handle equality properly
        } else {
            return res != 0 ? res : 1; // While still adding all entries
        }
    }
}