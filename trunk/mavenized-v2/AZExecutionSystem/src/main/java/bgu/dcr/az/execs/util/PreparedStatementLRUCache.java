/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.execs.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author User
 */
public class PreparedStatementLRUCache extends LinkedHashMap<String, PreparedStatement> {

    private int maxSize;
    private Connection connection;
    private ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    public PreparedStatementLRUCache(int maxSize, Connection connection) {
        this.maxSize = maxSize;
        this.connection = connection;
    }

    @Override
    protected boolean removeEldestEntry(Entry<String, PreparedStatement> eldest) {
        lock.writeLock().lock();
        try {
            if (size() > maxSize) {
                try {
                    eldest.getValue().close();
                } catch (SQLException ex) {
                    Logger.getLogger(PreparedStatementLRUCache.class.getName()).log(Level.SEVERE, null, ex);
                }

                return true;
            }
        } finally {
            lock.writeLock().unlock();
        }

        return false;
    }

    public PreparedStatement retreive(Object key) throws SQLException {
        lock.readLock().lock();
        PreparedStatement got;
        try {
            got = super.get(key.toString());
        } finally {
            lock.readLock().unlock();
        }
        if (got == null) {
            got = connection.prepareStatement(key.toString());
            lock.writeLock().lock();
            try {
                super.put(key.toString(), got);
            } finally {
                lock.writeLock().unlock();
            }
        }

        return got;
    }

    @Override
    public void clear() {
        lock.writeLock().lock();
        try {
            for (PreparedStatement v : values()) {
                try {
                    v.close();
                } catch (SQLException ex) {
                    Logger.getLogger(PreparedStatementLRUCache.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            
            super.clear();
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public PreparedStatement remove(Object key) {
        throw new UnsupportedOperationException("do not remove directly from the cache");
    }

    @Override
    public PreparedStatement put(String key, PreparedStatement value) {
        throw new UnsupportedOperationException("do not directly put statements in this cache");
    }

    
    
    /**
     * please do not use this method - instead use: {@link PreparedStatementLRUCache#retreive(java.lang.Object)
     * }
     *
     * @param key
     * @return
     */
    @Override
    public PreparedStatement get(Object key) {
        try {
            return retreive(key);
        } catch (SQLException ex) {
            return null;
        }
    }

}
