 https://debezium.io/documentation/reference/1.0/connectors/postgresql.html


 create table debezium_products (product_id varchar, title varchar, description varchar, sku varchar, attribute_values array<map<varchar,varchar>>, company_id varchar, created_by varchar, created_at bigint, updated_at bigint, deleted_at bigint, __deleted varchar) with (kafka_topic='productservice.public.products', value_format='json', key='product_id');

 set 'auto.offset.reset' = 'earliest'
 


product_options = t_product_option_values + t_options (option_id)
products = t_product + t_product_options (product_id)

rekey product_options (product_option_value_id)
variant_options = t_variant_product_options_values + rekeyed_product_options (key variant_id)
variants = t_variants + variant_options

