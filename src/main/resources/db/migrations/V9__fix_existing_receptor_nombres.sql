-- Migración para corregir datos existentes de receptor_nombre
-- Actualizar facturas existentes con datos correctos del huésped

UPDATE factura 
SET receptor_nombre = CONCAT(h.nombre, ' ', h.apellido)
FROM reserva r
INNER JOIN huesped h ON r.huesped_titular_id = h.id
WHERE factura.reserva_id = r.id 
AND (factura.receptor_nombre IS NULL OR factura.receptor_nombre = 'Receptor sin datos');

-- Si no hay huésped titular pero hay datos en la propia factura, 
-- intentar usar algún nombre disponible o mantener un valor más descriptivo
UPDATE factura 
SET receptor_nombre = 'Cliente de la reserva ' || COALESCE(reserva_id::text, 'sin ID')
WHERE (receptor_nombre IS NULL OR receptor_nombre = 'Receptor sin datos')
AND reserva_id IS NOT NULL;