package studio.lysid.scales.deploy.service;

public enum Service {

    QueryScale("query-scale-service");



    public final String address;
    Service(String address) {
        this.address = address;
    }
}
