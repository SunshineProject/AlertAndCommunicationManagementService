package it.sinergis.sunshine.suggestion.rest;

import it.sinergis.sunshine.suggestion.Responseobject.ObjCalendarIdList;
import it.sinergis.sunshine.suggestion.Responseobject.ObjComfortArray;
import it.sinergis.sunshine.suggestion.Responseobject.ObjJsonComfortProfile;
import it.sinergis.sunshine.suggestion.Responseobject.ObjShelter;
import it.sinergis.sunshine.suggestion.Responseobject.ObjThresholdList;
import it.sinergis.sunshine.suggestion.Responseobject.Objbuilding;
import it.sinergis.sunshine.suggestion.Responseobject.ObjbuildingList;
import it.sinergis.sunshine.suggestion.delegate.Delegatedatabasemap4data;
import it.sinergis.sunshine.suggestion.delegate.DelegateoperationSuggestion;
import it.sinergis.sunshine.suggestion.map4datapojo.BuildingMap4;
import it.sinergis.sunshine.suggestion.map4datapojo.Calendar;
import it.sinergis.sunshine.suggestion.map4datapojo.Calendarshelter;
import it.sinergis.sunshine.suggestion.pojo.JsonObject;
import it.sinergis.sunshine.suggestion.suggestion.Principale;
import it.sinergis.sunshine.suggestion.timertask.ScheduledTask;
import it.sinergis.sunshine.suggestion.utils.Functions;
import it.sinergis.sunshine.suggestion.utils.ReadFromConfig;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jettison.json.JSONException;

import com.google.gson.Gson;

@Path(value = "v1")
public class Suggestioninterface {
	static final Logger LOGGER = Logger.getLogger(Suggestioninterface.class);
	
	@GET
	@Path(value = "data")
	@Produces({ MediaType.APPLICATION_JSON })
	public JsonObject getDataSuggestion(@QueryParam("folderpath") String folderpath, @QueryParam("pilot") String pilot,
			@QueryParam("buildingid") String buildingid) {
		LOGGER.info("In getDataSuggestion");
		Principale p = new Principale();
		JsonObject JO = null;
		if (folderpath == null || folderpath == "") {
			JO = p.calculate(null, -1, null, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0);
		}
		else {
			JO = p.calculate(folderpath, Integer.parseInt(buildingid), pilot, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0);
		}
		
		return JO;
	}
	
	@GET
	@Path(value = "css/{CODESPACE}/profiles/")
	@Produces({ MediaType.APPLICATION_JSON })
	public Response getProfileFromProfileid(@QueryParam("buildingid") String buildingid,
			@QueryParam("foiid") String foiid, @PathParam("CODESPACE") String codespace) throws NumberFormatException,
			ParseException {
		LOGGER.info("In GET profile");
		String pilot = ReadFromConfig.loadByName(codespace);
		Long foiidLong;
		List<Calendar> profiles = null;
		long buildid = -1;
		if (pilot == null) {
			return Response.status(400).header("Error", "No codespace found").build();
		}
		Delegatedatabasemap4data dbmap4data = new Delegatedatabasemap4data();
		try {
			foiidLong = Long.parseLong(foiid);
		}
		catch (Exception e) {
			foiidLong = null;
		}
		if (foiidLong != null) {
			BuildingMap4 bb = dbmap4data.readParameterBuilding(foiidLong, null, null, pilot);
			if (bb != null) {
				buildid = bb.getGid();
				profiles = dbmap4data.readParameterCalendar(buildid, pilot, null, null);
			}
			
		}
		else {
			profiles = dbmap4data.readParameterCalendar(Long.parseLong(buildingid), pilot, null, null);
		}
		List<Objbuilding> listObjBuilding = new ArrayList<Objbuilding>();
		ObjbuildingList buildings = new ObjbuildingList();
		DateFormat outputFormatter = new SimpleDateFormat("yyyy-MM-dd");
		if (profiles != null) {
			for (Calendar profile : profiles) {
				Objbuilding objBuildingProfile = new Objbuilding();
				objBuildingProfile.setId(profile.getCalendarid());
				objBuildingProfile.setBuildingid(profile.getBuildingid());
				try {
					objBuildingProfile.setComfortProfile(profile.getProfilecomfort());
				}
				catch (Exception e) {
					LOGGER.warn("No profile fro " + profile.getCalendarid());
					objBuildingProfile.setComfortProfile("");
				}
				objBuildingProfile.setDay(outputFormatter.format(profile.getDay()));
				objBuildingProfile.setPilot(profile.getPilot());
				listObjBuilding.add(objBuildingProfile);
			}
		}
		
		buildings.setBuildings(listObjBuilding);
		if (listObjBuilding.isEmpty()) {
			Objbuilding defaultElem = new Objbuilding();
			if (buildid != -1) {
				defaultElem.setBuildingid(buildid);
				//				defaultElem.setComfortProfile("{T:00,INT:[{from_hh:00, from_mm:00, to_hh:00, to_mm:00}]}");
				//				defaultElem.setDay("0000-00-00");
			}
			else {
				defaultElem.setBuildingid(Long.parseLong(buildingid));
				//				defaultElem.setComfortProfile("{T:00,INT:[{from_hh:00, from_mm:00, to_hh:00, to_mm:00}]}");
				//				defaultElem.setDay("0000-00-00");
			}
			listObjBuilding.add(defaultElem);
			//listObjBuilding.add(null);
			//buildings.setBuildings(listObjBuilding);
			return Response.ok(buildings).build();
		}
		return Response.ok(buildings).build();
	}
	
	@GET
	@Path(value = "css/{CODESPACE}/profiles/{id}")
	@Produces({ MediaType.APPLICATION_JSON })
	public Response getProfileFromId(@PathParam("id") String idprofile, @PathParam("CODESPACE") String codespace)
			throws NumberFormatException, ParseException {
		LOGGER.info("In GET profile");
		String pilot = ReadFromConfig.loadByName(codespace);
		if (pilot == null) {
			return Response.status(400).header("Error", "No codespace found").build();
		}
		Delegatedatabasemap4data dbmap4data = new Delegatedatabasemap4data();
		Calendar profile = dbmap4data.readParameterCalendarFromId(Integer.parseInt(idprofile), pilot, null, null);
		if (profile == null) {
			return Response.status(404).build();
		}
		DateFormat outputFormatter = new SimpleDateFormat("yyyy-MM-dd");
		Objbuilding objBuildingProfile = new Objbuilding();
		objBuildingProfile.setId(profile.getCalendarid());
		objBuildingProfile.setBuildingid(profile.getBuildingid());
		objBuildingProfile.setComfortProfile(profile.getProfilecomfort());
		objBuildingProfile.setDay(outputFormatter.format(profile.getDay()));
		objBuildingProfile.setPilot(profile.getPilot());
		return Response.ok(objBuildingProfile).build();
	}
	
	@PUT
	@Path(value = "css/{CODESPACE}/profiles/{id}")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public Response modifyJsonProfile(@PathParam("id") String idprofile, String jsonPayload,
			@PathParam("CODESPACE") String codespace) throws NumberFormatException, ParseException, JSONException {
		LOGGER.info("In GET profile " + idprofile + " - " + codespace);
		LOGGER.info("In GET jsonPayload " + jsonPayload);
		ObjJsonComfortProfile jsonInput = null;
		ObjectMapper mapper = new ObjectMapper();
		String pilot = ReadFromConfig.loadByName(codespace);
		LOGGER.info("In GET profile1");
		if (pilot == null) {
			return Response.status(400).header("Error", "No codespace found").build();
		}
		String json = "";
		try {
			jsonInput = mapper.readValue(jsonPayload, ObjJsonComfortProfile.class);
			jsonInput = Functions.ObjJsonComfortProfileRemoveNull(jsonInput);
			Gson gson = new Gson();
			
			// convert java object to JSON format,
			// and returned as JSON formatted string
			json = gson.toJson(jsonInput);
			LOGGER.info("Update : T " + jsonInput.getTemp());
			LOGGER.info("Update : INT " + jsonInput.getComfortArray().toString());
		}
		catch (IOException e) {
			LOGGER.error("Failed in Json conversion.", e);
			return Response.status(404).build();
		}
		Delegatedatabasemap4data dbmap4data = new Delegatedatabasemap4data();
		Calendar profile = dbmap4data.readParameterCalendarFromId(Integer.parseInt(idprofile), pilot, null, null);
		if (profile == null) {
			return Response.status(404).build();
		}
		DateFormat outputFormatter = new SimpleDateFormat("yyyy-MM-dd");
		Objbuilding objBuildingProfile = new Objbuilding();
		objBuildingProfile.setId(profile.getCalendarid());
		objBuildingProfile.setBuildingid(profile.getBuildingid());
		objBuildingProfile.setComfortProfile(json);
		objBuildingProfile.setDay(outputFormatter.format(profile.getDay()));
		objBuildingProfile.setPilot(profile.getPilot());
		profile.setProfilecomfort(json);
		Integer idCalendar = dbmap4data.save(profile, null, null);
		if (idCalendar == null) {
			return Response.status(404).build();
		}
		return Response.ok(objBuildingProfile).build();
	}
	
	@POST
	@Path(value = "css/{CODESPACE}/profiles/")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public Response setupPeriodComfortProfile(@QueryParam("buildingid") String buildingid,
			@QueryParam("daybelow") String dayBelow, @QueryParam("dayupper") String dayUpper, String jsonPayload,
			@PathParam("CODESPACE") String codespace) throws NumberFormatException, ParseException, JSONException {
		LOGGER.info("In SETUP period profile");
		ObjJsonComfortProfile jsonInput = null;
		ObjectMapper mapper = new ObjectMapper();
		String pilot = ReadFromConfig.loadByName(codespace);
		if (pilot == null) {
			return Response.status(400).header("Error", "No codespace found").build();
		}
		String json = "";
		try {
			jsonInput = mapper.readValue(jsonPayload, ObjJsonComfortProfile.class);
			Gson gson = new Gson();
			
			// convert java object to JSON format,
			// and returned as JSON formatted string
			json = gson.toJson(jsonInput);
			LOGGER.info("Update : T " + jsonInput.getTemp());
			LOGGER.info("Update : INT " + jsonInput.getComfortArray().toString());
		}
		catch (IOException e) {
			LOGGER.error("Failed in Json conversion.", e);
			return Response.status(404).build();
		}
		DateFormat outputFormatter = new SimpleDateFormat("yyyy-MM-dd");
		//		String dayU = outputFormatter.format(dayUpper);
		java.util.Calendar cal = java.util.Calendar.getInstance();
		cal.setTime(outputFormatter.parse(dayBelow));
		DelegateoperationSuggestion sugg = new DelegateoperationSuggestion();
		Calendar calendar = new Calendar();
		calendar.setBuildingid(Integer.parseInt(buildingid));
		calendar.setDay(cal.getTime());
		calendar.setPilot(pilot);
		calendar.setProfilecomfort(json);
		ArrayList<Calendar> calendarCreated = sugg.setupComfortPeriod(calendar, dayUpper);
		ArrayList<Integer> idList = new ArrayList<Integer>();
		for (Calendar elem : calendarCreated) {
			idList.add(elem.getCalendarid());
		}
		ObjCalendarIdList calList = new ObjCalendarIdList();
		calList.setId(idList);
		
		//TODO chiamare una funzione passando i 2 limiti, profilo di comfort tramite json (ObjJsonComfortProfile) e 
		//caricare questo profilo per il periodo indicato dai parametri
		return Response.ok(calList).build();
	}
	
	@DELETE
	@Path(value = "css/{CODESPACE}/profiles/{id}")
	@Produces({ MediaType.APPLICATION_JSON })
	public Response setupPeriodComfortProfile(@PathParam("id") String idprofile,
			@PathParam("CODESPACE") String codespace) throws NumberFormatException, ParseException, JSONException {
		LOGGER.info("In DELETE period profile");
		Delegatedatabasemap4data dbmap4 = new Delegatedatabasemap4data();
		Integer idProfile = null;
		String pilot = ReadFromConfig.loadByName(codespace);
		try {
			idProfile = Integer.parseInt(idprofile);
		}
		catch (Exception e) {
			LOGGER.error(idprofile + " is'n a integer");
			return Response.status(400).build();
		}
		
		//Calendar calProfile = dbmap4.readParameterCalendarFromId(idProfile, pilot, null, null);
		if (!dbmap4.delete(idProfile, pilot, null, null)) {
			return Response.status(400).build();
		}
		
		return Response.status(200).build();
	}
	
	@GET
	@Path(value = "css/{CODESPACE}/thresholds/{id}")
	@Produces({ MediaType.APPLICATION_JSON })
	public Response getThreshold(@PathParam("id") String idcalendarshelter, @PathParam("CODESPACE") String codespace)
			throws ParseException {
		DateFormat outputFormatter = new SimpleDateFormat("yyyy-MM-dd");
		String pilot = ReadFromConfig.loadByName(codespace);
		if (pilot == null) {
			return Response.status(400).header("Error", "No codespace found").build();
		}
		Long idcalShelter;
		try {
			idcalShelter = Long.parseLong(idcalendarshelter);
		}
		catch (Exception e) {
			return Response.status(400).build();
		}
		Delegatedatabasemap4data dbmap4 = new Delegatedatabasemap4data();
		Calendarshelter calShel = dbmap4.readParameterCalendarShelterFromId(idcalShelter, pilot, null, null, null);
		ObjShelter shelterResp = new ObjShelter();
		if (calShel != null) {
			shelterResp.setId(calShel.getCalendarshelterid());
			shelterResp.setDay(outputFormatter.format(calShel.getDay()));
			shelterResp.setColdthreshold(calShel.getThresholdcold());
			shelterResp.setWarmthreshold(calShel.getThresholdwarm());
		}
		return Response.ok(shelterResp).build();
	}
	
	@GET
	@Path(value = "css/{CODESPACE}/thresholds")
	@Produces({ MediaType.APPLICATION_JSON })
	public Response getThreshold(@PathParam("CODESPACE") String codespace, @QueryParam("foiuri") String shelterName,
			@QueryParam("day") String day) throws ParseException {
		DateFormat outputFormatter = new SimpleDateFormat("yyyy-MM-dd");
		String pilot = ReadFromConfig.loadByName(codespace);
		if (pilot == null) {
			return Response.status(400).header("Error", "No codespace found").build();
		}
		
		Delegatedatabasemap4data dbmap4 = new Delegatedatabasemap4data();
		List<Calendarshelter> calShelList = dbmap4.readParameterCalendarShelterFromName(shelterName, pilot, null, null);
		List<ObjShelter> shelterResp = new ArrayList<ObjShelter>();
		if (calShelList != null) {
			for (Calendarshelter calShel : calShelList) {
				ObjShelter oShel = new ObjShelter();
				oShel.setId(calShel.getCalendarshelterid());
				oShel.setDay(outputFormatter.format(calShel.getDay()));
				oShel.setColdthreshold(calShel.getThresholdcold());
				oShel.setWarmthreshold(calShel.getThresholdwarm());
				shelterResp.add(oShel);
			}
		}
		if (shelterResp.isEmpty()) {
			return Response.ok().build();
		}
		ObjThresholdList objResp = new ObjThresholdList();
		objResp.setThresholds(shelterResp);
		return Response.ok(objResp).build();
		
	}
	
	@PUT
	@Path(value = "css/{CODESPACE}/thresholds/")
	@Produces({ MediaType.APPLICATION_JSON })
	public Response setThreshold(@QueryParam("foiuri") String shelterName, @QueryParam("day") String dayThreshold,
			@QueryParam("warmthreshold") String warmThreshold, @QueryParam("coldthreshold") String coldThreshold,
			@PathParam("CODESPACE") String codespace) throws ParseException {
		String pilot = ReadFromConfig.loadByName(codespace);
		if (pilot == null) {
			return Response.status(400).header("Error", "No codespace found").build();
		}
		DateFormat outputFormatter = new SimpleDateFormat("yyyy-MM-dd");
		Integer coldT = null;
		Integer warmT = null;
		try {
			coldT = Integer.parseInt(coldThreshold);
		}
		catch (Exception e) {
			LOGGER.info("coldthreshold not found");
		}
		try {
			warmT = Integer.parseInt(warmThreshold);
		}
		catch (Exception e) {
			LOGGER.info("warmthreshold not found");
		}
		if (coldT == null && warmT == null) {
			return Response.status(400).header("Error", "No thresholds found").build();
		}
		Date d = outputFormatter.parse(dayThreshold);
		
		Delegatedatabasemap4data dbmap4 = new Delegatedatabasemap4data();
		Calendarshelter calShelter = dbmap4.readParameterCalendarShelterFromDate(shelterName, pilot, d, null, null);
		if (coldT != null) {
			calShelter.setThresholdcold(coldT);
		}
		if (warmT != null) {
			calShelter.setThresholdwarm(warmT);
		}
		long idShelter = dbmap4.save(calShelter, null, null);
		ObjShelter shelterResp = new ObjShelter();
		shelterResp.setId(idShelter);
		shelterResp.setColdthreshold(calShelter.getThresholdcold());
		shelterResp.setWarmthreshold(calShelter.getThresholdwarm());
		shelterResp.setDay(outputFormatter.format(calShelter.getDay()));
		ObjThresholdList objtList = new ObjThresholdList();
		List<ObjShelter> objList = new ArrayList<ObjShelter>();
		objList.add(shelterResp);
		objtList.setThresholds(objList);
		return Response.ok(objtList).build();
	}
	
	@GET
	@Path(value = "test/")
	@Produces({ MediaType.APPLICATION_JSON })
	public Response getProfileFromId() throws NumberFormatException, ParseException {
		LOGGER.info("In TEST profile");
		
		ScheduledTask ff = new ScheduledTask();
		ff.run();
		ObjJsonComfortProfile obj = new ObjJsonComfortProfile();
		ObjComfortArray objca = new ObjComfortArray();
		objca.setDah(7);
		objca.setDam(45);
		objca.setAh(18);
		objca.setAm(0);
		List<ObjComfortArray> listobjca = new ArrayList<ObjComfortArray>();
		listobjca.add(objca);
		obj.setComfortArray(listobjca);
		obj.setTemp(20);
		return Response.ok(obj).build();
	}
	
	@GET
	@Path(value = "run/")
	public Response runSuggestion() {
		DateFormat outputFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
		LOGGER.info("Forced to shoot " + outputFormatter.format(new Date()));
		ScheduledTask ff = new ScheduledTask();
		ff.run();
		return Response.ok().build();
	}
}
