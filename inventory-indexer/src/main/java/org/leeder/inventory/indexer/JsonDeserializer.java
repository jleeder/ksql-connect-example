package org.leeder.inventory.indexer;

import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Deserializer;

public class JsonDeserializer<T> implements Deserializer<T> {

    private ObjectMapper om = new ObjectMapper();
    private Class<T> type;

    /*
     * Default constructor needed by kafka
     */
    public JsonDeserializer() {

    }

    public JsonDeserializer(Class<T> type) {
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
            type = (Class<T>) map.get("type");
        }

    }

    @Override
    public T deserialize(String undefined, byte[] bytes) {
        //System.out.println("Started Deserialize");
        T data = null;
        if (bytes == null || bytes.length == 0) {
            return null;
        }

        try {
            //System.out.println(getType());
            data = om.readValue(bytes, type);
        } catch (Exception e) {
            throw new SerializationException(e);
        }
        //.out.println("Done Deserializing");
        return data;
    }

    protected Class<T> getType() {
        return type;
    }

}