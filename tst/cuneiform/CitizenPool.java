package cuneiform;

import java.util.ArrayList;



//object pool, so we dont constantly create new citizens
//(potentiall 100 new each gen)
public class CitizenPool {

	int maxNum;
	ArrayList<Citizen> usedCitizens;
	ArrayList<Citizen> freeCitizens;
	
	public CitizenPool(int num) {
		maxNum = num;
		usedCitizens = new ArrayList<Citizen>();
		freeCitizens = new ArrayList<Citizen>();
	}
	
	
	public Citizen getCitizen(){
		Citizen newCit = null;
		if (freeCitizens.size() > 0){
			newCit = freeCitizens.get(0);
			usedCitizens.add(freeCitizens.get(0));
			freeCitizens.remove(0);
		}
		else if (usedCitizens.size() < maxNum){
			//create new cit, add to usedCit
			newCit = new Citizen();
			usedCitizens.add(newCit);
		}
		
		return newCit;
		
		
	}
	
	
	public Citizen getCitizen(int IDNo){
		Citizen newCit = null;
		if (freeCitizens.size() > 0){
			newCit = freeCitizens.get(0);
			usedCitizens.add(freeCitizens.get(0));
			freeCitizens.remove(0);
			newCit.IDNo = IDNo;
		}
		else if (usedCitizens.size() < maxNum){
			//create new cit, add to usedCit
			newCit = new Citizen(IDNo);
			usedCitizens.add(newCit);
		}
		
		return newCit;
		
		
	}
	
	
	public void killCitizen(Citizen Cit){
		int index = usedCitizens.indexOf(Cit);
		Citizen foundCit;
		if (index > 0){
			foundCit = usedCitizens.get(index);
			usedCitizens.remove(index);
			
			foundCit.Fitness = 0;
			foundCit.IDNo = 0;
			foundCit.personalMatrix = null;
			
			freeCitizens.add(foundCit);
		}
		
	
	}

}
