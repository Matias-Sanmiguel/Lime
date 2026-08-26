import { useEffect, useMemo, useState } from "react";
import { getMyProperties, getProperties, getProperty } from "./api";
import { EmptyState } from "./components/EmptyState";
import { HeroSearch } from "./components/HeroSearch";
import { Navbar } from "./components/Navbar";
import { OwnerPanel } from "./components/OwnerPanel";
import { PropertyDetail } from "./components/PropertyDetail";
import { PropertyGrid } from "./components/PropertyGrid";
import type { Property, SearchFilters } from "./types";

const initialFilters: SearchFilters = {
  city: "",
  type: "",
  operation: "",
  minPrice: "",
  maxPrice: "",
};

type View = "home" | "detail" | "owner";

function currentPathView(): { view: View; id?: number } {
  const path = window.location.pathname;
  if (path.startsWith("/propiedades/")) {
    const id = Number(path.split("/").pop());
    return Number.isFinite(id) ? { view: "detail", id } : { view: "home" };
  }
  if (path === "/publicador") return { view: "owner" };
  return { view: "home" };
}

export default function App() {
  const [filters, setFilters] = useState<SearchFilters>(initialFilters);
  const [properties, setProperties] = useState<Property[]>([]);
  const [selectedProperty, setSelectedProperty] = useState<Property | null>(null);
  const [ownerProperties, setOwnerProperties] = useState<Property[]>([]);
  const [viewState, setViewState] = useState(currentPathView);
  const [loading, setLoading] = useState(true);
  const [detailLoading, setDetailLoading] = useState(false);
  const [ownerLoading, setOwnerLoading] = useState(false);
  const [error, setError] = useState("");

  const hasActiveFilters = useMemo(
    () => Object.values(filters).some((value) => value.trim() !== ""),
    [filters],
  );

  useEffect(() => {
    const onPopState = () => setViewState(currentPathView());
    window.addEventListener("popstate", onPopState);
    return () => window.removeEventListener("popstate", onPopState);
  }, []);

  useEffect(() => {
    setLoading(true);
    setError("");
    getProperties(filters)
      .then((page) => setProperties(page.content))
      .catch((err: Error) => setError(err.message))
      .finally(() => setLoading(false));
  }, [filters]);

  useEffect(() => {
    if (viewState.view !== "detail" || !viewState.id) return;
    setDetailLoading(true);
    setError("");
    getProperty(viewState.id)
      .then(setSelectedProperty)
      .catch((err: Error) => setError(err.message))
      .finally(() => setDetailLoading(false));
  }, [viewState]);

  useEffect(() => {
    if (viewState.view !== "owner") return;
    setOwnerLoading(true);
    getMyProperties()
      .then(setOwnerProperties)
      .catch(() => setOwnerProperties([]))
      .finally(() => setOwnerLoading(false));
  }, [viewState.view]);

  function navigate(view: View, id?: number) {
    const path = view === "detail" && id ? `/propiedades/${id}` : view === "owner" ? "/publicador" : "/";
    window.history.pushState({}, "", path);
    setViewState({ view, id });
    window.scrollTo({ top: 0, behavior: "smooth" });
  }

  function resetFilters() {
    setFilters(initialFilters);
  }

  return (
    <>
      <Navbar onNavigate={navigate} />
      <main>
        {viewState.view === "home" && (
          <div className="page-enter">
            <HeroSearch filters={filters} onFiltersChange={setFilters} />
            <section className="section-shell" aria-labelledby="featured-heading">
              <div className="section-heading">
                <div>
                  <p className="eyebrow">Propiedades disponibles</p>
                  <h2 id="featured-heading">Elegí con datos claros y una experiencia con carácter.</h2>
                </div>
                {hasActiveFilters && (
                  <button className="text-button" type="button" onClick={resetFilters}>
                    Limpiar filtros
                  </button>
                )}
              </div>
              {error ? (
                <EmptyState title="No pudimos cargar las propiedades." action="Reintentar" onAction={() => setFilters({ ...filters })}>
                  {error}
                </EmptyState>
              ) : (
                <PropertyGrid
                  loading={loading}
                  properties={properties}
                  hasActiveFilters={hasActiveFilters}
                  onSelect={(property) => navigate("detail", property.id)}
                  onClearFilters={resetFilters}
                />
              )}
            </section>
            <section className="value-band" aria-label="Diferenciales de Lime">
              <div>
                <span>01</span>
                <h3>Búsqueda limpia</h3>
                <p>Filtros simples, lectura rápida y datos comparables desde el primer vistazo.</p>
              </div>
              <div>
                <span>02</span>
                <h3>Publicaciones claras</h3>
                <p>Precio, operación, ubicación y superficie ordenados para decidir sin fricción.</p>
              </div>
              <div>
                <span>03</span>
                <h3>Identidad Lime</h3>
                <p>Un producto inmobiliario con presencia propia, sobrio y reconocible.</p>
              </div>
            </section>
            <section className="publish-band">
              <div>
                <p className="eyebrow">Publicadores</p>
                <h2>Publicá tu propiedad con una presencia más cuidada.</h2>
                <p>Llegá a más personas con avisos ordenados, visuales y listos para consultar.</p>
              </div>
              <button type="button" onClick={() => navigate("owner")}>
                Publicar ahora
              </button>
            </section>
            <footer className="footer">
              <span>Lime</span>
              <p>Marketplace inmobiliario con precisión editorial.</p>
            </footer>
          </div>
        )}

        {viewState.view === "detail" && (
          <PropertyDetail
            loading={detailLoading}
            property={selectedProperty}
            error={error}
            onBack={() => navigate("home")}
          />
        )}

        {viewState.view === "owner" && (
          <OwnerPanel loading={ownerLoading} properties={ownerProperties} onBack={() => navigate("home")} />
        )}
      </main>
    </>
  );
}
