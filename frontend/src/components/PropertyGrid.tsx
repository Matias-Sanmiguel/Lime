import type { Property } from "../types";
import { EmptyState } from "./EmptyState";
import { PropertyCard } from "./PropertyCard";
import { SkeletonCard } from "./SkeletonCard";

type PropertyGridProps = {
  loading: boolean;
  properties: Property[];
  hasActiveFilters: boolean;
  onSelect: (property: Property) => void;
  onClearFilters: () => void;
};

export function PropertyGrid({ loading, properties, hasActiveFilters, onSelect, onClearFilters }: PropertyGridProps) {
  if (loading) {
    return (
      <div className="property-grid" aria-label="Cargando propiedades">
        {Array.from({ length: 6 }, (_, index) => (
          <SkeletonCard key={index} />
        ))}
      </div>
    );
  }

  if (properties.length === 0) {
    return (
      <EmptyState
        title={hasActiveFilters ? "No encontramos propiedades con estos filtros." : "Todavía no hay propiedades publicadas."}
        action={hasActiveFilters ? "Limpiar filtros" : undefined}
        onAction={hasActiveFilters ? onClearFilters : undefined}
      >
        {hasActiveFilters
          ? "Probá ajustar la ubicación, operación o rango de precio."
          : "Cuando el backend tenga avisos, van a aparecer acá con tarjetas completas."}
      </EmptyState>
    );
  }

  return (
    <div className="property-grid">
      {properties.map((property, index) => (
        <PropertyCard key={property.id} property={property} index={index} onSelect={() => onSelect(property)} />
      ))}
    </div>
  );
}
