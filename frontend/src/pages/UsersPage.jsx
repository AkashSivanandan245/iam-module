import { useEffect, useState } from 'react';
import { usersApi } from '../api/users';
import { rolesApi } from '../api/roles';
import { useAuth } from '../context/AuthContext';
import PageHeader from '../components/PageHeader';
import Modal from '../components/Modal';
import ErrorBanner from '../components/ErrorBanner';

const USER_STATUSES = ['ACTIVE', 'INVITED', 'SUSPENDED', 'DEACTIVATED'];

export default function UsersPage() {
  const [pageData, setPageData] = useState(null);
  const [pageNumber, setPageNumber] = useState(0);
  const [pageSize] = useState(10);
  const [roles, setRoles] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  const [showCreate, setShowCreate] = useState(false);
  const [editing, setEditing] = useState(null); // full user object being edited
  const { can } = useAuth();

  const roleName = (roleId) => roles.find((r) => r.id === roleId)?.name || '—';

  const load = () => {
    setLoading(true);
    Promise.all([
      usersApi.list(pageNumber, pageSize),
      rolesApi.list().catch(() => []), // fail gracefully if ROLE:READ not granted
    ])
        .then(([users, allRoles]) => {
          setPageData(users);
          setRoles(allRoles);
        })
        .catch(setError)
        .finally(() => setLoading(false));
  };

  useEffect(load, [pageNumber, pageSize]);

  return (
      <>
        <PageHeader
            title="Users"
            subtitle={pageData ? `${pageData.totalElements} total` : 'Loading…'}
            actions={can('USER:CREATE') && <button onClick={() => setShowCreate(true)}>New user</button>}
        />

        <ErrorBanner error={error} />

        <div className="card no-pad">
          <table className="data-table">
            <thead>
            <tr>
              <th>Email</th>
              <th>Name</th>
              <th>Role</th>
              <th>Status</th>
              <th>Created</th>
              <th className="col-actions">Actions</th>
            </tr>
            </thead>
            <tbody>
            {loading && (
                <tr><td colSpan="6" className="muted center">Loading…</td></tr>
            )}
            {!loading && pageData?.content?.length === 0 && (
                <tr><td colSpan="6" className="muted center">No users yet.</td></tr>
            )}
            {!loading && pageData?.content?.map((u) => (
                <tr key={u.userId}>
                  <td>{u.email}</td>
                  <td>{u.displayName}</td>
                  <td>{roleName(u.roleId)}</td>
                  <td>
                  <span className={`badge status-${u.status?.toLowerCase()}`}>
                    {u.status}
                  </span>
                  </td>
                  <td className="muted small">
                    {u.createdAt ? new Date(u.createdAt).toLocaleDateString() : '—'}
                  </td>
                  <td>
                    {can('USER:UPDATE') && (
                        <button className="ghost small-btn" onClick={() => setEditing(u)}>
                          Edit
                        </button>
                    )}
                  </td>
                </tr>
            ))}
            </tbody>
          </table>
        </div>

        {pageData && pageData.totalPages > 1 && (
            <div className="pagination">
              <button
                  className="ghost small-btn"
                  disabled={pageNumber === 0}
                  onClick={() => setPageNumber((p) => Math.max(0, p - 1))}
              >
                ← Prev
              </button>
              <span className="muted">
            Page {pageData.pageNumber + 1} of {pageData.totalPages}
          </span>
              <button
                  className="ghost small-btn"
                  disabled={pageData.isLast}
                  onClick={() => setPageNumber((p) => p + 1)}
              >
                Next →
              </button>
            </div>
        )}

        {showCreate && (
            <CreateUserModal
                roles={roles}
                onClose={() => setShowCreate(false)}
                onCreated={() => {
                  setShowCreate(false);
                  load();
                }}
            />
        )}
        {editing && (
            <EditUserModal
                user={editing}
                roles={roles}
                onClose={() => setEditing(null)}
                onSaved={() => {
                  setEditing(null);
                  load();
                }}
            />
        )}
      </>
  );
}

/* -------------------- Create modal -------------------- */

function CreateUserModal({ roles, onClose, onCreated }) {
  const [form, setForm] = useState({
    email: '',
    displayName: '',
    roleId: '',
    timezone: 'UTC',
  });
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState(null);

  const submit = async (e) => {
    e.preventDefault();
    setSubmitting(true);
    setError(null);
    try {
      await usersApi.create(form);
      onCreated();
    } catch (err) {
      setError(err);
      setSubmitting(false);
    }
  };

  return (
      <Modal title="Create user" onClose={onClose}>
        <ErrorBanner error={error} />
        <form onSubmit={submit} className="stack" id="create-user-form">
          <label>Email
            <input
                type="email"
                value={form.email}
                onChange={(e) => setForm({ ...form, email: e.target.value })}
                required
            />
          </label>
          <label>Display name
            <input
                value={form.displayName}
                onChange={(e) => setForm({ ...form, displayName: e.target.value })}
                required
            />
          </label>
          <label>Role
            <select
                value={form.roleId}
                onChange={(e) => setForm({ ...form, roleId: e.target.value })}
                required
            >
              <option value="">Select a role…</option>
              {roles.map((r) => (
                  <option key={r.id} value={r.id}>{r.name}</option>
              ))}
            </select>
          </label>
          <label>Timezone
            <input
                value={form.timezone}
                onChange={(e) => setForm({ ...form, timezone: e.target.value })}
            />
          </label>
          <div className="modal-actions">
            <button type="button" className="ghost" onClick={onClose}>Cancel</button>
            <button type="submit" disabled={submitting}>
              {submitting ? 'Creating…' : 'Create'}
            </button>
          </div>
        </form>
      </Modal>
  );
}

/* -------------------- Edit modal -------------------- */

function EditUserModal({ user, roles, onClose, onSaved }) {
  const [profile, setProfile] = useState({
    displayName: user.displayName,
    timezone: user.timezone || 'UTC',
  });
  const [roleId, setRoleId] = useState(user.roleId);
  const [status, setStatus] = useState(user.status);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState(null);

  // Save is a small orchestration — three separate endpoints for profile / role / status,
  // fired only if the field actually changed. Simpler than a single PUT that overloads
  // its responsibilities.
  const save = async () => {
    setSaving(true);
    setError(null);
    try {
      if (
          profile.displayName !== user.displayName ||
          profile.timezone !== (user.timezone || 'UTC')
      ) {
        await usersApi.update(user.userId, profile);
      }
      if (roleId !== user.roleId) {
        await usersApi.assignRole(user.userId, roleId);
      }
      if (status !== user.status) {
        await usersApi.changeStatus(user.userId, status);
      }
      onSaved();
    } catch (err) {
      setError(err);
      setSaving(false);
    }
  };

  return (
      <Modal title={`Edit user — ${user.email}`} onClose={onClose}>
        <ErrorBanner error={error} />
        <div className="stack">
          <label>Display name
            <input
                value={profile.displayName}
                onChange={(e) => setProfile({ ...profile, displayName: e.target.value })}
            />
          </label>
          <label>Timezone
            <input
                value={profile.timezone}
                onChange={(e) => setProfile({ ...profile, timezone: e.target.value })}
            />
          </label>
          <label>Role
            <select value={roleId} onChange={(e) => setRoleId(e.target.value)}>
              {roles.map((r) => (
                  <option key={r.id} value={r.id}>{r.name}</option>
              ))}
            </select>
          </label>
          <label>Status
            <select value={status} onChange={(e) => setStatus(e.target.value)}>
              {USER_STATUSES.map((s) => <option key={s} value={s}>{s}</option>)}
            </select>
          </label>
          <div className="modal-actions">
            <button type="button" className="ghost" onClick={onClose}>Cancel</button>
            <button type="button" onClick={save} disabled={saving}>
              {saving ? 'Saving…' : 'Save changes'}
            </button>
          </div>
        </div>
      </Modal>
  );
}