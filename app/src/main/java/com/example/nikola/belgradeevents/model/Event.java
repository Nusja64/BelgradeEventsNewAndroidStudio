package com.example.nikola.belgradeevents.model;

import java.util.ArrayList;
import java.util.List;

public class Event {

	private int eventID;
	private String name;
	private String starting_at; //pravilni je staviti date
	private String ending_at;
	private int is_trending;
	private Location location;
	private List<String> tags = new ArrayList<String>();
	private List<Image> images = new ArrayList<Image>();
	
	public Event() {
		super();
		// TODO Auto-generated constructor stub
	}

	public int getEventID() {
		return eventID;
	}

	public void setEventID(int eventID) {
		this.eventID = eventID;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getStarting_at() {
		return starting_at;
	}

	public void setStarting_at(String starting_at) {
		this.starting_at = starting_at;
	}

	public String getEnding_at() {
		return ending_at;
	}

	public void setEnding_at(String ending_at) {
		this.ending_at = ending_at;
	}

	public int Is_trending() {
		return is_trending;
	}

	public void setIs_trending(int is_trending) {
		this.is_trending = is_trending;
	}

	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	public List<String> getTags() {
		return tags;
	}

	public void setTags(List<String> tags) {
		this.tags = tags;
	}

	public List<Image> getImages() {
		return images;
	}

	public void setImages(List<Image> images) {
		this.images = images;
	}

	
	
	

	//"tags": ["Festival"]
}
