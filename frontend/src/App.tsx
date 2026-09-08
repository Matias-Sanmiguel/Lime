import { useEffect, useMemo, useState } from "react";
import { getMyProperties, getProperties, getProperty } from "./api";
import { EmptyState } from "./components/EmptyState";
import { HeroSearch } from "./components/HeroSearch";
import { Navbar } from "./components/Navbar";
import { OwnerPanel } from "./components/OwnerPanel";
import { PropertyDetail } from "./components/PropertyDetail";
import { PropertyGrid } from "./components/PropertyGrid";
import { SearchResultsPage } from "./components/SearchResultsPage";
import type { OperationType, Property, PropertyType, SearchFilters } from "./types";

const initialFilters: SearchFilters = {
  city: "",
  type: "",
  operation: "",
  minPrice: "",
  maxPrice: "",
};

type View = "home" | "search" | "detail" | "owner";

function currentPathView(): { view: View; id?: number } {
  const path = window.location.pathname;
  if (path === "/buscar") return { view: "search" };
  if (path.startsWith("/propiedad/") || path.startsWith("/propiedades/")) {
    const id = Number(path.split("/").pop());
    return Number.isFinite(id) ? { view: "detail", id } : { view: "home" };
  }
  if (path === "/publicador") return { view: "owner" };
  return { view: "home" };
}

function filtersFromUrl(): SearchFilters {
  const params = new URLSearchParams(window.location.search);
  return {
    city: params.get("city") ?? "",
    type: parsePropertyType(params.get("type")),
    operation: parseOperationType(params.get("operation")),
    minPrice: params.get("minPrice") ?? "",
    maxPrice: params.get("maxPrice") ?? "",
  };
}

function filtersToQuery(filters: SearchFilters) {
  const params = new URLSearchParams();
  if (filters.city.trim()) params.set("city", filters.city.trim());
  if (filters.type) params.set("type", filters.type);
  if (filters.operation) params.set("operation", filters.operation);
  if (filters.minPrice.trim()) params.set("minPrice", filters.minPrice.trim());
  if (filters.maxPrice.trim()) params.set("maxPrice", filters.maxPrice.trim());
  return params.toString();
}

function parsePropertyType(value: string | null): "" | PropertyType {
  return value === "APARTMENT" || value === "HOUSE" || value === "LAND" || value === "COMMERCIAL" || value === "OTHER"
    ? value
    : "";
}

function parseOperationType(value: string | null): "" | OperationType {
  return value === "SALE" || value === "RENT" || value === "TEMPORARY_RENT" ? value : "";
}

export default function App() {
  const [filters, setFilters] = useState<SearchFilters>(() =>
    currentPathView().view === "search" ? filtersFromUrl() : initialFilters,
  );
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
    const onPopState = () => {
      const nextView = currentPathView();
      setViewState(nextView);
      if (nextView.view === "search") setFilters(filtersFromUrl());
    };
    window.addEventListener("popstate", onPopState);
    return () => window.removeEventListener("popstate", onPopState);
  }, []);

  useEffect(() => {
    if (viewState.view !== "home") return;
    setLoading(true);
    setError("");
    getProperties(initialFilters)
      .then((page) => setProperties(page.content))
      .catch((err: Error) => setError(err.message))
      .finally(() => setLoading(false));
  }, [viewState.view]);

  useEffect(() => {
    if (viewState.view !== "search") return;
    setLoading(true);
    setError("");
    getProperties(filters)
      .then((page) => setProperties(page.content))
      .catch((err: Error) => setError(err.message))
      .finally(() => setLoading(false));
  }, [filters, viewState.view]);

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
    const path = view === "detail" && id ? `/propiedad/${id}` : view === "owner" ? "/publicador" : "/";
    window.history.pushState({}, "", path);
    setViewState({ view, id });
    window.scrollTo({ top: 0, behavior: "smooth" });
  }

  function navigateSearch(nextFilters = filters, replace = false) {
    const query = filtersToQuery(nextFilters);
    const path = query ? `/buscar?${query}` : "/buscar";
    window.history[replace ? "replaceState" : "pushState"]({}, "", path);
    setFilters(nextFilters);
    setViewState({ view: "search" });
    window.scrollTo({ top: 0, behavior: "smooth" });
  }

  function resetFilters() {
    setFilters(initialFilters);
    if (viewState.view === "search") navigateSearch(initialFilters, true);
  }

  return (
    <>
      <Navbar onNavigate={navigate} onSearchNavigate={navigateSearch} />
      <main>
        {viewState.view === "home" && (
          <div className="page-enter">
            <HeroSearch filters={filters} onFiltersChange={setFilters} onSearch={() => navigateSearch(filters)} />
            <section className="section-shell" aria-labelledby="featured-heading">
              <div className="section-heading">
                <div>
                  <p className="eyebrow">Propiedades disponibles</p>
                  <h2 id="featured-heading">Elegi con datos claros y una experiencia con caracter.</h2>
                </div>
              </div>
              {error ? (
                <EmptyState title="No pudimos cargar las propiedades." action="Reintentar" onAction={() => setFilters({ ...filters })}>
                  {error}
                </EmptyState>
              ) : (
                <PropertyGrid
                  loading={loading}
                  properties={properties}
                  hasActiveFilters={false}
                  onSelect={(property) => navigate("detail", property.id)}
                  onClearFilters={resetFilters}
                />
              )}
            </section>
            <section className="value-band" aria-label="Diferenciales de Lime">
              <div>
                <span>01</span>
                <h3>Busqueda limpia</h3>
                <p>Filtros simples, lectura rapida y datos comparables desde el primer vistazo.</p>
              </div>
              <div>
                <span>02</span>
                <h3>Publicaciones claras</h3>
                <p>Precio, operacion, ubicacion y superficie ordenados para decidir sin friccion.</p>
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
                <h2>Publica tu propiedad con una presencia mas cuidada.</h2>
                <p>Llega a mas personas con avisos ordenados, visuales y listos para consultar.</p>
              </div>
              <button type="button" onClick={() => navigate("owner")}>
                Publicar ahora
              </button>
            </section>
            <footer className="footer">
              <span>Lime</span>
              <p>Marketplace inmobiliario con precision editorial.</p>
            </footer>
          </div>
        )}

        {viewState.view === "search" && (
          <SearchResultsPage
            filters={filters}
            properties={properties}
            loading={loading}
            error={error}
            hasActiveFilters={hasActiveFilters}
            onFiltersChange={setFilters}
            onSearch={() => navigateSearch(filters, true)}
            onRetry={() => navigateSearch(filters, true)}
            onSelect={(property) => navigate("detail", property.id)}
            onClearFilters={resetFilters}
          />
        )}

        {viewState.view === "detail" && (
          <PropertyDetail
            loading={detailLoading}
            property={selectedProperty}
            error={error}
            onBack={() => navigateSearch(filters)}
          />
        )}

        {viewState.view === "owner" && (
          <OwnerPanel loading={ownerLoading} properties={ownerProperties} onBack={() => navigate("home")} />
        )}
      </main>
    </>
  );
}
