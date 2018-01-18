//package lab1;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

//import trieTree.trieNode;



class trieTree{
	class trieNode{
		
		private IO.Pair indexSymbol;
		private HashMap<Character, trieNode> nodeChildren;
		private int dictIndex;
		private String nodeString;
		
		trieNode() {
			indexSymbol = new IO.Pair(0, '\u0000');
			nodeChildren = new HashMap<Character, trieNode>();
			dictIndex = 0;
			nodeString = "";
		}
		
		trieNode(int index, Character symbol, int indexDict, String strNode) {
			indexSymbol = new IO.Pair(index, symbol);
			nodeChildren = new HashMap<Character, trieNode>();
			dictIndex = indexDict;
			nodeString = strNode + symbol;
		}
		
		public int getDictIndex() {
			
			return dictIndex;
		}
		
		
	}
	
	private trieNode root;
	private int dictNum;
	
	trieTree() {
		root = new trieNode();
		dictNum = 0;
	}
	
//    public boolean isPresent(String str){
//        return isPresentHelper(root, str, 1);
//    }
//    
//    private boolean isPresentHelper(trieNode currentNode, String str, int index) {
//    	HashMap<String, trieNode> nodeChildren = currentNode.nodeChildren;
//    	boolean result = false;
//    	
//    	if(nodeChildren.containsKey(str))
//    		return true;
//    	
//    	if(nodeChildren.containsKey(str.substring(0, index)))
//    		result = isPresentHelper(nodeChildren.get(str.substring(0, index)), str, index + 1);
//    	
//    	return result;
//    	
//    	if(currentNode == null)
//    		return false;    	
//    	int compare = data.compareTo(currentNode.getValue());
//    	if(compare == 0)
//    		return true;
//    	else if(compare < 0)
//    		return isPresentHelper(data, currentNode.getLeft());
//    	else
//    		return isPresentHelper(data, currentNode.getRight());
    		
//    }
	
	public String rebuildString(int dictionaryEntry) {
		return rebuildStringHelper(root, dictionaryEntry);
	}
	
	private String rebuildStringHelper(trieNode currentNode, int dictionaryEntry) {
	
		// Base Case: dictionaryEntry = currentNode's dictionary index
		if(currentNode.dictIndex == dictionaryEntry)
			return currentNode.nodeString;
		else {
			Iterator<trieNode> itr = currentNode.nodeChildren.values().iterator();
			String temp = null;
			while(itr.hasNext()) {
				temp = rebuildStringHelper(itr.next(), dictionaryEntry);
				if(temp != null)
					break;
			}
			
			return temp;
		}
	}
	
	//(1) from a given string find the (shortest) prefix that is not in the dictionary, 
	public trieNode findString(String str) {
		return findStringHelper(root, str);
	}
	
	private trieNode findStringHelper(trieNode currentNode, String str) {
			//trieNode tempNode = null;
			
			// String is empty if
			if(str.isEmpty())
				return null;
		
			// if first character of string is in hash map
			if(currentNode.nodeChildren.containsKey(str.charAt(0)))
				return findStringHelper(currentNode.nodeChildren.get(str.charAt(0)), str.substring(1));
			
			//return tempNode;
			return currentNode;
		
	}
	
	//(2) add a new entry to the dictionary.
	public boolean addNode(trieNode nodeAdd, int index, char symbol) {
		dictNum++;
		trieNode nNode = new trieNode(index, symbol, dictNum, nodeAdd.nodeString);
		nodeAdd.nodeChildren.put(symbol, nNode);
		return true;
	}
	
	
	
}

public class LZcoding {
	
	public static void main(String[] args) throws Exception{
		
		trieTree trieData = new trieTree();
		trieTree.trieNode temp;
		
		// Compression
		if(args[0].equals("c")) {
			
			
			//ArrayList<String> arrayStrings = new ArrayList<String>();
			//ArrayList<IO.Pair> arrayTuples = new ArrayList<IO.Pair>();
			
			
			IO.Compressor compressor = new IO.Compressor(args[1]);
			char[] charArray = compressor.getCharacters();
			
			int i = 0;
			boolean found = false;
			String input = "";
			//arrayStrings.add(null);
			
			
			//compressor.encode(indexFound, '');
			// Iterate through character Array
			while(i < charArray.length) {
				
				
				input += charArray[i];
				
				// Find if input is in trie
				temp = trieData.findString(input);
				if(temp == null)
					found = true;
				
				// input string isn't already in dictionary
				// encode(int index, char character)
				if(!found && temp != null) {
					
					compressor.encode(temp.getDictIndex(), input.charAt(input.length()-1));
					
					//public boolean addNode(trieNode nodeAdd, int index, char symbol) {
					trieData.addNode(temp, temp.getDictIndex(), input.charAt(input.length() - 1));
					//arrayStrings.add(input);
					input = "";
				}
					
				found = false;
				i++;
			}
			
			
			compressor.finalize();
		}
		
		
		// Decompression
		else if(args[0].equals("d")) {
			
			String str = "";
			String s = "";
			boolean found = false;
			IO.Decompressor io = new IO.Decompressor(args[1]);
			IO.Pair next;
			while((next = io.decode()).isValid()) {
				
				str += next.getCharacter();
				
				// Find if input is in trie
				temp = trieData.findString(str);
				if(temp == null)
					found = true;

				
				if(!found) {
					
					// Need to rebuild string from tree
					
					s = trieData.rebuildString(next.getIndex());
					trieData.addNode(temp, temp.getDictIndex(), str.charAt(str.length() - 1));
					System.out.println("s is: " + s + "\n" + "Str is: " + str);
					io.append(s + str);
					str = "";
					s = "";
				}
				found = false;
				
			}
			
			io.finalize();
		}
		
	}

}