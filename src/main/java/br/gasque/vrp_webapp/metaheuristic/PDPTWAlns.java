package br.gasque.vrp_webapp.metaheuristic;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.Pointer;

import br.gasque.vrp_webapp.metaheuristic.ALNSLibrary.SolutionForJava;

/**
 * This class responsibility is only to manage the link between Java and C++ ALNS implementation. 
 * This is why only raw vector and arrays are passed to it's methods.
 * 
 * @author Diógenes H. Frazzato Gasque
 *
 */
public class PDPTWAlns implements Serializable{
	
	private static final long serialVersionUID = -1856152723111281334L;
	
	private ALNSLibrary alnsLib;
	
	public void init() {
		System.out.println("Loading ALNS LIB");
		alnsLib = Native.loadLibrary("ALNS", ALNSLibrary.class);	
		System.out.println("Finished Loading ALNS LIB");
	}
	
	/**
	 * Set the number of pickup and delivery requests. 
	 * The requests are numbers between 1 and n, been n the total number of pickup clients. 
	 * Note that the initial (0) and final depot (2n+1) must not be counted as a request.
	 * 
	 * @param number
	 */
	public void setRequests(int number) {		
		alnsLib.setNumberOfRequest(number);
	}
	
	/**
	 * Set the total number of clients. The number of clients include the initial and final depot.
	 * The initial depot is represented as zero (0) and the final depot is represented as (2n+1).
	 * Assume that n is the total number of requests. 
	 * The pickup clients are represented by 1,..,n and the delivery clients are represented by n+1,...,2n.
	 * Observe that the total number of clients are the sum of pickup clients plus delivery clients 
	 * plus initial depot plus final depot. The total number of clients can be represented by 2n+2 nodes.
	 * 
	 * @param number
	 */
	public void setNumberOfClients(int number) {
		alnsLib.setNumberOfClients(number);
	}
	
	/**
	 * The 2-D double matrix representing all the distances between clients.
	 * @param dist
	 */
	public void setDistances(double[][] dist) {		
		int size = dist.length;
		Pointer[] distances = new Pointer[size];
		
		/*for(int i=0; i<size; ++i) {
			for(int j=0; j<size; ++j) {
				System.out.println(i+" - "+j+" = "+dist[i][j]);
			}
		}*/
		
		for(int i=0; i<size; ++i) {
			distances[i] = new Memory(size * Native.getNativeSize(Double.TYPE));			
			distances[i].write(0, dist[i], 0, size);			
		}
				
		alnsLib.setCurstomersDistances(distances);
	}
	
	/**
	 * 
	 * @param timeTravel
	 */
	public void setTimeTravel(double[][] timeTravel) {		
		int size = timeTravel.length;
		Pointer[] timeTravels = new Pointer[size];
		
		/*for(int i=0; i<size; ++i) {
			for(int j=0; j<size; ++j) {
				System.out.println("Time Travel -> ["+i+"] - ["+j+"] = "+timeTravel[i][j]);
			}
		}*/
		
		for(int i=0; i<size; ++i) {
			timeTravels[i] = new Memory(size * Native.getNativeSize(Double.TYPE));			
			timeTravels[i].write(0, timeTravel[i], 0, size);			
		}
				
		alnsLib.setTimeTravel(timeTravels);
	}
	
		
	/**
	 * Set the open and close time windows and the service time at each client. 
	 * The initial depot as the final depot must have a open, close and service time values. 
	 * If there is no such specifications, the open time window and service time on the initial depot must be zero.
	 * For the final depot the close time window must be set to a large value to no restrict the route tour.
	 * If one desires to restrict the tour duration a specific time must be set to close time window value.
	 * 
	 * @param numberOfClients
	 * @param timeWindowOpen
	 * @param timeWindowClose
	 * @param serviceTime
	 */
	public void setTimeWindows(int numberOfClients, double[] timeWindowOpen, double[] timeWindowClose, double[] serviceTime) {		
		Pointer servTime = new Memory(numberOfClients * Native.getNativeSize(Double.TYPE));
		Pointer winOpen = new Memory(numberOfClients * Native.getNativeSize(Double.TYPE));
		Pointer winClose = new Memory(numberOfClients * Native.getNativeSize(Double.TYPE));
		
		for(int i=0; i<numberOfClients; ++i) {
			servTime.setDouble(i*Native.getNativeSize(Double.TYPE), serviceTime[i]);
			winOpen.setDouble(i*Native.getNativeSize(Double.TYPE), timeWindowOpen[i]);
			winClose.setDouble(i*Native.getNativeSize(Double.TYPE), timeWindowClose[i]);
		}
		
		alnsLib.setServiceTime(servTime);
		alnsLib.setWindowOpen(winOpen);
		alnsLib.setWindowClose(winClose);		
	}
	
	/**
	 * 
	 * @param fleetSize
	 * @param vehiclesCapacities
	 * @param vehiclesFixedCosts
	 * @param vehiclesVaribleCosts
	 */
	public void setVehicleFleet(int fleetSize, 
			int[] vehicleIds,
			double[] vehiclesCapacities, 
			double[] vehiclesFixedCosts,
			double[] vehiclesVaribleCosts) {
		
		Pointer vehIds = new Memory(fleetSize * Native.getNativeSize(Integer.TYPE));
		Pointer vehCap = new Memory(fleetSize * Native.getNativeSize(Double.TYPE));
		Pointer vehFC = new Memory(fleetSize * Native.getNativeSize(Double.TYPE));
		Pointer vehVC = new Memory(fleetSize * Native.getNativeSize(Double.TYPE));
		
		for(int i=0; i<fleetSize; ++i) {
			vehIds.setInt(i*Native.getNativeSize(Integer.TYPE), vehicleIds[i]);
			vehCap.setDouble(i*Native.getNativeSize(Double.TYPE), vehiclesCapacities[i]);
			vehFC.setDouble(i*Native.getNativeSize(Double.TYPE), vehiclesFixedCosts[i]);
			vehVC.setDouble(i*Native.getNativeSize(Double.TYPE), vehiclesVaribleCosts[i]);
		}
		
		alnsLib.setVehicles(fleetSize, vehIds, vehCap, vehFC, vehVC);
	}
	
	/**
	 * Set the product demands for each client. All the demands must be in the same vehicle's unit measure.
	 * @param numberOfProducts
	 * @param clientPrdDemands 2-D array with the products demands. The first dimensional are the clients and 
	 * the second are the product demands. The array must be like this clientPrdDemands[clients][products], clients by products.
	 */
	public void setProductsDemand(int numberOfProducts, int numberOfClients, double[][] clientPrdDemands) {
		Pointer[] prdDemands = new Pointer[numberOfClients];
		
		for(int i=0; i<numberOfClients; ++i) {
			prdDemands[i] = new Memory(numberOfProducts * Native.getNativeSize(Double.TYPE));
			prdDemands[i].write(0, clientPrdDemands[i], 0, numberOfProducts);
		}
		
		alnsLib.setMultipleProducts(numberOfClients ,numberOfProducts, prdDemands);
	}
	
	/**
	 * 
	 * @param maxTime
	 * @param maxDuration
	 * @return
	 */
	public SolutionForJava.ByValue run(int maxTime, int maxDuration) {		
		return alnsLib.runAlns(maxTime, maxDuration);
	}

}
