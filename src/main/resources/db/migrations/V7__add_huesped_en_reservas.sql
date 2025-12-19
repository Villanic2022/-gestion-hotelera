ALTER TABLE reserva
    ADD COLUMN huesped_titular_id BIGINT;

ALTER TABLE reserva
    ADD CONSTRAINT fk_reserva_huesped_titular
    FOREIGN KEY (huesped_titular_id)
    REFERENCES huesped (id);
