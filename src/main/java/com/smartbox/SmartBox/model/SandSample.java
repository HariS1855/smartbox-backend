package com.smartbox.SmartBox.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "samples")
public class SandSample {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Image and Location
    private String imageUrl;
    private Double latitude;
    private Double longitude;

    // Grain size metrics
    private Double d10;
    private Double d50;
    private Double d90;
    private Double meanSize;

    // Classification
    private String category; // e.g., Fine, Medium, Coarse Sand

    // Metadata
    private String beachName;
    private String town;
    private String state;
    private String country;
    private String sampleId;
    private String coinType;       // e.g., Penny, Nickel, Quarter, Euro
    private String locationOnBeach; // Berm, Dune, Swash, Other

    // Auto timestamp
    private LocalDateTime timestamp = LocalDateTime.now();

    // ---- Getters & Setters ----

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getD10() {
        return d10;
    }

    public void setD10(Double d10) {
        this.d10 = d10;
    }

    public Double getD50() {
        return d50;
    }

    public void setD50(Double d50) {
        this.d50 = d50;
    }

    public Double getD90() {
        return d90;
    }

    public void setD90(Double d90) {
        this.d90 = d90;
    }

    public Double getMeanSize() {
        return meanSize;
    }

    public void setMeanSize(Double meanSize) {
        this.meanSize = meanSize;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getBeachName() {
        return beachName;
    }

    public void setBeachName(String beachName) {
        this.beachName = beachName;
    }

    public String getTown() {
        return town;
    }

    public void setTown(String town) {
        this.town = town;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getSampleId() {
        return sampleId;
    }

    public void setSampleId(String sampleId) {
        this.sampleId = sampleId;
    }

    public String getCoinType() {
        return coinType;
    }

    public void setCoinType(String coinType) {
        this.coinType = coinType;
    }

    public String getLocationOnBeach() {
        return locationOnBeach;
    }

    public void setLocationOnBeach(String locationOnBeach) {
        this.locationOnBeach = locationOnBeach;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
