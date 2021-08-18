package com.api.slotstracker.data;

public class Slots {
    private String name;
    private String address;
    private String vaccin;
    private String minage;
    private String dose1;
    private String dose2;
    private String price;

    public Slots(String name, String address, String vaccin) {
        this.name = name;
        this.address = address;
        this.vaccin = vaccin;
    }

    public Slots(String name, String address, String vaccin, String minage, String dose1, String dose2, String price) {
        this.name = name;
        this.address = address;
        this.vaccin = vaccin;
        this.minage = minage;
        this.dose1 = dose1;
        this.dose2 = dose2;
        this.price = price;
    }

    public String getMinage() {
        return minage;
    }

    public void setMinage(String minage) {
        this.minage = minage;
    }

    public String getDose1() {
        return dose1;
    }

    public void setDose1(String dose1) {
        this.dose1 = dose1;
    }

    public String getDose2() {
        return dose2;
    }

    public void setDose2(String dose2) {
        this.dose2 = dose2;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getVaccin() {
        return vaccin;
    }

    public void setVaccin(String vaccin) {
        this.vaccin = vaccin;
    }
}
