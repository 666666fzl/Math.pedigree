package baseModel;
/**
 * 
 */


import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

/**
 * @author zhenchengwang
 */
public class AllelGenerator {

	
	public void generator(int gCount, int pCount, int[] result){
		/* four Individuals, create and give them name */
		List<Individual> four = new ArrayList<Individual>();
		for(int i=0; i<4; i++){
			four.add(new Individual("a"+i));
		}
		
		Map<String, Individual> inMap = new HashMap<String, Individual>();
		Map<String, Individual> outMap = new HashMap<String, Individual>();
		/* initially, we have 4 Individuals in our inMap: */
		for(int i=0; i<4; i++){
			inMap.put(four.get(i).myName, four.get(i));
		}
		
		Set<Integer> checkDup = new HashSet<Integer>();
		int randomCt = 4;
		int myL = 1;
		int myU = pCount;
		int firstCt = 0;
		
		/* level order traverse */	
		for(int i=0; i<gCount; i++){
			
			Iterator it = inMap.entrySet().iterator();			
			while(it.hasNext()){ // iterate through inMap, one level
				int myRand = 0;
				if(i==0){					
					if(firstCt<2) // 0,1
						myRand = getRandom(0, pCount/2);
					else		  // 2,3	
						myRand = getRandom(pCount/2, pCount);
				}
				else{
					myRand = getRandom(myL, myU);
				}
				Map.Entry pairs = (Map.Entry)it.next();				
				

				String myName = (String)pairs.getKey();
				Individual me = (Individual)pairs.getValue();				
				me.myParents.add(myRand);
				// update individual's myParents in order in inMap
				inMap.put(myName, me);
				// put them in a set to check duplicate
				checkDup.add(myRand);
				
				firstCt++;
			}
			
			randomCt = checkDup.size();
			testSame(inMap, outMap); // test same by myParents.end(), update myEqual, put from inMap to outMap
		
		}
		// copy the content of the filled array to its slave array
		//printMap(inMap);		
		//System.out.println(" ");
		//printMap(outMap);
		accumulate(result, inMap, outMap);
		
	}
	
	/* 
	 * */
	public void testSame(Map<String, Individual> inMap, Map<String, Individual> outMap){
		List<Pair<String, Individual>> tmp = new ArrayList<Pair<String, Individual>>();
		Iterator it = inMap.entrySet().iterator();
		while(it.hasNext()){ // use a list to get all items in inMap
			Map.Entry pairs = (Map.Entry)it.next();
			String tmpString = (String)pairs.getKey();
			Individual tmpIndi = (Individual)pairs.getValue();
			Pair<String, Individual> tmpPair = new Pair(tmpString, tmpIndi);
			tmp.add(tmpPair);
		}
		//printList(tmp);
		for(int i=0; i<tmp.size(); i++){  // check same in inMap, (i,j) (j,i)...problem
			for(int j=0; j<tmp.size(); j++){
				if(i>j){ // (i, j) (j, i) problem solved
				String tmpIndiI = tmp.get(i).getFirst(); //System.out.println("tmpIndiI:"+tmpIndiI);
				List<Integer> tmpParentsI = tmp.get(i).getSecond().myParents;
				String tmpIndiJ = tmp.get(j).getFirst(); //System.out.println("tmpIndiJ:"+tmpIndiJ);
				List<Integer> tmpParentsJ = tmp.get(j).getSecond().myParents;
				
				int first = tmpParentsI.get(tmpParentsI.size()-1);
				int second = tmpParentsJ.get(tmpParentsJ.size()-1);	
				
				// check if i, j still in inMap or is removed:
				boolean IinMap = inMap.containsKey(tmpIndiI);
				boolean JinMap = inMap.containsKey(tmpIndiJ);
				if(first == second && IinMap && JinMap){
					//System.out.println("first:" + first);
					//System.out.println("second:"+second);
					// set second point to first and put second to outMap
					Individual tmpI = inMap.get(tmpIndiI);
					Individual tmpJ = inMap.get(tmpIndiJ);		
					tmpI.myEqual = tmpJ.myName; //System.out.println(i+":"+j);
					outMap.put(tmpI.myName,tmpI);
					inMap.remove(tmpI.myName);
				}
				} // end if
			}
		}
	}
	
	/* Generate a random integer from [lower, upper] */
	public int getRandom(int lower, int upper){
		Random rand = new Random();
		int randomNumb = rand.nextInt(upper-lower+1)+lower;
		return randomNumb;
	}
	
	/* Print a list, for debug purpose */
	public void printList(List<Pair<String, Individual>> toPrint){
		for(int i=0; i<toPrint.size(); i++){
			System.out.println(toPrint.get(i).getFirst());
		}
	}
	
	/* Print a map, for debug purpose */
	public static void printMap(Map<String, Individual> toPrint){
		if(toPrint.size() == 0){
			return;
		}
		Iterator it = toPrint.entrySet().iterator();			
		while(it.hasNext()){ // iterate through inMap, one level
			Map.Entry pairs = (Map.Entry)it.next(); // pair: getKey, getValue
			String key = (String)pairs.getKey();
			Individual me = (Individual)pairs.getValue();
			for(int i=0; i<me.myParents.size(); i++){
				System.out.println(me.myParents.get(i));
			}
			System.out.println(me.myEqual);
			System.out.println(key+"//"); 
		}
		
	}
	
	/* Print parents of Individual */
	public void printIndividual(Individual me){
		for(int i=0; i<me.myParents.size(); i++){
			System.out.println(me.myParents.get(i));
		}
	}
	
	/* Print the probability */
	public static void accumulate(int[]IBDCount, Map<String, Individual> inMap, Map<String, Individual> outMap){
		//System.out.println("in:"+inMap.size());
		//System.out.println("out:"+outMap.size());
		// all different: 1
		if(inMap.size() == 4){
			IBDCount[0]++;
		}
		// all same: 1
		if(inMap.size() == 1){
			IBDCount[14]++;
		}
		printMap(inMap);
		System.out.println("in/out");
		printMap(outMap);
		
		// three different: 6
		if(inMap.size() == 3 && outMap.containsKey("a2") && outMap.get("a2").myEqual.equals("a0")){ // know the order element get pushed
			IBDCount[1]++;
		}
		if(inMap.size() == 3 && outMap.containsKey("a3") && outMap.get("a3").myEqual.equals("a1")){
			IBDCount[2]++;
		}
		if(inMap.size() == 3 && outMap.containsKey("a1") && outMap.get("a1").myEqual.equals("a0")){
			IBDCount[3]++;
		}
		if(inMap.size() == 3 && outMap.containsKey("a3") && outMap.get("a3").myEqual.equals("a2")){
			IBDCount[4]++;
		}
		if(inMap.size() == 3 && outMap.containsKey("a3") && outMap.get("a3").myEqual.equals("a0")){
			IBDCount[5]++;
		}
		if(inMap.size() == 3 && outMap.containsKey("a2") && outMap.get("a2").myEqual.equals("a1")){
			IBDCount[6]++;
		}
		// two different: 7
			// triangles:
		if(inMap.size() == 2 &&outMap.containsKey("a1") &&outMap.containsKey("a3") && outMap.get("a1").myEqual.equals("a0") && outMap.get("a3").myEqual.equals("a0")){
			IBDCount[7]++;
		}
		if(inMap.size() == 2 &&outMap.containsKey("a1") &&outMap.containsKey("a2") && outMap.get("a1").myEqual.equals("a0") && outMap.get("a2").myEqual.equals("a0")){
			IBDCount[8]++;
		}
		if(inMap.size() == 2 &&outMap.containsKey("a2") &&outMap.containsKey("a3") && outMap.get("a2").myEqual.equals("a0") && outMap.get("a3").myEqual.equals("a0")){
			IBDCount[9]++;
		}
		if(inMap.size() == 2 &&outMap.containsKey("a2") &&outMap.containsKey("a3") && outMap.get("a2").myEqual.equals("a1") && outMap.get("a3").myEqual.equals("a1")){
			IBDCount[10]++;
		}
			// two lines:
		if(inMap.size() == 2 &&outMap.containsKey("a1") &&outMap.containsKey("a3") && outMap.get("a1").myEqual.equals("a0") && outMap.get("a3").myEqual.equals("a2")){
			IBDCount[11]++;
		}
		if(inMap.size() == 2 &&outMap.containsKey("a2") &&outMap.containsKey("a3") && outMap.get("a2").myEqual.equals("a0") && outMap.get("a3").myEqual.equals("a1")){
			IBDCount[12]++;
		}
		if(inMap.size() == 2 &&outMap.containsKey("a2") &&outMap.containsKey("a3") && outMap.get("a2").myEqual.equals("a1") && outMap.get("a3").myEqual.equals("a0")){
			IBDCount[13]++;
		}
		
		
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		AllelGenerator test = new AllelGenerator();
		int[] IBDCount = new int[15];
		//int total = 0;
		int sum = 0;
		for(int i=0; i<15; i++){
			IBDCount[i] = 0;
		}
		for(int i=0; i<1; i++){
			test.generator(10, 40, IBDCount);
		}	
		// print out the result:
		for(int i : IBDCount)
			sum += i;
		System.out.println(sum);
		for(int i=0; i<15; i++){
			System.out.println( ((double)IBDCount[i])/sum);
		}
	}

}
