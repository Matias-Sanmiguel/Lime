import type { SearchFilters } from "../types";

type HeroSearchProps = {
  filters: SearchFilters;
  onFiltersChange: (filters: SearchFilters) => void;
};

export function HeroSearch({ filters, onFiltersChange }: HeroSearchProps) {
  function update(name: keyof SearchFilters, value: string) {
    onFiltersChange({ ...filters, [name]: value });
  }

  return (
    <section className="hero">
      <div className="hero-copy">
        <p className="eyebrow">Marketplace inmobiliario</p>
        <h1>
          Encontrá un lugar
          <span>que se sienta tuyo.</span>
        </h1>
        <p>
          Buscá propiedades en venta o alquiler con una experiencia clara, rápida y pensada para comparar sin ruido.
        </p>
      </div>
      <form className="search-panel" onSubmit={(event) => event.preventDefault()} aria-label="Buscar propiedades">
        <label>
          <span>Ubicación</span>
          <input
            value={filters.city}
            onChange={(event) => update("city", event.target.value)}
            placeholder="Buenos Aires, Rosario..."
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
          <span>Operación</span>
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
        <button type="submit">Buscar</button>
      </form>
    </section>
  );
}
