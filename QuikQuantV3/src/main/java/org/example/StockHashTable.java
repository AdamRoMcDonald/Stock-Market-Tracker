package org.example;

import java.util.ArrayList;
import java.util.LinkedList;

public class StockHashTable<K,V> {

    public int hashTableSize = 3;
    public LinkedList<Entry<K, V>>[] hashTable;
    public int numElements;

    public StockHashTable() {
        hashTable = new LinkedList[hashTableSize - 1];
        numElements = 0;
    }

    public void put(K key, V value) {
        if (key == null)
            throw new IllegalArgumentException("Key cannot be null");

        int curPos = getIndex(key);
        if (hashTable[curPos] == null) {
            hashTable[curPos] = new LinkedList<>();
        }


        for (Entry<K, V> entry : hashTable[curPos]) {
            if (entry.key.equals(key)) {
                entry.value = value;
                return;
            }
        }

        hashTable[curPos].add(new Entry<>(key, value));
        numElements++;


        if (numElements > hashTable.length * 0.75) {
            renumElements();
        }
    }

    public V get(K key) {
        if (key == null)
            throw new IllegalArgumentException("Key cannot be null");

        int curPos = getIndex(key);
        if (hashTable[curPos] != null) {
            for (Entry<K, V> entry : hashTable[curPos]) {
                if (entry.key.equals(key)) {
                    return entry.value;
                }
            }
        }
        return null;
    }


    public boolean remove(K key) {
        if (key == null)
            System.out.println("Key is null, fix it.");

        int index = getIndex(key);
        if (hashTable[index] != null) {
            hashTable[index].removeIf(entry -> entry.key.equals(key));
            numElements--;
            return true;
        }
        return false;
    }

    public int getIndex(K key) {
        long index = Math.abs((long)key.hashCode()) % hashTable.length;
        int indexInt = (int)index;
        return indexInt;
    }

    private void renumElements() {
        LinkedList<Entry<K, V>>[] oldTable = hashTable;
        hashTable = new LinkedList[intFindNextPrime(hashTable.length * 2)];
        numElements = 0;
        for (LinkedList<Entry<K, V>> list : oldTable) {
            if (list != null) {
                for (Entry<K, V> entry : list) {
                    put(entry.key, entry.value);
                }
            }
        }
    }


    public ArrayList<Entry<K, V>> getAllContents() {
        ArrayList<Entry<K, V>> allElements = new ArrayList<>();
        for (LinkedList<Entry<K, V>> list : hashTable) {
            if (list != null) {
                for (Entry<K, V> entry : list) {

                    allElements.add(entry);
//                    System.out.println("Key: " + entry.key + ", Value: " + entry.value);
                }
            }
        }
        return allElements;
    }

    public String printOne(K key) {
        LinkedList<Entry<K, V>> bucket = hashTable[getIndex(key)];
        if (bucket != null) {
            for (Entry<K, V> entry : bucket) {
                if (entry.key.equals(key)) {
                    // Printing out the key and value of the entry
//                    System.out.println("Key: " + entry.key + ", Value: " + entry.value);
                    return "" + entry.value;
                }
            }
            return null;
        }
        return "Entry not found for key: " + key;
    }


    public static class Entry<K, V> {
        K key;
        V value;

        Entry(K key, V value) {
            this.key = key;
            this.value = value;
        }



    }


    public int intFindNextPrime(int num){
        for(int i = 2 ; i < num ; i++){
            if(num % i == 0){
                return intFindNextPrime(num+1);
            }
        }
        return num;
    }



    public String purify(String date, String ticker){
        date = date.replaceAll("-","");
        int intDate = Integer.parseInt(date);
        int numRepOfTick = 0;
        for (int i = 0; i < ticker.length(); i++) {
            numRepOfTick += (int) ticker.charAt(i);
        }
        int totalKey = intDate + numRepOfTick;
        String totalKeyStr = Integer.toString(totalKey);
        return totalKeyStr;


    }







}
