package com.af.canty;

public class Details {
    String longtitude;
     String cusname;
     String imageurl;
    String latitude;
    String nic;
    String address;

    public Details(String longtitude, String latitude, String nic, String address,String name, String url) {
        this.longtitude = longtitude;
        this.latitude = latitude;
        this.nic = nic;
        this.address = address;
        this.cusname = name;
        this.imageurl = url;
    }

    public Details(String tempImageName, String toString) {
    }

    public String getImagename() {
        return cusname;
    }

    public String getImageurl() {
        return imageurl;
    }

    public String getLongtitude() {
        return longtitude;
    }

    public void setLongtitude(String longtitude) {
        this.longtitude = longtitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getNic() {
        return nic;
    }

    public void setNic(String nic) {
        this.nic = nic;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
