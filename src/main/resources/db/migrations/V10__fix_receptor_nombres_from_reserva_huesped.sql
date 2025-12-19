-- Migración para obtener el receptor_nombre desde la tabla reserva_huesped
-- usando el huésped marcado como titular (es_titular = true)

UPDATE factura 
SET receptor_nombre = CONCAT(h.nombre, ' ', h.apellido)
FROM reserva r
INNER JOIN reserva_huesped rh ON rh.reserva_id = r.id AND rh.es_titular = true
INNER JOIN huesped h ON rh.huesped_id = h.id
WHERE factura.reserva_id = r.id 
AND (factura.receptor_nombre IS NULL 
     OR factura.receptor_nombre = 'Receptor sin datos' 
     OR factura.receptor_nombre LIKE 'Cliente de la reserva%');

-- Como fallback, si no hay huésped titular, usar el primer huésped de la reserva
UPDATE factura 
SET receptor_nombre = CONCAT(h.nombre, ' ', h.apellido)
FROM reserva r
INNER JOIN reserva_huesped rh ON rh.reserva_id = r.id
INNER JOIN huesped h ON rh.huesped_id = h.id
WHERE factura.reserva_id = r.id 
AND (factura.receptor_nombre IS NULL 
     OR factura.receptor_nombre = 'Receptor sin datos' 
     OR factura.receptor_nombre LIKE 'Cliente de la reserva%')
AND factura.id NOT IN (
    SELECT f2.id 
    FROM factura f2 
    WHERE f2.receptor_nombre IS NOT NULL 
    AND f2.receptor_nombre != 'Receptor sin datos'
    AND f2.receptor_nombre NOT LIKE 'Cliente de la reserva%'
);