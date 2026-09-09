import type { SearchFilters } from "../types";
import { SearchFiltersForm } from "./SearchFiltersForm";

type HeroSearchProps = {
  filters: SearchFilters;
  onFiltersChange: (filters: SearchFilters) => void;
  onSearch: () => void;
};

export function HeroSearch({ filters, onFiltersChange, onSearch }: HeroSearchProps) {
  return (
    <section className="hero">
      <div className="hero-copy">
        <p className="eyebrow">Marketplace inmobiliario</p>
        <h1>
          Encontra un lugar
          <span>que se sienta tuyo.</span>
        </h1>
        <p>Busca propiedades en venta o alquiler con una experiencia clara, rapida y pensada para comparar sin ruido.</p>
      </div>
      <SearchFiltersForm filters={filters} onFiltersChange={onFiltersChange} onSubmit={onSearch} />
    </section>
  );
}
