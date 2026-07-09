import { useAuth } from '../context/AuthContext';

// Dashboard reads permissions directly from AuthContext — no extra fetch needed
// since AuthContext already loads them during login/hydration.
export default function DashboardPage() {
  const { user, permissions } = useAuth();

  return (
      <>
        <section className="card">
          <h2>Your profile</h2>
          <dl className="kv">
            <div><dt>Name</dt><dd>{user?.displayName || '—'}</dd></div>
            <div><dt>Email</dt><dd>{user?.email || '—'}</dd></div>
            <div><dt>Status</dt><dd>{user?.status || '—'}</dd></div>
            <div><dt>Permissions</dt><dd>{permissions.size}</dd></div>
          </dl>
        </section>

        <section className="card">
          <h2>Effective permissions</h2>
          {permissions.size === 0 ? (
              <p className="muted">No permissions assigned yet.</p>
          ) : (
              <ul className="chip-list">
                {Array.from(permissions).sort().map((p) => (
                    <li key={p} className="chip">{p}</li>
                ))}
              </ul>
          )}
        </section>
      </>
  );
}