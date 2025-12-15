ALTER TABLE cliente
    ADD COLUMN cuit VARCHAR(20),
    ADD COLUMN condicion_iva VARCHAR(20),
    ADD COLUMN domicilio_fiscal VARCHAR(255);
