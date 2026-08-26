import { formatPrice } from "../format";
import type { Property } from "../types";
import { EmptyState } from "./EmptyState";
import { SkeletonCard } from "./SkeletonCard";
import { StatusBadge } from "./StatusBadge";

type OwnerPanelProps = {
  loading: boolean;
  properties: Property[];
  onBack: () => void;
};

export function OwnerPanel({ loading, properties, onBack }: OwnerPanelProps) {
  const published = properties.filter((property) => property.status === "PUBLISHED").length;
  const drafts = properties.filter((property) => property.status === "DRAFT").length;

  return (
    <section className="owner-shell page-enter">
      <aside className="owner-sidebar">
        <button className="brand owner-brand" type="button" onClick={onBack}>
          <span>Lime</span>
        </button>
        <nav aria-label="Panel">
          <button type="button" className="active">Dashboard</button>
          <button type="button">Propiedades</button>
          <button type="button" disabled>Consultas</button>
          <button type="button" disabled>Perfil</button>
        </nav>
      </aside>
      <div className="owner-content">
        <div className="section-heading">
          <div>
            <p className="eyebrow">Panel del publicador</p>
            <h1>Mis propiedades</h1>
          </div>
          <button type="button">Nuevo aviso</button>
        </div>
        <div className="metrics-grid">
          <Metric label="Total" value={properties.length} />
          <Metric label="Publicadas" value={published} />
          <Metric label="Borradores" value={drafts} />
        </div>
        {loading ? (
          <div className="property-grid compact-grid">
            <SkeletonCard />
            <SkeletonCard />
            <SkeletonCard />
          </div>
        ) : properties.length === 0 ? (
          <EmptyState title="Todavia no tenés publicaciones propias.">
            Cuando existan avisos del usuario 1, el panel los va a listar sin necesitar JWT.
          </EmptyState>
        ) : (
          <div className="owner-list">
            {properties.map((property) => (
              <article key={property.id} className="owner-row">
                <div>
                  <h3>{property.title ?? "Propiedad sin titulo"}</h3>
                  <p>{[property.city, property.province].filter(Boolean).join(", ") || "Ubicación a confirmar"}</p>
                </div>
                <p>{formatPrice(property.price, property.currency)}</p>
                <StatusBadge status={property.status} />
              </article>
            ))}
          </div>
        )}
      </div>
    </section>
  );
}

function Metric({ label, value }: { label: string; value: number }) {
  return (
    <div className="metric">
      <span>{label}</span>
      <strong>{value}</strong>
    </div>
  );
}
