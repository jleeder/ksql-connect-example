CREATE EXTENSION IF NOT EXISTS pgcrypto;

CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
   NEW.updated_at = (now() at time zone 'utc'); 
   RETURN NEW;
END;
$$ language 'plpgsql';

CREATE TABLE products (
    product_id  uuid  DEFAULT gen_random_uuid() NOT NULL,
    title text   NOT NULL,
    description text   NULL,
    sku text   NOT NULL,
    attributes_values jsonb DEFAULT '[]' NOT NULL,
    company_id uuid   NOT NULL,
    created_by uuid   NOT NULL,
    created_at timestamp  DEFAULT (now() at time zone 'utc') NOT NULL,
    updated_at timestamp   NULL,
    deleted_at timestamp   NULL,
    CONSTRAINT pk_products PRIMARY KEY (
        product_id
     ),
    CONSTRAINT uc_products_company_id_sku UNIQUE (
        company_id,
        sku
     )
);

CREATE TRIGGER update_products_updated_at BEFORE UPDATE ON products FOR EACH ROW EXECUTE PROCEDURE  update_updated_at_column();

CREATE TABLE options (
    option_id  uuid  DEFAULT gen_random_uuid() NOT NULL,
    name text   NOT NULL,
    inturn_type text NOT NULL,
    type text  DEFAULT 'STRING' NOT NULL,
    created_at timestamp  DEFAULT (now() at time zone 'utc') NOT NULL,
    updated_at timestamp   NULL,
    deleted_at timestamp   NULL,
    CONSTRAINT pk_options PRIMARY KEY (
        option_id
     ),
    CONSTRAINT uc_options_name_type UNIQUE (
        inturn_type
     ),
    CONSTRAINT type_check CHECK (type IN ('INT', 'FLOAT', 'DECIMAL', 'STRING', 'DATETIME', 'BOOLEAN', 'JSON'))
);

CREATE TRIGGER update_options_updated_at BEFORE UPDATE ON options FOR EACH ROW EXECUTE PROCEDURE  update_updated_at_column();

CREATE TABLE product_option_values (
    product_option_value_id uuid DEFAULT gen_random_uuid() NOT NULL,
    product_id uuid   NOT NULL,
    option_id uuid   NOT NULL,
    value text   NOT NULL,
    position smallint  DEFAULT 0 NOT NULL,
    created_at timestamp  DEFAULT (now() at time zone 'utc') NOT NULL,
    updated_at timestamp   NULL,
    deleted_at timestamp   NULL,
    CONSTRAINT pk_product_option_values PRIMARY KEY (
        product_option_value_id
     ),
    CONSTRAINT uc_product_option_values_product_id_option_id UNIQUE (
        product_id,
        option_id,
        value
     )
);

CREATE TRIGGER update_pov_updated_at BEFORE UPDATE ON product_option_values FOR EACH ROW EXECUTE PROCEDURE  update_updated_at_column();

CREATE TABLE variants (
    variant_id  uuid  DEFAULT gen_random_uuid() NOT NULL,
    product_id uuid   NOT NULL,
    upc text   NULL,
    inturn_variant_id text   NOT NULL,
    description text   NOT NULL,
    position smallint  DEFAULT 0 NOT NULL,
    created_by uuid   NOT NULL,
    created_at timestamp  DEFAULT (now() at time zone 'utc') NOT NULL,
    updated_at timestamp   NULL,
    deleted_at timestamp   NULL,
    CONSTRAINT pk_variants PRIMARY KEY (
        variant_id
     )
);

CREATE TRIGGER update_variants_updated_at BEFORE UPDATE ON variants FOR EACH ROW EXECUTE PROCEDURE  update_updated_at_column();

CREATE TABLE variant_product_option_values (
    variant_product_option_value_id uuid DEFAULT gen_random_uuid() NOT NULL,
    variant_id uuid   NOT NULL,
    product_option_value_id uuid   NOT NULL,
    created_by uuid   NOT NULL,
    created_at timestamp  DEFAULT (now() at time zone 'utc') NOT NULL,
    updated_at timestamp   NULL,
    deleted_at timestamp   NULL,
    CONSTRAINT pk_variant_product_option_values PRIMARY KEY (
        variant_product_option_value_id
     ),
    CONSTRAINT uc_vpov_variant_id_product_option_value_id UNIQUE (
        variant_id,
        product_option_value_id
     )
);

CREATE TRIGGER update_variant_pov_updated_at BEFORE UPDATE ON variant_product_option_values FOR EACH ROW EXECUTE PROCEDURE  update_updated_at_column();

CREATE TABLE package_variants (
    package_variant_id  uuid DEFAULT gen_random_uuid()  NOT NULL,
    package_id uuid   NOT NULL,
    variant_id uuid   NOT NULL,
    ratio int  DEFAULT 1 NOT NULL,
    created_by uuid   NOT NULL,
    created_at timestamp  DEFAULT (now() at time zone 'utc') NOT NULL,
    updated_at timestamp   NULL,
    deleted_at timestamp   NULL,
    CONSTRAINT pk_package_variants PRIMARY KEY (
        package_variant_id
     ),
    CONSTRAINT uc_package_variants_package_id_variant_id UNIQUE (
        package_id,
        variant_id
     )
);

CREATE TRIGGER update_package_variants_updated_at BEFORE UPDATE ON package_variants FOR EACH ROW EXECUTE PROCEDURE  update_updated_at_column();

CREATE TABLE packages (
    package_id  uuid  DEFAULT gen_random_uuid() NOT NULL,
    inturn_package_id text   NOT NULL,
    title text   NOT NULL,
    created_by uuid   NOT NULL,
    created_at timestamp  DEFAULT (now() at time zone 'utc') NOT NULL,
    updated_at timestamp   NULL,
    deleted_at timestamp   NULL,
    CONSTRAINT pk_packages PRIMARY KEY (
        package_id
     )
);

CREATE TRIGGER update_packages_updated_at BEFORE UPDATE ON packages FOR EACH ROW EXECUTE PROCEDURE  update_updated_at_column();