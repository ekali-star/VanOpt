CREATE TABLE optimization_request
(
    id            CHAR(36) PRIMARY KEY,
    max_volume    INT       NOT NULL,
    total_volume  INT       NOT NULL,
    total_revenue INT       NOT NULL,
    created_at    TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE shipment
(
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    request_id CHAR(36)     NOT NULL,
    name       VARCHAR(255) NOT NULL,
    volume     INT          NOT NULL,
    revenue    INT          NOT NULL,
    selected   BOOLEAN      NOT NULL,

    CONSTRAINT fk_shipment_request
        FOREIGN KEY (request_id)
            REFERENCES optimization_request (id)
            ON DELETE CASCADE
);

CREATE INDEX idx_shipment_request_id
    ON shipment (request_id);