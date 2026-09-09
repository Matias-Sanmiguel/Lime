import type { FormEvent } from "react";
import type { SearchFilters } from "../types";

type SearchFiltersFormProps = {
  filters: SearchFilters;
  onFiltersChange: (filters: SearchFilters) => void;
  onSubmit: () => void;
  submitLabel?: string;
  className?: string;
};

export function SearchFiltersForm({
  filters,
  onFiltersChange,
  onSubmit,
  submitLabel = "Buscar",
  className = "",
}: SearchFiltersFormProps) {
  function update(name: keyof SearchFilters, value: string) {
    onFiltersChange({ ...filters, [name]: value });
  }

  function submit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    onSubmit();
  }

  return (
    <form className={`search-panel ${className}`.trim()} onSubmit={submit} aria-label="Buscar propiedades">
      <label>
        <span>Ubicacion</span>
        <input
          value={filters.city}
          onChange={(event) => update("city", event.target.value)}
          placeholder="Buenos Aires, Rosario..."
          autoComplete="address-level2"
        />
      </label>
      <label>
        <span>Tipo</span>
        <select value={filters.type} onChange={(event) => update("type", event.target.value)}>
          <option value="">Todos</option>
          <option value="APARTMENT">Departamento</option>
          <option value="HOUSE">Casa</option>
          <option value="LAND">Terreno</option>
          <option value="COMMERCIAL">Comercial</option>
          <option value="OTHER">Otro</option>
        </select>
      </label>
      <label>
        <span>Operacion</span>
        <select value={filters.operation} onChange={(event) => update("operation", event.target.value)}>
          <option value="">Todas</option>
          <option value="SALE">Comprar</option>
          <option value="RENT">Alquilar</option>
          <option value="TEMPORARY_RENT">Temporal</option>
        </select>
      </label>
      <label>
        <span>Desde</span>
        <input
          value={filters.minPrice}
          onChange={(event) => update("minPrice", event.target.value)}
          inputMode="numeric"
          placeholder="$ minimo"
        />
      </label>
      <label>
        <span>Hasta</span>
        <input
          value={filters.maxPrice}
          onChange={(event) => update("maxPrice", event.target.value)}
          inputMode="numeric"
          placeholder="$ maximo"
        />
      </label>
      <button type="submit">{submitLabel}</button>
    </form>
  );
}
