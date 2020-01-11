package br.gasque.vrp_webapp.bean;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.http.impl.conn.tsccm.RouteSpecificPool;

import com.sun.jna.Pointer;

import br.gasque.vrp_webapp.dao.ClientDAO;
import br.gasque.vrp_webapp.dao.DAOException;
import br.gasque.vrp_webapp.dao.ProductDAO;
import br.gasque.vrp_webapp.dao.VehicleDAO;
import br.gasque.vrp_webapp.entity.Client;
import br.gasque.vrp_webapp.entity.ClientRoute;
import br.gasque.vrp_webapp.entity.Demand;
import br.gasque.vrp_webapp.entity.DemandMet;
import br.gasque.vrp_webapp.entity.Product;
import br.gasque.vrp_webapp.entity.Route;
import br.gasque.vrp_webapp.entity.RoutingPlan;
import br.gasque.vrp_webapp.entity.ServiceTime;
import br.gasque.vrp_webapp.entity.UnitMeasure;
import br.gasque.vrp_webapp.entity.Vehicle;
import br.gasque.vrp_webapp.entity.constrains.ClientTime;
import br.gasque.vrp_webapp.entity.constrains.Pairing;
import br.gasque.vrp_webapp.metaheuristic.ALNSLibrary;
import br.gasque.vrp_webapp.metaheuristic.ALNSLibrary.SolutionForJava;
import br.gasque.vrp_webapp.metaheuristic.PDPTWAlns;
import br.gasque.vrp_webapp.util.OSRMHandler;

/**
 * 
 * @author Diógenes H. Frazzato Gasque
 *
 */
public class PDPTWBean implements Serializable{

	private static final long serialVersionUID = -8386635830441548649L;
	
	private ClientDAO cliDao;	
	private VehicleDAO vehDao;
	private ProductDAO prdDao;
	
	private Client initialDepot;
	private Client finalDepot;
	
	private List<Pairing> requests;
	private List<Vehicle> vehicles;
	private UnitMeasure vehiclesCapacityUnit;
	
	private List<Product> products;
	private int numberOfProducts;
	
	private OSRMHandler osrm;
	
	private PDPTWAlns alns;
	
	
	public PDPTWBean() {
		cliDao = new ClientDAO();
		vehDao = new VehicleDAO();
		prdDao = new ProductDAO();
		osrm = new OSRMHandler();
		
		requests = new ArrayList<>();
		vehicles = new ArrayList<>();
		products = new ArrayList<>();
		numberOfProducts = 0;
	}
	
	
	/**
	 * 
	 * @param pickup
	 * @param pair
	 * @return
	 * @throws VRPException
	 */
	public Pairing addPickupClientToRequest(Client pickup, Pairing pair)throws VRPException {
		if((pair.getDelivery()!=null) && (pair.getDelivery().equals(pickup))) {
			throw new VRPException("Erro ao tentar inserir cliente de colete: Cliente de coleta igual ao de entrega: "
					+pickup.getName()+" , "+pair.getDelivery().getName());
		}else {
			pair.setPickup(pickup);
			return pair;
		}
	}
	
	/**
	 * 
	 * @param delivery
	 * @param pair
	 * @return
	 * @throws VRPException
	 */
	public Pairing addDeliveryClientToRequest(Client delivery, Pairing pair)throws VRPException {
		if((pair.getPickup()!=null) && (pair.getPickup().equals(delivery))) {
			throw new VRPException("Erro ao tentar inserir cliente de colete: Cliente de coleta igual ao de entrega: "
					+delivery.getName()+" , "+pair.getPickup().getName());
		}else {
			pair.setDelivery(delivery);
			return pair;
		}
	}
	
	/**
	 * 
	 * @param prd
	 * @param demands
	 * @return
	 * @throws VRPException
	 */
	public List<Demand> addProductToDemandList(Product prd, List<Demand> demands)throws VRPException{
		if(prd != null) {
			Demand newDemand = new Demand(prd);
			if(!demands.contains(newDemand)) {
				demands.add(newDemand);				
				return demands;
			}else {
				throw new VRPException("Erro tentar adicionar o produto: Produto já possúi uma demand "+prd.getName()+".");
			}
		}else {
			throw new VRPException("Erro ao tentar adicionar o produto: Produto não pode ser nulo.");
		}
	}
	
	/**
	 * 
	 * @param demand
	 * @param demands
	 * @return
	 * @throws VRPException
	 */
	public List<Demand> removeDemandFromDemandList(Demand demand, List<Demand> demands)throws VRPException{
		if(demands.contains(demand)) {
			demands.remove(demand);			
			return demands;
		}else {
			throw new VRPException("Erro ao tentar remover a damanda: Demanda para o produto "
					+demand.getProduct().getName()+", não está na lista.");
		}
	}
	
	/**
	 * 
	 * @param prd
	 * @param demands
	 * @return
	 * @throws VRPException
	 */
	public List<Demand> removeProductFromDemandList(Product prd, List<Demand> demands)throws VRPException{
		Demand dem = demands.stream()
				.filter(d -> prd.equals(d.getProduct()))
				.findAny()
				.orElse(null);
		if(dem!=null) {
			demands.remove(dem);			
			return demands;
		}else {
			throw new VRPException("Erro ao tentar remover a demanda: Demanda para o produto "
					+prd.getName()+", não está na lista.");
		}
	}	
	
	/**
	 * 
	 * @param pair
	 * @param demands
	 * @return
	 * @throws VRPException
	 */
	public Pairing addDemandToPairingClients(Pairing pair, List<Demand> demands)throws VRPException {
		if((pair.getPickup()!=null) && (pair.getDelivery()!=null)) {
			if((demands!=null) && (!demands.isEmpty())) {
				pair.getPickup().setDemands(demands);
				
				List<Demand> deliveryDemands = new ArrayList<>(demands);
				
				deliveryDemands.forEach(dem -> dem.setQuantity(-1*dem.getQuantity()));
				pair.getDelivery().setDemands(deliveryDemands);
				
				return pair;
			}else {
				throw new VRPException("Erro o tentar adicionar demandas: A lista de demanda por produtos não pode estar vazia.");
			}
		}else {
			throw new VRPException("Erro ao tentar adicionar demandas: Clientes de coleta e entrega não podem ser nulos.");
		}
	}
	
	/**
	 * 
	 * @param pair
	 * @throws VRPException
	 */
	public void addPairToRequestList(Pairing pair) throws VRPException {
		if(!requests.contains(pair)) {
			requests.add(pair);
		}else {
			throw new VRPException("Erro ao tentar adicionar a requisição: Requisição de coleta e entrega já existe; "
					+ "Cliente "+pair.getPickup().getName()+" entrega no Cliente "+pair.getDelivery().getName()+".");
		}
	}
	
	/**
	 * 
	 * @param pair
	 * @throws VRPException
	 */
	public void removePairFromRequestList(Pairing pair)throws VRPException{
		if(requests.contains(pair)) {
			requests.remove(pair);
		}else {
			throw new VRPException("Erro ao tentar remover a requisição: Requisição de coleta e entrega "
					+ "não está na lista de requisições do problem; "
					+ "Cliente "+pair.getPickup().getName()+" entrega no Cliente "+pair.getDelivery().getName()+".");
		}
	}
	
	/**
	 * Add a product demand to a client
	 * @param cli
	 * @param demand
	 * @return client
	 * @throws VRPException
	 */
	public Client addClientDemand(Client cli, Demand demand) throws VRPException {
		
		if(!cli.getDemands().contains(demand)) {
			cli.getDemands().add(demand);
		}else {
			throw new VRPException("Erro ao adicionar demanda: Demanda para o produto "
					+demand.getProduct().getName()+", já inserida.");
		}		
		return cli;
	}
	
	/**
	 * Remove a product demand from a client
	 * @param cli
	 * @param demand
	 * @return client
	 * @throws VRPException
	 */
	public Client removeClientDemand(Client cli, Demand demand)throws VRPException{
		if(cli.getDemands().contains(demand)) {
			cli.getDemands().remove(demand);
		}else {
			throw new VRPException("Erro tentar remover demand: Demanda já removida.");
		}
		return cli;
	}	
	
	/**
	 * 
	 * @param veh
	 * @throws VRPException
	 */
	public void addVehicle(Vehicle veh) throws VRPException {
		if(!vehicles.contains(veh)) {
			vehicles.add(veh);
		}else {
			throw new VRPException("Erro ao tentar adicionar veículo: Veículo já adicionado.");
		}
	}
	
	/**
	 * 
	 * @param veh
	 * @throws VRPException
	 */
	public void removeVehicle(Vehicle veh)throws VRPException {
		if(!vehicles.remove(veh)) {
			throw new VRPException("Erro ao tentar remover veículo: Veículo não removido.");
		}
	}
	
	public void addProductToProblem(Product prd) {
		if(!products.contains(prd)) {
			products.add(prd);
			numberOfProducts +=1;
		}
	}
	
	public void removeProductFromProblem(Product prd) {
		if(products.contains(prd)) {
			products.remove(prd);
			numberOfProducts -=1;
		}
	}
	
	/**
	 * Convert requests, which are pairs of clients, into a list of clients where pickup clients are represented by
	 * indexes 1,..,n, been n the number of requests of the problem, and delivery clients are represented by indexes going from
	 * n+1,..., 2n.
	 * It's necessary that initial and final depot must be provided. The index representing initial and final depot are 0 and 2n+1.
	 * The final client list will contain 2n+2 elements.
	 * @param initialDepot
	 * @param finalDepot
	 * @return list of all clients included initial and final depot.
	 */
	public List<Client> requestsToClients(Client initialDepot, Client finalDepot){
		List<Client> clients = new ArrayList<>();
		List<Client> dClients = new ArrayList<>();	
		int n = requests.size();
		for(int j=0; j<requests.size(); ++j) {
			Pairing p = requests.get(j);
			clients.add(p.getPickup());
			dClients.add(p.getDelivery());
		}
		clients.addAll(n, dClients);
		clients.add(0, initialDepot);
		clients.add((2*n+1), finalDepot);
		for(int j=0; j<clients.size(); ++j) {
			System.out.println(clients.get(j).getName());
		}
		
		return clients;
	}
	
	/**
	 * Get clients distance matrix
	 * @return 2-D double[][] matrix.
	 */
	public double[][] getClientsDistance() {				
		return osrm.getDistances();	
	}
	
	/**
	 * Get clients time travel matrix
	 * @return 2-D double[][] matrix.
	 */
	public double[][] getClientsTimeTravel(){
		
		return osrm.getTimeTravel();
	}
	
	public void runOSRM(List<Client> clients) {
		osrm.fillDistanceAndTimeTravelMatrix(clients, OSRMHandler.URL);
	}
	
	/**
	 * This method converts open and close time windows and service time from hours to minutes.
	 * The minutes are calculated considering the initial depot window open time as zero (0). Example, if a client
	 * has time window open and close at 8:20 am and 9:00 am respectively, and initial depot time window open starts 
	 * at 7:00 am than, the converted client time windows will be 80 minutes and 120 minutes.
	 * @param clients list of clients including initial and final depot at 0 and 2n+1 indexes.
	 * @return 2-D arrays of doubles. For each client from 0,...2n+1 we have 3 positions from 0,..2. 
	 * Position 0 keeps time window open, position 1 keeps time window close and last position 2 keeps service time.
	 * In other words we have a matrix that is 2n+1 x 3 (double[2n+1][3]). First dimension is for clients and second 
	 * dimension for window open, close and service time.
	 */
	public double[][] convertTimeWindows(List<Client> clients) {
		double[][] timeWindows = new double[clients.size()][3];
		try {
			System.out.println("Time Windows "+clients.size());
			ClientTime ct = clients.get(0).getTimeWindows().get(0);		
			LocalTime initialTime = LocalTime.parse(ct.getTimeWindowOpen());
			timeWindows[0][0] = 0d;
			timeWindows[0][1] = 0d;
			timeWindows[0][2] = ChronoUnit.MINUTES.between(
					LocalTime.parse("00:00"),  
					LocalTime.parse(ct.getServiceDuration()));
			
			for(int i=1; i<clients.size(); ++i) {
				ct = clients.get(i).getTimeWindows().get(0);	
				
				timeWindows[i][0] = ChronoUnit.MINUTES.between(initialTime, LocalTime.parse(ct.getTimeWindowOpen()));
				timeWindows[i][1] = ChronoUnit.MINUTES.between(initialTime, LocalTime.parse(ct.getTimeWindowClose()));
				
				timeWindows[i][2] = ChronoUnit.MINUTES.between(
						LocalTime.parse("00:00"), 
						LocalTime.parse(ct.getServiceDuration()));
				
				//System.out.println(ct.getClient().getName()+" WO = "+timeWindows[i][0]);
				//System.out.println(ct.getClient().getName()+" WC = "+timeWindows[i][1]);
				//System.out.println(ct.getClient().getName()+" ST = "+timeWindows[i][2]);
			}		
		}catch(Exception e) {
			e.printStackTrace();
		}		
		return timeWindows;
	}
	
	public double[][] convertProducts(List<Product> products, List<Client> clients, UnitMeasure vehiclesUnitMeasure){		
		double[][] prds = new double[clients.size()][products.size()];
		try {
			for(int i=0; i<clients.size(); ++i) {
				System.out.println("demand "+clients.get(i).getDemands().size());
				for(Demand d : clients.get(i).getDemands()) {				
					int index = products.indexOf(d.getProduct());
					if(index >= 0) {
					
						BigDecimal qtd = d.getUnitMeasure().convertTo(
								vehiclesUnitMeasure, 
								BigDecimal.valueOf(d.getQuantity()));
					
						/*System.out.println("Unidade prd = "+d.getUnitMeasure().name()
								+" convertida para "+vehiclesUnitMeasure.name()
								+" - "+d.getQuantity()+" para "+qtd);*/
						
						prds[i][index] = qtd.doubleValue();
					}
				}
			}
			
			/*for(int i=0; i<clients.size(); ++i) {
				for(int p=0; p<products.size(); ++p) {
					System.out.println("Demanda proutos: cliente "+i+" ; produto "+p+" = "+prds[i][p]);
				}
			}*/
		}catch (Exception e) {
			e.printStackTrace();
		}
		return prds;
	}
	
	private void addDemandToDepots() {
		for(int p=0; p<products.size(); ++p) {
			Product prd = products.get(p);
			try {	
				
				initialDepot = addClientDemand(initialDepot, new Demand(
						initialDepot, prd, 
						Double.valueOf(0d), 
						UnitMeasure.kg));
				
				finalDepot = addClientDemand(finalDepot, new Demand(
						finalDepot, prd, 
						Double.valueOf(0d), 
						UnitMeasure.kg));
				
			} catch (VRPException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Add vehicle to ALNS
	 * @param vehicles
	 */
	private void setVehiclesToAlns(List<Vehicle> vehicles) {
		
		int fleetSize = vehicles.size();
		int[] ids = new int[fleetSize];
		double[] capacities = new double[fleetSize];
		double[] fixedCost = new double[fleetSize];
		double[] variableCost = new double[fleetSize];
		
		for(int i=0; i<fleetSize; ++i) {
			Vehicle veh = vehicles.get(i);
			if(veh.getCapacityUnit().equals(vehiclesCapacityUnit)) {
				capacities[i] = veh.getCapacity();
			}else {
				capacities[i] = veh.getCapacityUnit().convertTo(
						vehiclesCapacityUnit, BigDecimal.valueOf(veh.getCapacity())).intValue();
			}
			ids[i] = veh.getId();
			fixedCost[i] = veh.getFixedCost().doubleValue();
			variableCost[i] = veh.getVariableCost().doubleValue();			
		}
		
		alns.setVehicleFleet(fleetSize, ids, capacities, fixedCost, variableCost);
	}
	
	/**
	 * Add products to ALNS including their demand for each client
	 * @param products
	 * @param clients
	 * @param vehiclesCapacityUnit
	 */
	private void setProductsToAlns(List<Product> products, List<Client> clients, UnitMeasure vehiclesCapacityUnit) {
		
		alns.setProductsDemand(products.size(), clients.size(), convertProducts(products, clients, vehiclesCapacityUnit));
	}
	
	/**
	 * Set clients distances, number of clients and number of requests to ALNS
	 * @param clients
	 */
	private void setClientsToAlns(List<Client> clients, int numberOfRequests) {		
		runOSRM(clients);
		alns.setNumberOfClients(clients.size());
		alns.setRequests(numberOfRequests);
		alns.setDistances(getClientsDistance());
		alns.setTimeTravel(getClientsTimeTravel());
	}
	
	/**
	 * Set client's window open, close and service time to ALNS.
	 * @param clients
	 */
	private void setTimeWindowsToAlns(List<Client> clients) {
		int numberOfClients = clients.size();
		double[][] timeWindows = convertTimeWindows(clients);
		
		double[] windowOpen = new double[numberOfClients];
		double[] windowClose = new double[numberOfClients];
		double[] serviceTime = new double[numberOfClients];
		
		for(int i=0; i<numberOfClients; ++i) {
			windowOpen[i] = timeWindows[i][0];
			windowClose[i] = timeWindows[i][1];
			serviceTime[i] = timeWindows[i][2];
		}
		
		alns.setTimeWindows(numberOfClients, windowOpen, windowClose, serviceTime);
	}
	
	/**
	 * 
	 * @param solution
	 * @param clients
	 * @param vehicles
	 * @return
	 */
	public List<Route> getRoutes(SolutionForJava solution, List<Client> clients, List<Vehicle> vehicles) {
		List<Route> routes = new ArrayList<>();
		ALNSLibrary.RouteForJava[] routeData = (ALNSLibrary.RouteForJava[])solution.routes.toArray(solution.numberOfRoutes);
		
		for(int i=0; i<solution.numberOfRoutes; ++i) {
			Route route = new Route();
			ALNSLibrary.RouteForJava r = routeData[i];
			route.setVehicle(vehicles.stream()
					.filter(vehicle -> r.vehicleCode==vehicle.getId())
					.findFirst()
					.orElse(null));			
			
			int[] path = r.route.getIntArray(0, r.numberOfClientsInRoute);
			double[] time = r.timeAtVisit.getDoubleArray(0, r.numberOfClientsInRoute);
			Pointer[] loadData = r.loadAtVisit.getPointerArray(0);
			
			for(int p=0; p<r.numberOfClientsInRoute; ++p) {	
				Client client = clients.get(path[p]);					
				ClientRoute clientRoute = new ClientRoute(route, client, p, new ServiceTime(time[p]));
				
				double[] load = loadData[p].getDoubleArray(0, numberOfProducts);
				List<DemandMet> demands = new ArrayList<>();
				
				for(int l=0; l<numberOfProducts; ++l) {
					demands.add(new DemandMet(clientRoute, 
							clientRoute.getClient().getDemands().get(l), 
							load[l]));
				}
				clientRoute.setDemands(demands);
				route.getClients().add(clientRoute);				
			}
			routes.add(route);
		}
		return routes;
	}
	
	public String getRoutePath(Route route) {
		return osrm.getRoutePath(route);
	}
	
	/**
	 * Return the sequence of visits to clients within a route.
	 * @param route
	 * @return A String representing the visits.
	 * @throws VRPException throws exception if the route's clients list is null or empty.
	 */
	public String getRouteSequenceOfVisits(Route route) throws VRPException {
		StringBuilder sb = new StringBuilder();
		if((route.getClients() != null) && (!route.getClients().isEmpty())) {
			Iterator<ClientRoute> it = route.getClients().iterator();
			ClientRoute cr = null;
			while(it.hasNext()) {
				cr = it.next();
				sb.append(cr.getClient().getName());
				if(it.hasNext()) {
					sb.append(" -> ");
				}			
			}
			return sb.toString();
		}else {
			throw new VRPException("Erro ao tentar gerar sequencia de visitas: A lista de clientes da rota esta vazía.");
		}
	}
	
	/**
	 * Returns a List of Strings where each String element represents the route's visits sequence that belongs to a routing plan.
	 * @param plan Routing plan.
	 * @return List of Strings.
	 * @throws VRPException throws exception if the routing plan is null or the routes and the clients list are null/empty.
	 */
	public List<String> getAllRoutesSequenceOfVisits(RoutingPlan plan) throws VRPException{
		if(plan != null) {
			if((plan.getRoutes() != null) && (!plan.getRoutes().isEmpty())) {
				List<String> routesVisits = new ArrayList<>();
				for(Route r : plan.getRoutes()) {
					routesVisits.add(getRouteSequenceOfVisits(r));
				}
				return routesVisits;
			}else {
				throw new VRPException("Erro ao tentar gerar sequencia de visitas: A lista de rotas está vazía.");
			}
		}else {
			throw new VRPException("Erro ao tentar gerar sequencia de visitas: O plano de rotas vazio.");
		}
	}
	
	
	/**
	 * Run the Blessed by God ALNS!!
	 */
	public RoutingPlan runALNS() {		
		/*RoutingPlan plan = new RoutingPlan();
		this.osrm = new OSRMHandler();
		this.alns = new PDPTWAlns();
		this.alns.init();
		
		List<Client> clients = requestsToClients(initialDepot, finalDepot);
		setVehiclesToAlns(this.vehicles);			
		setProductsToAlns(this.products, clients, vehiclesCapacityUnit);
		setClientsToAlns(clients, requests.size());
		setTimeWindowsToAlns(clients);
		
		List<Route> routes = getRoutes(alns.run(600, 25000), clients, this.vehicles);
		plan.setRoutes(routes);
		
		return plan;	*/
		return teste();
	}
	
	public Client findClientById(Client cli)throws DAOException{
		return cliDao.findClientById(cli);
	}
	
	public List<Client> getClientsByAdress(Client cli) throws DAOException{
		return cliDao.getClientsByAddress(cli);
	}
	
	public List<Client> getClientsByName(Client cli) throws DAOException{
		return cliDao.getClientsByName(cli);
	}
	
	public List<Client> getAllClients()throws DAOException{
		return cliDao.getAllClients();
	}
	
	public Product findProductById(Product prd)throws DAOException {
		return prdDao.findProductbyId(prd);
	}
	
	public List<Product> getProductsByName(Product prd) throws DAOException{
		return prdDao.getProductsByName(prd);
	}
	
	public List<Product> getAllProducts() throws DAOException{
		return prdDao.getAllProducts();
	}
	
	public Vehicle findVehicleById(Vehicle veh)throws DAOException{
		return vehDao.findVehicleById(veh);
	}
	
	public List<Vehicle> getVehiclesByDescrption(Vehicle veh) throws DAOException{
		return vehDao.getVehiclesByDescription(veh);
	}
	
	public List<Vehicle> getVehiclesByCapacity(Vehicle veh) throws DAOException{
		return vehDao.getVehiclesByCapacity(veh);
	}
	
	public List<Vehicle> getAllVehicles()throws DAOException{
		return vehDao.getAllVehicles();
	}
	
	public List<Pairing> getRequests(){
		return requests;
	}
	
	public List<Vehicle> getVehicles(){
		return vehicles;
	}


	public Client getInitialDepot() {
		return initialDepot;
	}


	public void setInitialDepot(Client initialDepot) {
		this.initialDepot = initialDepot;
	}


	public Client getFinalDepot() {
		return finalDepot;
	}


	public void setFinalDepot(Client finalDepot) {
		this.finalDepot = finalDepot;
	}
	
	/**
	 * Método para testes
	 */
	private RoutingPlan teste() {
		System.out.println("Begening Test");		
		RoutingPlan plan = new RoutingPlan();
		requests = new ArrayList<>();		
		
		Client depot = new Client();
		Client cli = new Client();
		depot.setName("Depot");
		cli.setName("C");
		try {
			
			//Client stuff
			System.out.println("Initialize Clients");
			depot = getClientsByName(depot).get(0);
			depot = cliDao.getClientTimeWindows(depot);			
			List<Client> cls = getClientsByName(cli);
			
			//Vehicle stuff
			System.out.println("Initialize Vehicle");
			this.vehicles = getAllVehicles();			
			vehiclesCapacityUnit = UnitMeasure.kg;
			
			//Product stuff
			System.out.println("Initialize Products");
			List<Product> prds = getAllProducts();				
			this.products = prds;
						
			System.out.println("Initialize Demand");
			depot.setDemands(new ArrayList<>());
			for(int p=0; p<prds.size(); ++p) {
					try {
						depot = addClientDemand(depot, new Demand(
									depot, prds.get(p), 
									Double.valueOf(0), 
									UnitMeasure.kg));
					} catch (VRPException e) {
						e.printStackTrace();
					}
			}			
			System.out.println("Initialize Requests "+cls.size());			
			for(int i=0; i<cls.size(); i+=2) {	
				
				try {
					Client pickup = cls.get(i);
					Client delivery = cls.get(i+1);
				
				
					if(pickup.isTimeWindow()) {
						pickup = cliDao.getClientTimeWindows(pickup);
					}
					if(delivery.isTimeWindow()) {
						delivery = cliDao.getClientTimeWindows(delivery);
					}
					pickup.setDemands(new ArrayList<>());
					delivery.setDemands(new ArrayList<>());				
					
					System.out.println("Adding demand to client "+i);
					//Add demand for products to each client
					for(int p=0; p<prds.size(); ++p) {
						Product prd = prds.get(p);
						try {	
							
							pickup = addClientDemand(pickup, new Demand(
									pickup, prd, 
									Double.valueOf((p+1.0)), 
									UnitMeasure.kg));
													
							delivery = addClientDemand(delivery, new Demand(
									pickup, prd, 
									Double.valueOf(-1.0*(p+1.0)), 
									UnitMeasure.kg));
							
						} catch (VRPException e) {
							e.printStackTrace();
						}
					}
					requests.add(new Pairing(pickup, delivery));	
					System.out.println("Finished Adding demand to client "+i);
				}catch(Exception e) {
					e.printStackTrace();
				}
			}
		} catch (DAOException e) {
			e.printStackTrace();
		}	
		System.out.println("Finished Initialize Requests");
				
		System.out.println("Loading Resources for ALNS");
		this.osrm = new OSRMHandler();		
		this.alns = new PDPTWAlns();
		System.out.println("Initialize ALNS");
		this.alns.init();
		System.out.println("Finished to Initialize ALNS");
		
		numberOfProducts = 3;	
		List<Client> clients = requestsToClients(depot, depot);
		
		try {			
			setVehiclesToAlns(this.vehicles);			
			setProductsToAlns(this.products, clients, vehiclesCapacityUnit);
			setClientsToAlns(clients, requests.size());
			setTimeWindowsToAlns(clients);
			
			System.out.println("RUN ALNS");
			List<Route> routes = getRoutes(alns.run(5, 100), clients, this.vehicles);
			plan.setRoutes(routes);
			for(Route r : routes) {
				System.out.println(r.getVehicle().getId()+" - "+r.getVehicle().getCapacity());
				for(ClientRoute cr : r.getClients()) {
					System.out.println(cr.getClient().getName()+" - "+cr.getPositionInRoute());
					for(DemandMet met : cr.getDemands()) {
						System.out.println(met.getDemand().getProduct().getName()+" - "+met.getQuantityMet());
					}
				}
				System.out.println("**********END ROUTE**********");
			}
			
			
			
		}catch(Exception e){
			e.printStackTrace();
		}
		return plan;
		
	}

}
