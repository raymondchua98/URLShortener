package com.local.shortener;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
public class Geolocation {
	
	@JsonProperty("country_name")
	private String countryName;
	
	@JsonProperty("city")
	private String city;
	
	@JsonProperty("zipcode")
	private String zipCode;
	
	@JsonProperty("latitude")
	private String latitude;
	
	@JsonProperty("longitude")
	private String longitude;
	
	public Geolocation() {
	}
	
	public Geolocation(String countryName, String city, String zipCode, String latitude, String longitude) {
		this.countryName = countryName;
		this.city = city;
		this.zipCode = zipCode;
		this.latitude = latitude;
		this.longitude = longitude;
	}
	
	public String getCountryName() {
		return countryName;
	}
	
	public void setCountryName(String countryName) {
		this.countryName = countryName;
	}
	
	public String getCity() {
		return city;
	}
	
	public void setCity(String city) {
		this.city = city;
	}
	
	public String getZipCode() {
		return zipCode;
	}
	
	public void setZipCode(String zipCode) {
		this.zipCode = zipCode;
	}
	
	public String getLatitude() {
		return latitude;
	}
	
	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}
	
	public String getLongitude() {
		return longitude;
	}
	
	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}
	
	public String generateOriginGeo() {
		return zipCode + ", " + city + ", " + countryName + " (" + latitude + ", " + longitude + ")";
	}
	
	@Override
	public String toString() {
		return "Geolocation [" +
				"countryName= " + countryName +
				", city= " + city +
				", zipCode= " + zipCode +
				", latitude= " + latitude +
				", longitude= " + longitude +
				']';
	}
}
