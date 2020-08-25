### Reset Application
```
kafkatools/kafka_2.13-2.6.0/bin/kafka-streams-application-reset.sh --application-id denormalizer --input-topics product-variants,packages,package-quantities --bootstrap-servers 0.0.0.0:29092
```

### Delete Topics
```
kafkatools/kafka_2.13-2.6.0/bin/kafka-topics.sh --bootstrap-server 0.0.0.0:29092 --delete --topic product-variants

kafkatools/kafka_2.13-2.6.0/bin/kafka-topics.sh --bootstrap-server 0.0.0.0:29092 --delete --topic packages
```
### Utility
```
kafkatools/kafka_2.13-2.6.0/bin/kafka-topics.sh --bootstrap-server 0.0.0.0:29092 --create --topic output

kafkatools/kafka_2.13-2.6.0/bin/kafka-topics.sh --bootstrap-server 0.0.0.0:29092 --list 
```

### Add Messages
```
kafkacat -b 0.0.0.0:29092 -t packages -P -l -K : ./messages/samplePackage.txt 

kafkacat -b 0.0.0.0:29092 -t product-variants -P -l -K : ./messages/samplePV.txt

kafkacat -b 0.0.0.0:29092 -t package-quantities -P -l -K : ./messages/samplePQ.txt
```

### Listen for Messages on CLI
```
kafkacat -b 0.0.0.0:29092 -t output -C
```

Caching output data can cause weird delays, by default 30 seconds. https://docs.confluent.io/current/streams/developer-guide/memory-mgmt.html

https://debezium.io/documentation/reference/1.0/connectors/postgresql.html

### KSQL CLI
```docker exec -it ksqldb-cli ksql http://ksqldb-server:8088```

 ```set 'auto.offset.reset' = 'earliest';```
 

### KSQL STREAMS from DEBEZIUM TOPICS
```
create stream db_options (option_id varchar, inturn_type varchar, name varchar, type varchar, updated_at bigint, created_at bigint, deleted_at bigint, __deleted varchar, __ts_ms bigint) 
    WITH (kafka_topic = 'productservice.public.options', value_format = 'json');

create table t_options (option_id varchar, inturn_type varchar, name varchar, type varchar, updated_at bigint, created_at bigint, deleted_at bigint, __deleted varchar, __ts_ms bigint) 
    WITH (kafka_topic = 'DB_OPTIONS_REPART', value_format = 'json', key='option_id');

create stream db_pov (product_option_value_id varchar, option_id varchar, product_id varchar, value varchar, updated_at bigint, created_at bigint, deleted_at bigint) 
    WITH (kafka_topic = 'productservice.public.product_option_values', value_format = 'json');

create stream db_products (product_id varchar, title varchar, description varchar, sku varchar, attribute_values array<map<varchar,varchar>>, company_id varchar, created_by varchar, created_at bigint, updated_at bigint, deleted_at bigint, __deleted BOOLEAN)
    WITH (kafka_topic='productservice.public.products', value_format='json');
```

### KSQL STREAMS REKEYED BY REAL ID
```
create stream db_options_repart AS select * from db_options PARTITION BY option_id;
create stream db_pov_repart AS select * from db_pov PARTITION BY product_id;
```
### KSQL TABLES from TOPIC STREAMS
```
create table product_options as select * from db_product_option_values_rk_option left join db_options_rk_option;
```
product_options = t_product_option_values + t_options (option_id)
products = t_product + t_product_options (product_id)

rekey product_options (product_option_value_id)
rekey t_variant_product_options_values (product_option_value_id)

variant_options (product_option_value_id)= rekeyed_t_variant_product_options_values + rekeyed_product_options 
rekey_variant_options (variant_id)
variants = t_variants + variant_options



CREATE STREAM es_sink_err_stream (MSG VARCHAR) WITH (KAFKA_TOPIC='es_sink_err', VALUE_FORMAT='DELIMITED');


## Version 2

### Product Stream

```
CREATE STREAM products (
    id VARCHAR KEY,
    title VARCHAR,
    sku VARCHAR,
    attributes MAP<VARCHAR, VARCHAR>
) WITH (

)
``` 

### Variant Stream

### ProductVariant Stream

### Package Stream

### Group Stream
