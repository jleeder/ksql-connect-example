 https://debezium.io/documentation/reference/1.0/connectors/postgresql.html


 ```set 'auto.offset.reset' = 'earliest';```
 

### KSQL STREAMS from DEBEZIUM TOPICS
```
create stream db_options (option_id varchar, inturn_type varchar, name varchar, type varchar, updated_at bigint, created_at bigint, deleted_at bigint) 
    WITH (kafka_topic = 'productservice.public.options', value_format = 'json');

create stream db_product_options_values (product_option_values varchar, option_id varchar, product_id varchar, value varchar, updated_at bigint, created_at bigint, deleted_at bigint) 
    WITH (kafka_topic = 'productservice.public.product_option_values', value_format = 'json');

create stream db_products (product_id varchar, title varchar, description varchar, sku varchar, attribute_values array<map<varchar,varchar>>, company_id varchar, created_by varchar, created_at bigint, updated_at bigint, deleted_at bigint, __deleted BOOLEAN)
    WITH (kafka_topic='productservice.public.products', value_format='json');
```

### KSQL STREAMS REKEYED BY REAL ID
```
create stream db_options_rk_option AS select * from db_options PARTITION BY option_id;
create stream db_product_options_values_rk_option AS select * from db_product_options_values PARTITION BY option_id;
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