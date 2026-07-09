// Generic "Coming Soon" placeholder used for modules not yet built.
// Replace this with the real page when the module is implemented.
export default function ComingSoonPage({ title, icon, description, plannedFor }) {
  return (
    <div className="coming-soon">
      <div className="coming-soon-icon">{icon}</div>
      <h1>{title}</h1>
      <p className="muted">{description}</p>
      {plannedFor && (
        <span className="badge coming-badge">Planned — {plannedFor}</span>
      )}
      <div className="coming-soon-features">
        <p className="muted small">This module is under development. Check back soon.</p>
      </div>
    </div>
  );
}
