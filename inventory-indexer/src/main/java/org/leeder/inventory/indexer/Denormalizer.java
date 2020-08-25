package org.leeder.inventory.indexer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import java.util.Properties;
import java.util.concurrent.CountDownLatch;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.OffsetResetStrategy;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.Serializer;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.Topology.AutoOffsetReset;
import org.apache.kafka.streams.kstream.Aggregator;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.Grouped;
import org.apache.kafka.streams.kstream.Initializer;
import org.apache.kafka.streams.kstream.Joined;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Printed;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.streams.kstream.Repartitioned;
import org.apache.kafka.streams.kstream.Serialized;


/**
 * Hello world!
 *
 */
public class Denormalizer {
    
    //#region CLASSES
    static public class ProductVariant {
        public String id;
        public String sku;
        public String productId;
        public String productTitle;
        public String companyId;
        public List<Attribute> attributes;
        public String upc;
        public String inturnVariantId;
        public int position;
        public String description;
        public List<Option> options;
        public List<MarketPricing> pricing;
    }

    static public class Attribute {
        public String label;
        public String inturnType;
        public String value;
    }

    static public class Option {
        public String productOptionId;
        public String productOptionValueId;
        public String inturnType;
        public String name;
        public String type;
        public String value;
    }

    static public class MarketPricing {
        public String marketId;
        public String marketName;
        public String currencyCode;
        public List<Price> prices;
    }

    static public class Price {
        public String priceTypeId;
        public String priceDescription;
        public float amount;
    }

    static public class Package {
        public String id;
        public String title;
        public String inturnPackageId;
        public String type;
        public int unitsPerPackage;
        public List<Variants> variants;
    }

    static public class Variants {
        public String variantId;
        public int ratio;
    }

    static public class ProductVariantWithPackages extends ProductVariant{
        public List<PackageWithQuantities> packages;


        public ProductVariantWithPackages(ProductVariant variant){
            this.attributes = variant.attributes;
            this.companyId = variant.companyId;
            this.description = variant.description;
            this.id = variant.id;
            this.inturnVariantId = variant.id;
            this.options = variant.options;
            this.position = variant.position;
            this.pricing = variant.pricing;
            this.productId = variant.productId;
            this.productTitle = variant.productTitle;
            this.sku = variant.sku;
            this.upc = variant.upc;
            
        }
    }

    static public class PackageQuantity {
        public String packageId;
        public String groupId;
        public long quantity;
    }

    static public class PackageWithQuantities extends Package{
        public List<PackageQuantity> quantities;

        public PackageWithQuantities(){
            
        }
        public PackageWithQuantities(Package p){
            this.id = p.id;
            this.inturnPackageId = p.inturnPackageId;
            this.title = p.title;
            this.type = p.type;
            this.unitsPerPackage = p.unitsPerPackage;
            this.variants = p.variants;
        }
    }
    //#endregion

    public static void main(String[] args) {
        //#region CONFIG
        
        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "denormalizer");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:29092"); // assuming that the Kafka broker this
        
        // setting offset reset to earliest so that we can re-run the demo code with the same pre-loaded data
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(StreamsConfig.CACHE_MAX_BYTES_BUFFERING_CONFIG, 0);                                                                     // machine with port
        //props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        //props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        //props.list(System.out);
        final StreamsBuilder builder = new StreamsBuilder();

        //Setup serializer/deserializer
        final Serializer<ProductVariant> productVariantSerializer = new JsonSerializer<>();
        JsonDeserializer<ProductVariant> productVariantDeserializer = new JsonDeserializer<>(ProductVariant.class);
        Serde<ProductVariant> productVariantSerde = Serdes.serdeFrom(productVariantSerializer, productVariantDeserializer);

        final Serializer<Package> packageSerializer = new JsonSerializer<>();
        JsonDeserializer<Package> packageDeserializer = new JsonDeserializer<>(Package.class);
        Serde<Package> packageSerde = Serdes.serdeFrom(packageSerializer, packageDeserializer);

        final Serializer<PackageWithQuantities> packageWithQuantitiesSerializer = new JsonSerializer<>();
        JsonDeserializer<PackageWithQuantities> packagewithQuantitiesDeserializer = new JsonDeserializer<>(PackageWithQuantities.class);
        Serde<PackageWithQuantities> packageWithQuantitiesSerde = Serdes.serdeFrom(packageWithQuantitiesSerializer, packagewithQuantitiesDeserializer);

        final Serializer<PackageQuantity> packageQuantitySerializer = new JsonSerializer<>();
        JsonDeserializer<PackageQuantity> packageQuantityDeserializer = new JsonDeserializer<>(PackageQuantity.class);
        Serde<PackageQuantity> packageQuantitySerde = Serdes.serdeFrom(packageQuantitySerializer, packageQuantityDeserializer);

        final Serializer<List<Package>> packagesListSerializer = new ListJsonSerializer<Package>();
        ListJsonDeserializer<Package> packagesListDeserializer = new ListJsonDeserializer<Package>((Class<List<Package>>)(Object)List.class);
        Serde<List<Package>> packagesListSerde = Serdes.serdeFrom(packagesListSerializer, packagesListDeserializer);

        final Serializer<ProductVariantWithPackages> variantWithPackagesSerializer = new JsonSerializer<ProductVariantWithPackages>();
        JsonDeserializer<ProductVariantWithPackages> variantWithPackagesDeserializer = new JsonDeserializer<ProductVariantWithPackages>(ProductVariantWithPackages.class);
        Serde<ProductVariantWithPackages> variantWithPackagesSerde = Serdes.serdeFrom(variantWithPackagesSerializer, variantWithPackagesDeserializer);
        
        final Serializer<HashMap<String,PackageWithQuantities>> packageMapSerializer = new JsonSerializer<HashMap<String,PackageWithQuantities>>();
        JsonDeserializer<HashMap<String,PackageWithQuantities>> packageMapDeserializer = new JsonDeserializer<HashMap<String,PackageWithQuantities>>((Class<HashMap<String, PackageWithQuantities>>)(Object)HashMap.class);
        Serde<HashMap<String,PackageWithQuantities>> packageMapSerde = Serdes.serdeFrom(packageMapSerializer, packageMapDeserializer);
        
        final Serializer<HashMap<String,PackageQuantity>> packageQuantityMapSerializer = new JsonSerializer<HashMap<String,PackageQuantity>>();
        JsonDeserializer<HashMap<String,PackageQuantity>> packageQuantityMapDeserializer = new JsonDeserializer<HashMap<String,PackageQuantity>>((Class<HashMap<String, PackageQuantity>>)(Object)HashMap.class);
        Serde<HashMap<String,PackageQuantity>> packageQuantityMapSerde = Serdes.serdeFrom(packageQuantityMapSerializer, packageQuantityMapDeserializer);
        
        //#endregion
        
        KTable<String, ProductVariant> tProductVariants = builder.table("product-variants", Consumed.with(Serdes.String(), productVariantSerde));
        //KTable<String, Package> tbl_packages = builder.table("packages", Consumed.with(Serdes.String(), packageSerde));
        KTable<String, Package> sPackages = builder.table("packages", Consumed.with(Serdes.String(), packageSerde));

        KStream<String, PackageQuantity> sPackageQuantity = builder.stream("package-quantities", Consumed.with(Serdes.String(), packageQuantitySerde));

        KTable<String, HashMap<String, PackageQuantity>> tPackageQuantity = sPackageQuantity
            .groupByKey(Grouped.with(Serdes.String(), packageQuantitySerde))
            .aggregate(
                () -> new HashMap<String, PackageQuantity>(),
                (key, pq, pkgQty) -> {
                    
                    pkgQty.put(pq.groupId, pq);
                    return pkgQty;
                },
                Materialized.with(Serdes.String(), packageQuantityMapSerde)
            );
        
        KTable<String, HashMap<String, PackageWithQuantities>> tvariantPkgs = sPackages
            .leftJoin(tPackageQuantity, (pkg, pkgQty) -> {
                PackageWithQuantities result = new PackageWithQuantities(pkg);
                if (pkgQty == null){
                    result.quantities = new ArrayList<PackageQuantity>();
                }
                else{
                    result.quantities = new ArrayList<PackageQuantity>(pkgQty.values());
                }
                return result;
            }).toStream().flatMap((key, pkg) -> {
                /**
                 * Rekey package object by the variant ids it contains 
                 * Outputs a stream of packages per variant so it can be joined with variants later
                 */

                List<KeyValue<String, PackageWithQuantities>> result = new ArrayList<KeyValue<String, PackageWithQuantities>>();
                pkg.variants.forEach((variant) -> {
                    result.add(KeyValue.pair(variant.variantId, pkg));
                });
                return result;  
            })
            .groupByKey(Grouped.with(Serdes.String(), packageWithQuantitiesSerde)) //group by key which is variantId after flatMap
            .aggregate(
                //initializer
                () -> new HashMap<String, PackageWithQuantities>(),
                //aggregation method
                (key, pkg, packages) -> {
                    /**
                     * Keep running map of packages per variant
                     */ 
                    packages.put(pkg.id, pkg);
                    return packages;
                },
                Materialized.with(Serdes.String(), packageMapSerde)
            );

        tProductVariants.join(tvariantPkgs, (variant, pkgs) -> {
            ProductVariantWithPackages result = new ProductVariantWithPackages(variant);
            if (pkgs == null){
                result.packages = new ArrayList<PackageWithQuantities>();
            }
            else {
                result.packages = new ArrayList<PackageWithQuantities>(pkgs.values());
            }

            return result;
        }).toStream().to("output", Produced.with(Serdes.String(), variantWithPackagesSerde));
        
        

        final Topology topology = builder.build();
        System.out.println(topology.describe());
        
        final KafkaStreams streams = new KafkaStreams(topology, props);
        final CountDownLatch latch = new CountDownLatch(1);

        // attach shutdown handler to catch control-c
        Runtime.getRuntime().addShutdownHook(new Thread("streams-shutdown-hook") {
            @Override
            public void run() {
                streams.close();
                latch.countDown();
            }
        });

        try {
            streams.cleanUp();
            System.out.println("STARTING DENORMALIZATION");
            streams.start();
            latch.await();
        } catch (Throwable e) {
            System.exit(1);
        }
        System.exit(0);
    }
}
