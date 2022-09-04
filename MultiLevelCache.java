import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class MultiLevelCache {
    static class Node{
        int key;
        int value;
        Node(int key,int value){
            this.key = key;
            this.value = value;
        }
    }

    public static HashMap<Integer, ArrayDeque<Node>> map = new HashMap<>();
    public static HashMap<Integer,Integer> size = new HashMap<>();
    public static int lastLevel = 2;
    public static void deleteKey(int key){
        for(Map.Entry<Integer,ArrayDeque<Node>> entry:map.entrySet()){
            Iterator<Node> itr = entry.getValue().iterator();
            while(itr.hasNext()){
                if(itr.next().key == key){
                    entry.getValue().remove(itr.next());
                }
            }
        }
    }
    public static void writeKey(int key,int value){
        Node evictedNode = null;
        Node newNode = new Node(key,value);
        for(Map.Entry<Integer,ArrayDeque<Node>> entry:map.entrySet()){
            if(entry.getValue().size() == size.get(entry.getKey())){
                evictedNode = evictNode(entry.getValue());
                entry.getValue().addFirst(newNode);
                newNode = evictedNode;
                if(entry.getKey() == lastLevel){
                    ArrayDeque<Node> lastlevel = new ArrayDeque<>();
                    lastlevel.addFirst(newNode);
                    lastLevel++;
                    map.put(lastLevel,lastlevel);
                    size.put(lastLevel,2);
                }
            }
            else{
                entry.getValue().addFirst(newNode);
                return;
            }
        }

    }
    public static int read(int key){
        Node currentNode = null;
        for(Map.Entry<Integer,ArrayDeque<Node>> entry:map.entrySet()){
            Iterator<Node> itr = entry.getValue().iterator();
            while(itr.hasNext()){
                Node nd = itr.next();
                int itrkey = nd.key,itrvalue = nd.value;
                if(itrkey == key){
                   currentNode = new Node(itrkey,itrvalue);
                   entry.getValue().remove(nd);
                   break;
                }
            }
        }
        writeKey(currentNode.key,currentNode.value);
        return currentNode.value;
    }
    public static Node evictNode(ArrayDeque<Node> dq){
        return dq.removeLast();
    }
    public static void main(String[] args){
        map.put(1,new ArrayDeque<Node>(){
            {
                add(new Node(100,200));
                add(new Node(300,600));
            }
        });
        map.put(2,new ArrayDeque<Node>(){
            {
                add(new Node(500,200));
            }
        });
        size.put(1,2);
        size.put(2,1);
        writeKey(10,20);
        writeKey(20,30);
        writeKey(40,50);
        writeKey(60,70);

        read(300);
        read(40);

    }
}
