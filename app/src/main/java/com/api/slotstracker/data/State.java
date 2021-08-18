package com.api.slotstracker.data;

public class State {
    private String loc;
    private String totalConfirmed;
    private String deaths;
    private String discharged;

    public State(String loc, String totalConfirmed, String deaths, String discharged) {
        this.loc = loc;
        this.totalConfirmed = totalConfirmed;
        this.deaths = deaths;
        this.discharged = discharged;
    }
    public State(String totalConfirmed, String deaths, String discharged) {
        this.totalConfirmed = totalConfirmed;
        this.deaths = deaths;
        this.discharged = discharged;
    }

    public String getLoc() {
        return loc;
    }

    public void setLoc(String loc) {
        this.loc = loc;
    }

    public String getTotalConfirmed() {
        return totalConfirmed;
    }

    public void setTotalConfirmed(String totalConfirmed) {
        this.totalConfirmed = totalConfirmed;
    }

    public String getDeaths() {
        return deaths;
    }

    public void setDeaths(String deaths) {
        this.deaths = deaths;
    }

    public String getDischarged() {
        return discharged;
    }

    public void setDischarged(String discharged) {
        this.discharged = discharged;
    }
}
