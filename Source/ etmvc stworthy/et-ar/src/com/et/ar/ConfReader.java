package com.et.ar;

import com.et.ar.adapters.Adapter;
import com.et.ar.connections.ConnectionProvider;
import com.et.ar.connections.DriverManagerConnectionProvider;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class ConfReader {
    private Map<String,ConnectionProvider> connections = new HashMap<String,ConnectionProvider>();
    private Map<String,Adapter> adapters = new HashMap<String,Adapter>();
    
    public void init() throws Exception{
        Properties prop = new Properties();
        java.io.InputStream resource = getClass().getClassLoader().getResourceAsStream("activerecord.properties");
        if (resource == null){
            resource = getClass().getClassLoader().getResourceAsStream("/activerecord.properties");
        }
        if (resource != null){
            prop.load(resource);
            for(String bc: prop.getProperty("domain_base_class").split(" ")){
                Properties pp = new Properties();
                pp.setProperty("driver_class", prop.getProperty(bc+".driver_class"));
                pp.setProperty("url", prop.getProperty(bc+".url"));
                pp.setProperty("username", prop.getProperty(bc+".username"));
                pp.setProperty("password", prop.getProperty(bc+".password"));
                pp.setProperty("pool_size", prop.getProperty(bc+".pool_size"));
                String testTable = prop.getProperty(bc+".test_table");
                if (testTable != null){
                    pp.setProperty("test_table", testTable);
                }
                ConnectionProvider cp = new DriverManagerConnectionProvider(pp);
                getConnections().put(bc, cp);
                
                String adapterClassName = prop.getProperty(bc+".adapter_class");
                if (adapterClassName != null){
                    Class<?> adapterClass = Class.forName(adapterClassName);
                    getAdapters().put(bc, (Adapter)adapterClass.newInstance());
                }
            }
        }
    }
    
    public Map<String, ConnectionProvider> getConnections() {
        return connections;
    }

    public void setConnections(Map<String, ConnectionProvider> connections) {
        this.connections = connections;
    }

    public Map<String, Adapter> getAdapters() {
        return adapters;
    }

    public void setAdapters(Map<String, Adapter> adapters) {
        this.adapters = adapters;
    }
}
