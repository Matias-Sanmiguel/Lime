import { resolveImageUrl } from "../api";
import { formatArea, formatPrice, operationLabels, typeLabels } from "../format";
import type { Property } from "../types";

type PropertyCardProps = {
  property: Property;
  index: number;
  onSelect: () => void;
};

export function PropertyCard({ property, index, onSelect }: PropertyCardProps) {
  const image = resolveImageUrl(property.images?.[0]?.url);
  const location = [property.city, property.province].filter(Boolean).join(", ") || "Ubicación a confirmar";
  const area = formatArea(property.totalArea ?? property.coveredArea);

  return (
    <article className="property-card" style={{ animationDelay: `${index * 45}ms` }}>
      <button type="button" onClick={onSelect} aria-label={`Ver ${property.title ?? "propiedad"}`}>
        <div className="card-media">
          {image ? <img src={image} alt={property.title ?? "Propiedad"} /> : <div className="image-fallback">Lime</div>}
          <span className="operation-badge">{property.operation ? operationLabels[property.operation] : "Aviso"}</span>
        </div>
        <div className="card-body">
          <p className="price">{formatPrice(property.price, property.currency)}</p>
          <h3>{property.title ?? "Propiedad sin titulo"}</h3>
          <p className="location">{location}</p>
          <div className="facts" aria-label="Caracteristicas">
            {property.type && <span>{typeLabels[property.type]}</span>}
            {property.bedrooms !== null && <span>{property.bedrooms} dorm.</span>}
            {property.bathrooms !== null && <span>{property.bathrooms} baños</span>}
            {area && <span>{area}</span>}
          </div>
        </div>
      </button>
    </article>
  );
}
