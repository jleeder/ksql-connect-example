package org.leeder.inventory.indexer;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Deserializer;

public class ListJsonDeserializer<T> implements Deserializer<List<T>> {

    private ObjectMapper om = new ObjectMapper();
    private Class<List<T>> type;

    /*
     * Default constructor needed by kafka
     */
    public ListJsonDeserializer() {

    }

    public ListJsonDeserializer(Class<List<T>> type) {
        this.type = type;
    }

    @Override
    public void close() {
        // TODO Auto-generated method stub

    }

    @SuppressWarnings("unchecked")
    @Override
    public void configure(Map<String, ?> map, boolean arg1) {
        if (type == null) {
            type = (Class<List<T>>) map.get("type");
        }

    }

    @Override
    public List<T> deserialize(String undefined, byte[] bytes) {
        //System.out.println("Started Deserialize");
        List<T> data = null;
        if (bytes == null || bytes.length == 0) {
            return null;
        }

        try {
            //System.out.println(getType());
            
            data = om.readValue(bytes, new TypeReference<List<T>>() { });
        } catch (Exception e) {
            throw new SerializationException(e);
        }
        //.out.println("Done Deserializing");
        return data;
    }

    protected Class<List<T>> getType() {
        return type;
    }

}