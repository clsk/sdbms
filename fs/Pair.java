import java.lang.Number;

public class Pair <T, V> {
	private T key = null;
	private V value = null;
	
	public Pair (T theKey, V theValue){
		key = theKey;
		value = theValue;
	}
	
	public T getKey(){
		return this.key;
	}
	
	public void setKey(T key){
		this.key = key;
	}
	
	public V getValue(){
		return this.value;
	}
	
	public void setValue(V value){
		this.value = value;
	}
}