-- Agregar campo receptor_nombre a la tabla factura
ALTER TABLE factura ADD COLUMN receptor_nombre VARCHAR(255);

-- Poblar el campo con datos existentes donde sea posible
UPDATE factura 
SET receptor_nombre = CONCAT(h.nombre, ' ', h.apellido)
FROM reserva r
INNER JOIN huesped h ON r.huesped_titular_id = h.id
WHERE factura.reserva_id = r.id 
AND factura.receptor_nombre IS NULL;

-- Para facturas sin huésped titular, usar un valor por defecto temporal
UPDATE factura 
SET receptor_nombre = 'Receptor sin datos'
WHERE receptor_nombre IS NULL;