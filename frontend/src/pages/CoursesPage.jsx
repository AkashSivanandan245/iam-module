import { useState, useCallback } from 'react';
import { useAuth } from '../context/AuthContext';
import PageHeader from '../components/PageHeader';
import Toast from '../components/Toast';

const MOCK_COURSES = [
  { id: '1', title: 'Introduction to Java Spring Boot', description: 'Learn enterprise application development with Spring Boot 3, JPA, and REST APIs.', instructor: 'Rishi Gupta', duration: '8 weeks', level: 'Intermediate', enrolled: 124, category: 'Backend Development', status: 'PUBLISHED', thumbnail: '☕' },
  { id: '2', title: 'React & Vite — Modern Frontend', description: 'Build fast, scalable React applications with Vite, React Router, and component patterns.', instructor: 'Dron Garg', duration: '6 weeks', level: 'Beginner', enrolled: 89, category: 'Frontend Development', status: 'PUBLISHED', thumbnail: '⚛️' },
  { id: '3', title: 'PostgreSQL & Database Design', description: 'Master relational database design, indexing, query optimisation, and Flyway migrations.', instructor: 'Akash Sivanandan', duration: '5 weeks', level: 'Intermediate', enrolled: 67, category: 'Database', status: 'PUBLISHED', thumbnail: '🐘' },
  { id: '4', title: 'Cloud Deployment with Azure', description: 'Deploy containerised applications to Azure App Service, AKS, and configure CI/CD pipelines.', instructor: 'Neha Verma', duration: '7 weeks', level: 'Advanced', enrolled: 45, category: 'DevOps', status: 'PUBLISHED', thumbnail: '☁️' },
  { id: '5', title: 'System Design & Architecture', description: 'Design scalable distributed systems, microservices, caching strategies, and event-driven patterns.', instructor: 'Raj Mehta', duration: '10 weeks', level: 'Advanced', enrolled: 32, category: 'Architecture', status: 'DRAFT', thumbnail: '🏗️' },
  { id: '6', title: 'Security & IAM Fundamentals', description: 'Understand authentication, authorisation, JWT, OAuth2, and RBAC in enterprise systems.', instructor: 'Akash Sivanandan', duration: '4 weeks', level: 'Intermediate', enrolled: 78, category: 'Security', status: 'PUBLISHED', thumbnail: '🔐' },
];

const LEVELS = ['All', 'Beginner', 'Intermediate', 'Advanced'];

export default function CoursesPage() {
  const { can } = useAuth();
  const [search, setSearch] = useState('');
  const [level, setLevel] = useState('All');
  const [toast, setToast] = useState(null);

  const show = useCallback((message, icon = '✓') => setToast({ message, icon }), []);

  const filtered = MOCK_COURSES.filter((c) => {
    const matchSearch = c.title.toLowerCase().includes(search.toLowerCase()) || c.category.toLowerCase().includes(search.toLowerCase());
    const matchLevel = level === 'All' || c.level === level;
    const matchStatus = can('COURSE:CREATE') || c.status === 'PUBLISHED';
    return matchSearch && matchLevel && matchStatus;
  });

  return (
      <>
        <PageHeader
            title="Courses"
            subtitle={`${filtered.length} course${filtered.length === 1 ? '' : 's'} available`}
            actions={can('COURSE:CREATE') && (
                <button onClick={() => show('Course created successfully.', '📚')}>New course</button>
            )}
        />

        <div className="course-filters">
          <input className="course-search" placeholder="Search courses…" value={search} onChange={(e) => setSearch(e.target.value)} />
          <div className="level-tabs">
            {LEVELS.map((l) => (
                <button key={l} className={'tab' + (level === l ? ' tab-active' : '')} onClick={() => setLevel(l)}>{l}</button>
            ))}
          </div>
        </div>

        {filtered.length === 0 ? (
            <div className="coming-soon">
              <div className="coming-soon-icon">🔍</div>
              <h1>No courses found</h1>
              <p className="muted">Try a different search or filter.</p>
            </div>
        ) : (
            <div className="course-grid">
              {filtered.map((course) => (
                  <div key={course.id} className="course-card">
                    <div className="course-thumb">{course.thumbnail}</div>
                    <div className="course-body">
                      <div className="course-meta">
                        <span className="course-category">{course.category}</span>
                        {course.status === 'DRAFT' && <span className="badge status-suspended">DRAFT</span>}
                      </div>
                      <h3 className="course-title">{course.title}</h3>
                      <p className="course-desc muted">{course.description}</p>
                      <div className="course-footer">
                        <div className="course-stats">
                          <span className="muted small">👤 {course.instructor}</span>
                          <span className="muted small">⏱ {course.duration}</span>
                          <span className="muted small">📊 {course.level}</span>
                          <span className="muted small">👥 {course.enrolled} enrolled</span>
                        </div>
                        {can('ENROLLMENT:CREATE') && course.status === 'PUBLISHED' && (
                            <button className="enroll-btn" onClick={() => show(`Enrolled in "${course.title}" successfully.`, '🎓')}>
                              Enroll
                            </button>
                        )}
                        {can('COURSE:UPDATE') && (
                            <button className="ghost small-btn" onClick={() => show('Course updated successfully.', '✏️')}>Edit</button>
                        )}
                      </div>
                    </div>
                  </div>
              ))}
            </div>
        )}

        {toast && <Toast message={toast.message} icon={toast.icon} onClose={() => setToast(null)} />}
      </>
  );
}