export function SkeletonCard() {
  return (
    <div className="skeleton-card" aria-hidden="true">
      <div className="skeleton skeleton-image" />
      <div className="skeleton-lines">
        <div className="skeleton skeleton-price" />
        <div className="skeleton skeleton-title" />
        <div className="skeleton skeleton-meta" />
      </div>
    </div>
  );
}
