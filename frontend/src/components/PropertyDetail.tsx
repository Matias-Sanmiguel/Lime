import { resolveImageUrl } from "../api";
import { formatArea, formatPrice, operationLabels, typeLabels } from "../format";
import type { Property } from "../types";
import { EmptyState } from "./EmptyState";
import { SkeletonCard } from "./SkeletonCard";
import { StatusBadge } from "./StatusBadge";

type PropertyDetailProps = {
  loading: boolean;
  property: Property | null;
  error: string;
  onBack: () => void;
};

export function PropertyDetail({ loading, property, error, onBack }: PropertyDetailProps) {
  if (loading) {
    return (
      <section className="detail-shell page-enter">
        <button className="text-button" type="button" onClick={onBack}>
          Volver
        </button>
        <div className="detail-loading">
          <SkeletonCard />
          <SkeletonCard />
        </div>
      </section>
    );
  }

  if (error || !property) {
    return (
      <section className="detail-shell page-enter">
        <EmptyState title="No pudimos abrir esta propiedad." action="Volver" onAction={onBack}>
          {error || "El aviso no existe o todavia no esta disponible."}
        </EmptyState>
      </section>
    );
  }

  const images = property.images?.map((image) => resolveImageUrl(image.url)).filter(Boolean) ?? [];
  const location = [property.address, property.city, property.province].filter(Boolean).join(", ");
  const facts = [
    property.type ? typeLabels[property.type] : null,
    property.bedrooms !== null ? `${property.bedrooms} dormitorios` : null,
    property.bathrooms !== null ? `${property.bathrooms} baños` : null,
    formatArea(property.coveredArea),
    formatArea(property.totalArea),
  ].filter(Boolean);

  return (
    <section className="detail-shell page-enter">
      <button className="text-button" type="button" onClick={onBack}>
        Volver
      </button>
      <div className="detail-gallery">
        <GalleryImage src={images[0]} title={property.title} featured />
        <div className="gallery-stack">
          <GalleryImage src={images[1]} title={property.title} />
          <GalleryImage src={images[2]} title={property.title} />
        </div>
      </div>
      <div className="detail-layout">
        <article className="detail-content">
          <div className="detail-kicker">
            {property.operation && <span>{operationLabels[property.operation]}</span>}
            <StatusBadge status={property.status} />
          </div>
          <h1>{property.title ?? "Propiedad sin titulo"}</h1>
          <p className="detail-location">{location || "Ubicación a confirmar"}</p>
          <p className="detail-price">{formatPrice(property.price, property.currency)}</p>
          <div className="detail-facts">
            {facts.map((fact) => (
              <span key={fact}>{fact}</span>
            ))}
          </div>
          <div className="description-block">
            <h2>Descripción</h2>
            <p>{property.description || "El publicador todavia no agrego una descripción."}</p>
          </div>
        </article>
        <aside className="contact-panel" aria-label="Consulta">
          <p className="eyebrow">Consulta</p>
          <h2>Coordiná una visita</h2>
          <p>Dejá tus datos y el publicador puede responderte por mail o teléfono.</p>
          <div className="contact-fields" aria-hidden="true">
            <span>Nombre</span>
            <span>Email</span>
            <span>Mensaje</span>
          </div>
          <button type="button">Consultar propiedad</button>
        </aside>
      </div>
    </section>
  );
}

function GalleryImage({ src, title, featured = false }: { src?: string; title: string | null; featured?: boolean }) {
  return (
    <div className={featured ? "gallery-image gallery-image-featured" : "gallery-image"}>
      {src ? <img src={src} alt={title ?? "Propiedad"} /> : <div className="image-fallback">Lime</div>}
    </div>
  );
}
