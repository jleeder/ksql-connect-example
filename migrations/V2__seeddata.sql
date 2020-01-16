TRUNCATE options;
TRUNCATE product_option_values;
TRUNCATE products;

INSERT INTO "postgres"."public"."products" (
    company_id,
    created_by,
    description,
    product_id,
    sku,
    title
  )
VALUES
  (
    '94b706e6-e893-4bb5-95db-e9be4da921cf',
    'b153c707-8717-4f1c-9c20-13d99fcf5d1a',
    'a great product description',
    '39716673-be6b-481d-a9ff-f28db2eae195',
    'SKU12345',
    'Great Product Title'
  );

INSERT INTO options (
    inturn_type,
    name,
    option_id
)
VALUES
(
    'size',
    'Size',
    'a60c76b4-85e8-4f52-85fe-7a9af10299da'
);

INSERT INTO product_option_values (
    option_id,
    product_id,
    product_option_value_id,
    value
)
VALUES (
    'a60c76b4-85e8-4f52-85fe-7a9af10299da',
    '39716673-be6b-481d-a9ff-f28db2eae195',
    '214b1269-92f9-47e8-9978-336fc0f5ad54',
    'S'
);

--   UPDATE products
--   SET 
--     title = 'change to title'
--   where product_id = '39716673-be6b-481d-a9ff-f28db2eae195'

--   DELETE from products where product_id = '39716673-be6b-481d-a9ff-f28db2eae195'



UPDATE options
SET 
     name = 'size'
where option_id = 'a60c76b4-85e8-4f52-85fe-7a9af10299da'