-- Agregar columna precio_noche a la tabla tipo_habitacion
ALTER TABLE public.tipo_habitacion 
ADD COLUMN precio_noche NUMERIC(10,2);

-- Comentario descriptivo de la columna
COMMENT ON COLUMN public.tipo_habitacion.precio_noche IS 'Precio por noche del tipo de habitación en la moneda local';