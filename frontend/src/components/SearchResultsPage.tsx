import { operationLabels, typeLabels } from "../format";
import type { Property, SearchFilters } from "../types";
import { EmptyState } from "./EmptyState";
import { PropertyGrid } from "./PropertyGrid";
import { SearchFiltersForm } from "./SearchFiltersForm";

type SearchResultsPageProps = {
  filters: SearchFilters;
  properties: Property[];
  loading: boolean;
  error: string;
  hasActiveFilters: boolean;
  onFiltersChange: (filters: SearchFilters) => void;
  onSearch: () => void;
  onRetry: () => void;
  onSelect: (property: Property) => void;
  onClearFilters: () => void;
};

export function SearchResultsPage({
  filters,
  properties,
  loading,
  error,
  hasActiveFilters,
  onFiltersChange,
  onSearch,
  onRetry,
  onSelect,
  onClearFilters,
}: SearchResultsPageProps) {
  const summary = buildSearchSummary(filters, properties.length, loading);

  return (
    <section className="search-page page-enter" aria-labelledby="search-heading">
      <div className="search-page-header">
        <div>
          <p className="eyebrow">Busqueda publica</p>
          <h1 id="search-heading">Propiedades para explorar</h1>
          <p>{summary}</p>
        </div>
        {hasActiveFilters && (
          <button className="text-button" type="button" onClick={onClearFilters}>
            Limpiar filtros
          </button>
        )}
      </div>
      <SearchFiltersForm
        filters={filters}
        onFiltersChange={onFiltersChange}
        onSubmit={onSearch}
        submitLabel="Actualizar"
        className="search-panel-results"
      />
      {error ? (
        <EmptyState title="No pudimos cargar la busqueda." action="Reintentar" onAction={onRetry}>
          {error}
        </EmptyState>
      ) : (
        <PropertyGrid
          loading={loading}
          properties={properties}
          hasActiveFilters={hasActiveFilters}
          onSelect={onSelect}
          onClearFilters={onClearFilters}
        />
      )}
    </section>
  );
}

function buildSearchSummary(filters: SearchFilters, count: number, loading: boolean) {
  const active = [
    filters.city.trim() || null,
    filters.type ? typeLabels[filters.type] : null,
    filters.operation ? operationLabels[filters.operation] : null,
    filters.minPrice ? `desde ${filters.minPrice}` : null,
    filters.maxPrice ? `hasta ${filters.maxPrice}` : null,
  ].filter(Boolean);

  if (loading) return "Estamos cargando propiedades con estos criterios.";
  if (active.length === 0) return `${count} propiedades disponibles con filtros abiertos.`;
  return `${count} resultados para ${active.join(" · ")}.`;
}
