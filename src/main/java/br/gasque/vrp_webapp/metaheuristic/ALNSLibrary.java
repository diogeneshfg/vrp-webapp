package br.gasque.vrp_webapp.metaheuristic;

import java.util.Arrays;
import java.util.List;

import com.sun.jna.Library;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;

public interface ALNSLibrary extends Library{
	
	void setNumberOfRequest(int number);
	
	void setNumberOfClients(int number);
	
	void setCurstomersDistances(Pointer[] distances);
	
	void setTimeTravel(Pointer[] timeTravel);

	void setServiceTime(Pointer serviceTime);

	void setWindowOpen(Pointer windowOpen);

	void setWindowClose(Pointer windowClose);

	void setVehicles(int numberOfVehicles, 
			Pointer vehicleCodes,
			Pointer vehiclesCapacity, 
			Pointer vehiclesFixedCost, 
			Pointer vehiclesVariableCost);
	
	void setMultipleProducts(int numberOfClients, int numberOfProducts, 
			Pointer[] productsDemands);
	
	SolutionForJava.ByValue runAlns(int maxTime, int maxIterations);
	
	
	public class RouteForJava extends Structure{
		
		public static class ByReference extends RouteForJava implements Structure.ByReference{}
		
		public int vehicleCode;
		public Pointer route; //1-D array
		public int numberOfClientsInRoute;
		public Pointer loadAtVisit; //2-D array
		public Pointer timeAtVisit; //1-D array
		public Pointer splitedLoadProductsAtVisit; //2-D array

		@Override
		protected List<String> getFieldOrder() {
			return Arrays.asList("vehicleCode",
					"route",
					"numberOfClientsInRoute",
					"loadAtVisit",
					"timeAtVisit",
					"splitedLoadProductsAtVisit");			
		}
	}

	public class SolutionForJava extends Structure{

		public static class ByValue extends SolutionForJava implements Structure.ByValue {}
		
		public int numberOfRoutes;
		public RouteForJava.ByReference routes;
		public double totalCost;
		public double timeToSolution;
		public double totalDistance;
		
		
		@Override
		protected List<String> getFieldOrder() {			
			return Arrays.asList("numberOfRoutes",
					"routes",
					"totalCost",
					"timeToSolution",
					"totalDistance");
		}
		
	}
	
}
