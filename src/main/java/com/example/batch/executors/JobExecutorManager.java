package com.example.batch.executors;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Enumeration;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
public class JobExecutorManager {

    private ConcurrentHashMap<String, AtomicBoolean> clustersFlags = initClusterFlags();

    //TODO - refactoring cluster flags init
    private ConcurrentHashMap<String, AtomicBoolean> initClusterFlags(){
        ConcurrentHashMap<String, AtomicBoolean> flags = new ConcurrentHashMap<>();
        for(int i = 0; i<2; i++) {
            flags.put("cluster" + i, new AtomicBoolean(true));
        }
        return flags;
    }

    public Enumeration<String> getClustersNames(){
        return clustersFlags.keys();
    }

    public final boolean canRun(String clusterName){
        AtomicBoolean flag = clustersFlags.get(clusterName);
        return flag == null ? false : flag.get();
    }

    public final void lockCluster(String clusterName){
        AtomicBoolean flag = getFlag(clusterName);
        flag.set(false);
    }

    public final void unlockCluster(String clusterName){
        AtomicBoolean flag = getFlag(clusterName);
        flag.set(true);
    }

    protected final AtomicBoolean getFlag(String clusterName){
        if (!StringUtils.hasText(clusterName)) {
            throw new IllegalArgumentException("The cluster name must be defined");
        }
        AtomicBoolean flag = clustersFlags.get(clusterName);
        if(flag == null){
            throw new IllegalArgumentException("There is no cluster with the name "+clusterName);
        }

        return flag;
    }
}
