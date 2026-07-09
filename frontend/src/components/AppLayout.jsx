import { NavLink, Outlet } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

// Each nav item is gated by its own permission code.
// This means admin can grant COURSE:READ to MANAGER without giving them
// ENROLLMENT:READ — so manager sees Courses but not My Enrollments.
const NAV_ITEMS = [
  // Always visible
  { to: '/dashboard',   label: 'Dashboard',          icon: '⌂',  permission: null },

  // Learning tabs — each has its own permission
  { to: '/courses',     label: 'Courses',            icon: '📚', permission: 'COURSE:READ' },
  { to: '/enrollments', label: 'My Enrollments',     icon: '🎓', permission: 'ENROLLMENT:READ' },
  { to: '/assessments', label: 'Assessments',        icon: '📝', permission: 'ASSESSMENT:READ' },
  { to: '/analytics',   label: 'Analytics',          icon: '📊', permission: 'ANALYTICS:READ' },

  // Admin/management tabs
  { to: '/users',       label: 'Users',              icon: '👥', permission: 'USER:READ' },
  { to: '/roles',       label: 'Roles & permissions',icon: '🛡', permission: 'ROLE:READ' },
  { to: '/master-data', label: 'Master data',        icon: '🗄', permission: 'MASTERDATA:READ' },
  { to: '/audit',       label: 'Audit log',          icon: '📜', permission: 'ADM:AUDIT:VIEW' },
];

export default function AppLayout() {
  const { user, can, logout } = useAuth();

  const visibleNavItems = NAV_ITEMS.filter(
      (item) => item.permission === null || can(item.permission)
  );

  return (
      <div className="layout">
        <aside className="sidebar">
          <div className="sidebar-brand">
            <div className="brand-mark">LMS</div>
            <div>
              <div className="brand-title">Xebia LMS</div>
              <div className="brand-sub">IAM</div>
            </div>
          </div>

          <nav className="sidebar-nav">
            {visibleNavItems.map((item) => (
                <NavLink
                    key={item.to}
                    to={item.to}
                    className={({ isActive }) =>
                        'nav-item' + (isActive ? ' nav-item-active' : '')
                    }
                >
                  <span className="nav-icon">{item.icon}</span>
                  <span>{item.label}</span>
                </NavLink>
            ))}
          </nav>

          <div className="sidebar-footer">
            <div className="user-block">
              <div className="user-name">{user?.displayName || 'User'}</div>
              <div className="user-email">{user?.email}</div>
            </div>
            <button className="ghost small-btn" onClick={logout}>
              Sign out
            </button>
          </div>
        </aside>

        <main className="content">
          <Outlet />
        </main>
      </div>
  );
}