import { useState } from "react";
import { resolveImageUrl } from "../api";
import { formatArea, formatPrice, operationLabels, typeLabels } from "../format";
import type { Property } from "../types";
import { EmptyState } from "./EmptyState";
import { InquiryForm } from "./InquiryForm";
import { SkeletonCard } from "./SkeletonCard";
import { StatusBadge } from "./StatusBadge";

type PropertyDetailProps = {
  loading: boolean;
  property: Property | null;
  error: string;
  onBack: () => void;
};

export function PropertyDetail({ loading, property, error, onBack }: PropertyDetailProps) {
  const [selectedImageIndex, setSelectedImageIndex] = useState(0);

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
    property.bathrooms !== null ? `${property.bathrooms} banos` : null,
    property.coveredArea !== null ? `${formatArea(property.coveredArea)} cubiertos` : null,
    property.totalArea !== null ? `${formatArea(property.totalArea)} totales` : null,
  ].filter(Boolean);
  const selectedImage = images[selectedImageIndex] ?? images[0];
  const publisher = property.owner?.agencyName || property.owner?.name;

  return (
    <section className="detail-shell page-enter">
      <button className="text-button" type="button" onClick={onBack}>
        Volver
      </button>
      <div className="detail-gallery" aria-label="Imagenes de la propiedad">
        <GalleryImage src={selectedImage} title={property.title} featured />
        <div className="gallery-stack">
          {(images.length > 1 ? images.slice(0, 4) : [undefined, undefined]).map((image, index) => (
            <button
              className={index === selectedImageIndex ? "gallery-thumb gallery-thumb-active" : "gallery-thumb"}
              type="button"
              key={`${image ?? "fallback"}-${index}`}
              onClick={() => setSelectedImageIndex(index)}
              aria-label={`Ver imagen ${index + 1}`}
            >
              <GalleryImage src={image} title={property.title} />
            </button>
          ))}
        </div>
      </div>
      <div className="detail-layout">
        <article className="detail-content">
          <div className="detail-kicker">
            {property.operation && <span>{operationLabels[property.operation]}</span>}
            <StatusBadge status={property.status} />
          </div>
          <h1>{property.title ?? "Propiedad sin titulo"}</h1>
          <p className="detail-location">{location || "Ubicacion a confirmar"}</p>
          {publisher && <p className="publisher-line">Publicado por {publisher}</p>}
          <p className="detail-price">{formatPrice(property.price, property.currency)}</p>
          <div className="detail-facts">
            {facts.map((fact) => (
              <span key={fact}>{fact}</span>
            ))}
          </div>
          <div className="description-block">
            <h2>Descripcion</h2>
            <p>{property.description || "El publicador todavia no agrego una descripcion."}</p>
          </div>
        </article>
        <InquiryForm propertyId={property.id} />
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
