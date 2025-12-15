CREATE TABLE factura (
    id BIGSERIAL PRIMARY KEY,
    cliente_id BIGINT NOT NULL,
    reserva_id BIGINT,
    tipo_comprobante VARCHAR(5) NOT NULL,
    punto_venta INTEGER NOT NULL,
    numero_comprobante BIGINT NOT NULL,
    cuit_emisor VARCHAR(20) NOT NULL,
    documento_receptor VARCHAR(20) NOT NULL,
    tipo_documento_receptor VARCHAR(10) NOT NULL,
    fecha_emision TIMESTAMP NOT NULL,
    importe_total NUMERIC(15,2) NOT NULL,
    moneda VARCHAR(5) NOT NULL,
    cae VARCHAR(14),
    cae_vencimiento VARCHAR(10),
    estado VARCHAR(20) NOT NULL,
    detalle TEXT,

    CONSTRAINT fk_factura_cliente
      FOREIGN KEY (cliente_id) REFERENCES cliente (id),

    CONSTRAINT fk_factura_reserva
      FOREIGN KEY (reserva_id) REFERENCES reserva (id)
);
