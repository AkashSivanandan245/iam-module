import { useEffect, useMemo, useState } from 'react';
import { rolesApi, catalogApi } from '../api/roles';
import { useAuth } from '../context/AuthContext';
import PageHeader from '../components/PageHeader';
import Modal from '../components/Modal';
import ErrorBanner from '../components/ErrorBanner';

export default function RolesPage() {
  const [roles, setRoles] = useState([]);
  const [authorities, setAuthorities] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [showCreate, setShowCreate] = useState(false);
  const [editing, setEditing] = useState(null);
  const { can } = useAuth();

  const load = () => {
    setLoading(true);
    Promise.all([rolesApi.list(), catalogApi.listAuthorities()])
        .then(([rs, auths]) => {
          setRoles(rs);
          setAuthorities(auths);
        })
        .catch(setError)
        .finally(() => setLoading(false));
  };

  useEffect(load, []);

  const remove = async (role) => {
    if (!confirm(`Delete role "${role.name}"? This cannot be undone.`)) return;
    try {
      await rolesApi.remove(role.id);
      load();
    } catch (err) {
      setError(err);
    }
  };

  return (
      <>
        <PageHeader
            title="Roles & permissions"
            subtitle={`${roles.length} role${roles.length === 1 ? '' : 's'} defined`}
            actions={can('ROLE:CREATE') && <button onClick={() => setShowCreate(true)}>New role</button>}
        />

        <ErrorBanner error={error} />

        <div className="card no-pad">
          <table className="data-table">
            <thead>
            <tr>
              <th>Name</th>
              <th>Description</th>
              <th>Permissions</th>
              <th>System</th>
              <th className="col-actions">Actions</th>
            </tr>
            </thead>
            <tbody>
            {loading && <tr><td colSpan="5" className="muted center">Loading…</td></tr>}
            {!loading && roles.map((r) => (
                <tr key={r.id}>
                  <td><strong>{r.name}</strong></td>
                  <td className="muted">{r.description || '—'}</td>
                  <td>
                    <span className="chip small-chip">{(r.permissions || []).length}</span>
                  </td>
                  <td>{r.isSystem ? '✓' : ''}</td>
                  <td>
                    {can('ROLE:UPDATE') && (
                        <button className="ghost small-btn" onClick={() => setEditing(r)}>
                          Edit
                        </button>
                    )}
                    {' '}
                    {can('ROLE:DELETE') && !r.isSystem && (
                        <button className="ghost small-btn danger-btn" onClick={() => remove(r)}>
                          Delete
                        </button>
                    )}
                  </td>
                </tr>
            ))}
            </tbody>
          </table>
        </div>

        {showCreate && (
            <RoleFormModal
                title="Create role"
                onClose={() => setShowCreate(false)}
                onSubmit={async (form) => {
                  await rolesApi.create(form);
                  setShowCreate(false);
                  load();
                }}
            />
        )}
        {editing && (
            <EditRoleModal
                role={editing}
                authorities={authorities}
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

/* -------------------- Create form -------------------- */

function RoleFormModal({ title, initial, onClose, onSubmit }) {
  const [form, setForm] = useState(initial || { name: '', description: '' });
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState(null);

  const submit = async (e) => {
    e.preventDefault();
    setSubmitting(true);
    setError(null);
    try {
      await onSubmit(form);
    } catch (err) {
      setError(err);
      setSubmitting(false);
    }
  };

  return (
      <Modal title={title} onClose={onClose}>
        <ErrorBanner error={error} />
        <form onSubmit={submit} className="stack">
          <label>Name
            <input
                value={form.name}
                onChange={(e) => setForm({ ...form, name: e.target.value })}
                required
            />
          </label>
          <label>Description
            <input
                value={form.description}
                onChange={(e) => setForm({ ...form, description: e.target.value })}
            />
          </label>
          <div className="modal-actions">
            <button type="button" className="ghost" onClick={onClose}>Cancel</button>
            <button type="submit" disabled={submitting}>
              {submitting ? 'Saving…' : 'Save'}
            </button>
          </div>
        </form>
      </Modal>
  );
}

/* -------------------- Edit modal (details + permissions) -------------------- */

function EditRoleModal({ role, authorities, onClose, onSaved }) {
  const [name, setName] = useState(role.name);
  const [description, setDescription] = useState(role.description || '');
  const [selectedIds, setSelectedIds] = useState(() => {
    // Convert the role's current permissions (list of authorityName strings) into a Set of authority IDs.
    const owned = new Set(role.permissions || []);
    return new Set(authorities.filter((a) => owned.has(a.authorityName)).map((a) => a.id));
  });
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState(null);
  const [filter, setFilter] = useState('');

  // Group authorities by module for a cleaner permission editor: MODULE header,
  // then a row per action inside it.
  const grouped = useMemo(() => {
    const map = new Map();
    for (const a of authorities) {
      const [module] = a.authorityName.split(':');
      if (!map.has(module)) map.set(module, []);
      map.get(module).push(a);
    }
    return Array.from(map.entries()).sort(([a], [b]) => a.localeCompare(b));
  }, [authorities]);

  const filtered = grouped
      .map(([m, list]) => [m, list.filter((a) => a.authorityName.toLowerCase().includes(filter.toLowerCase()))])
      .filter(([, list]) => list.length > 0);

  const toggle = (id) => {
    setSelectedIds((prev) => {
      const next = new Set(prev);
      next.has(id) ? next.delete(id) : next.add(id);
      return next;
    });
  };

  const toggleModule = (list) => {
    const allSelected = list.every((a) => selectedIds.has(a.id));
    setSelectedIds((prev) => {
      const next = new Set(prev);
      list.forEach((a) => (allSelected ? next.delete(a.id) : next.add(a.id)));
      return next;
    });
  };

  const save = async () => {
    setSaving(true);
    setError(null);
    try {
      if (name !== role.name || description !== (role.description || '')) {
        await rolesApi.update(role.id, { name, description });
      }
      await rolesApi.updatePermissions(role.id, Array.from(selectedIds));
      onSaved();
    } catch (err) {
      setError(err);
      setSaving(false);
    }
  };

  return (
      <Modal title={`Edit role — ${role.name}`} onClose={onClose}>
        <ErrorBanner error={error} />
        <div className="stack">
          <label>Name
            <input value={name} onChange={(e) => setName(e.target.value)} />
          </label>
          <label>Description
            <input value={description} onChange={(e) => setDescription(e.target.value)} />
          </label>

          <div>
            <div className="permissions-header">
              <strong>Permissions</strong>
              <span className="muted small">{selectedIds.size} selected</span>
            </div>
            <input
                className="permissions-filter"
                placeholder="Filter permissions…"
                value={filter}
                onChange={(e) => setFilter(e.target.value)}
            />
            <div className="permissions-grid">
              {filtered.map(([moduleName, list]) => {
                const allSelected = list.every((a) => selectedIds.has(a.id));
                return (
                    <div className="perm-module" key={moduleName}>
                      <div className="perm-module-header">
                        <label className="check-inline">
                          <input
                              type="checkbox"
                              checked={allSelected}
                              onChange={() => toggleModule(list)}
                          />
                          <strong>{moduleName}</strong>
                        </label>
                        <span className="muted small">
                      {list.filter((a) => selectedIds.has(a.id)).length}/{list.length}
                    </span>
                      </div>
                      <div className="perm-actions">
                        {list.map((a) => {
                          const [, action] = a.authorityName.split(':');
                          return (
                              <label className="check-inline" key={a.id}>
                                <input
                                    type="checkbox"
                                    checked={selectedIds.has(a.id)}
                                    onChange={() => toggle(a.id)}
                                />
                                <span>{action}</span>
                              </label>
                          );
                        })}
                      </div>
                    </div>
                );
              })}
            </div>
          </div>

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