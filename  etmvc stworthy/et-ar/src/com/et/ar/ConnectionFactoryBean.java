package com.et.ar;

import com.et.ar.adapters.Adapter;
import com.et.ar.connections.DataSourceConnectionProvider;
import javax.sql.DataSource;

public class ConnectionFactoryBean {
    private String domainBaseClass;
    private String adapterClass;
    private DataSource dataSource;

    public String getDomainBaseClass() {
        return domainBaseClass;
    }

    public void setDomainBaseClass(String domainBaseClass) {
        this.domainBaseClass = domainBaseClass;
        init();
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
        init();
    }

    public String getAdapterClass() {
        return adapterClass;
    }

    public void setAdapterClass(String adapterClass) {
        this.adapterClass = adapterClass;
        init();
    }
    
    private void init(){
        if (domainBaseClass != null && dataSource != null){
            DataSourceConnectionProvider cp = new DataSourceConnectionProvider(dataSource);
            ActiveRecordBase.putConnectionProvider(domainBaseClass, cp);
//            ActiveRecordBase.connections.put(domainBaseClass, cp);
        }
        if (domainBaseClass != null && adapterClass != null){
            try{
                Adapter adapter = (Adapter)Class.forName(adapterClass).newInstance();
                ActiveRecordBase.putConnectionAdapter(domainBaseClass, adapter);
//                ActiveRecordBase.adapters.put(domainBaseClass, (Adapter)Class.forName(adapterClass).newInstance());
            }
            catch(Exception e){
                
            }
        }
    }
}
