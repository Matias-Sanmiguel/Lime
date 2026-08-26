import type { OperationType, PropertyStatus, PropertyType } from "./types";

export const typeLabels: Record<PropertyType, string> = {
  APARTMENT: "Departamento",
  HOUSE: "Casa",
  LAND: "Terreno",
  COMMERCIAL: "Comercial",
  OTHER: "Otro",
};

export const operationLabels: Record<OperationType, string> = {
  SALE: "Venta",
  RENT: "Alquiler",
  TEMPORARY_RENT: "Temporal",
};

export const statusLabels: Record<PropertyStatus, string> = {
  DRAFT: "Borrador",
  PUBLISHED: "Publicado",
  PAUSED: "Pausado",
};

export function formatPrice(price: number | null, currency: string | null) {
  if (price === null || Number.isNaN(price)) return "Consultar precio";
  const formatted = new Intl.NumberFormat("es-AR", {
    maximumFractionDigits: 0,
  }).format(price);
  return `${currency ?? "$"} ${formatted}`;
}

export function formatArea(value: number | null) {
  if (value === null || Number.isNaN(value)) return null;
  return `${new Intl.NumberFormat("es-AR", { maximumFractionDigits: 0 }).format(value)} m2`;
}
