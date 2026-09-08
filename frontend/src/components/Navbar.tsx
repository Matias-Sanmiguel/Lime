type NavbarProps = {
  onNavigate: (view: "home" | "search" | "detail" | "owner", id?: number) => void;
  onSearchNavigate: () => void;
};

export function Navbar({ onNavigate, onSearchNavigate }: NavbarProps) {
  return (
    <header className="navbar">
      <button className="brand" type="button" onClick={() => onNavigate("home")} aria-label="Ir al inicio">
        <img src="/lime-mark.svg" alt="" />
        <span>Lime</span>
      </button>
      <nav className="nav-links" aria-label="Principal">
        <button type="button" onClick={onSearchNavigate}>
          Comprar
        </button>
        <button type="button" onClick={onSearchNavigate}>
          Alquilar
        </button>
        <button type="button" onClick={() => onNavigate("owner")}>
          Publicar
        </button>
      </nav>
      <div className="nav-actions">
        <button type="button" className="ghost-action" aria-label="Favoritos">
          Favoritos
        </button>
        <button type="button" className="account-button">
          Mi cuenta
        </button>
      </div>
      <button className="mobile-menu" type="button" aria-label="Abrir menu">
        <span />
        <span />
      </button>
    </header>
  );
}
