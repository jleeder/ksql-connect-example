CREATE SOURCE CONNECTOR `productservice-connector` WITH(
    "connector.class"='io.debezium.connector.postgresql.PostgresConnector', 
    "plugin.name"='pgoutput',
    "database.hostname"='postgres', 
    "database.port"='5432', 
    "database.user"='postgres', 
    "database.password"='postgres', 
    "database.dbname" ='postgres', 
    "database.server.name"='productservice',
    "transforms"='unwrap',
    "transforms.unwrap.type"='io.debezium.transforms.ExtractNewRecordState',
    "transforms.unwrap.operation.header"=true,
    "transforms.unwrap.delete.handling.mode"='rewrite',
    "transforms.unwrap.drop.tombstones"=false,
    "transforms.unwrap.add.source.fields"='lsn,txId,ts_ms',
    "key.converter"='org.apache.kafka.connect.json.JsonConverter',
    "key.converter.schemas.enable"=false,
    "value.converter"='org.apache.kafka.connect.json.JsonConverter',
    "value.converter.schemas.enable"=false
);


CREATE SINK CONNECTOR `elasticsearch-connector` WITH(
    "connector.class"='io.confluent.connect.elasticsearch.ElasticsearchSinkConnector',
    "connection.url"='http://elasticsearch:9200',
    "topics"='DB_OPTIONS_REPART',
    "type.name"='options',
    "value.converter"='org.apache.kafka.connect.json.JsonConverter',
    "value.converter.schemas.enable"=false,
    "key.converter"='org.apache.kafka.connect.storage.StringConverter',
    "key.converter.schemas.enable"=false,
    "schema.ignore"=true,
    "key.ignore"=false
);
-- key and value convertors control the serialization
-- schemas.enables changes the payload from shema/payload to just root values

--  "transforms"='unwrap',
--     "transforms.unwrap.type"='io.debezium.transforms.ExtractNewRecordState',
--     "transforms.unwrap.operation.header"=true,
--     "transforms.unwrap.delete.handling.mode"='rewrite',
--     "transforms.unwrap.drop.tombstones"=false,
--     "transforms.unwrap.add.source.fields"='lsn,txId,ts_ms',