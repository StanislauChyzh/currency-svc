CREATE TABLE currency
(
    id   SERIAL     NOT NULL PRIMARY KEY,
    code VARCHAR(3) NOT NULL UNIQUE
);

CREATE TABLE exchange_rate
(
    currency_id INT              NOT NULL,
    code        VARCHAR(3)       NOT NULL,
    rate        DOUBLE PRECISION NOT NULL,
    FOREIGN KEY (currency_id) references currency (id),
    UNIQUE (currency_id, code)
);
