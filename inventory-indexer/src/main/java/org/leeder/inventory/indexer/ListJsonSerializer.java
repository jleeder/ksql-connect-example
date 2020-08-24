package org.leeder.inventory.indexer;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Serializer;

public class ListJsonSerializer<T> implements Serializer<List<T>> {

    private ObjectMapper om = new ObjectMapper();
    @Override
    public void close() {
        // TODO Auto-generated method stub

    }

    @Override
    public void configure(Map<String, ?> config, boolean isKey) {
        // TODO Auto-generated method stub

    }

    @Override
    public byte[] serialize(String topic, List<T> data) {
        //System.out.println("Started serialize");
        byte[] retval = null;
        try {
            //System.out.println(data.getClass());
            String json = om.writeValueAsString(data);
            //System.out.println(json);
            retval = json.getBytes();
        } catch (JsonProcessingException e) {
            throw new SerializationException();
        }
        //System.out.println("Done serialize");
        return retval;
    }

}