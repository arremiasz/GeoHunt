import React, { useState, useEffect } from 'react';

const MockServerConfig = () => {
  const [mocks, setMocks] = useState([]);
  const [path, setPath] = useState('');
  const [method, setMethod] = useState('GET');
  const [responseBody, setResponseBody] = useState('{}');
  const [status, setStatus] = useState(200);
  const [editingMock, setEditingMock] = useState(null);

  const MOCK_API = 'http://localhost:3000/_api/mocks';

  const fetchMocks = async () => {
    try {
      const res = await fetch(MOCK_API);
      const data = await res.json();
      if (Array.isArray(data)) {
        setMocks(data);
      }
    } catch (e) {
      console.error('Failed to fetch mocks', e);
    }
  };

  useEffect(() => {
    fetchMocks();
  }, []);

  const handleSave = async () => {
    try {
      let parsedBody = {};
      try {
        parsedBody = JSON.parse(responseBody);
      } catch (e) {
        alert('Invalid JSON in Response Body');
        return;
      }

      // If we are editing and the path or method changed, delete the old one first
      if (editingMock && (editingMock.path !== path || editingMock.method !== method)) {
        await fetch(MOCK_API, {
          method: 'DELETE',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({ path: editingMock.path, method: editingMock.method })
        });
      }

      await fetch(MOCK_API, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          path,
          method,
          response: parsedBody,
          status: parseInt(status)
        })
      });

      fetchMocks();
      resetForm();
    } catch (e) {
      alert('Failed to save mock');
    }
  };

  const deleteMock = async (m, e) => {
    e.stopPropagation(); // Prevent triggering edit
    if (!confirm('Delete this mock?')) return;
    try {
      await fetch(MOCK_API, {
        method: 'DELETE',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ path: m.path, method: m.method })
      });
      fetchMocks();
      if (editingMock && editingMock.path === m.path && editingMock.method === m.method) {
        resetForm();
      }
    } catch (e) {
      alert('Failed to delete mock');
    }
  };

  const startEdit = (m) => {
    setPath(m.path);
    setMethod(m.method);
    setStatus(m.status);
    setResponseBody(JSON.stringify(m.response, null, 2));
    setEditingMock(m);
  };

  const resetForm = () => {
    setPath('');
    setMethod('GET');
    setStatus(200);
    setResponseBody('{}');
    setEditingMock(null);
  };

  return (
    <div className="panel">
      <h3>Mock Server Configuration</h3>
      <p style={{ fontSize: '0.8rem', color: 'var(--text-secondary)' }}>
        Make sure the mock server is running on port 3000. Click a mock to edit.
      </p>

      <div className="input-group" style={{ flexDirection: 'column' }}>
        <div style={{ display: 'flex', gap: '0.5rem' }}>
          <select value={method} onChange={e => setMethod(e.target.value)}>
            <option>GET</option>
            <option>POST</option>
            <option>PUT</option>
            <option>DELETE</option>
          </select>
          <input
            placeholder="/path/to/mock"
            value={path}
            onChange={e => setPath(e.target.value)}
            style={{ flex: 1 }}
          />
          <input
            type="number"
            placeholder="Status"
            value={status}
            onChange={e => setStatus(e.target.value)}
            style={{ width: '80px' }}
          />
        </div>
        <textarea
          placeholder='Response Body (JSON)'
          value={responseBody}
          onChange={e => setResponseBody(e.target.value)}
          rows={3}
        />
        <div style={{ display: 'flex', gap: '0.5rem' }}>
          <button onClick={handleSave}>{editingMock ? 'Update Mock' : 'Add Mock'}</button>
          {editingMock && <button className="secondary" onClick={resetForm}>Cancel Edit</button>}
        </div>
      </div>

      <div style={{ marginTop: '1rem' }}>
        <h4>Active Mocks</h4>
        {mocks.length === 0 && <p>No active mocks.</p>}
        {mocks.map((m, i) => (
          <div key={i} onClick={() => startEdit(m)} style={{
            display: 'flex',
            justifyContent: 'space-between',
            alignItems: 'center',
            padding: '0.5rem',
            backgroundColor: editingMock === m ? 'rgba(187, 134, 252, 0.1)' : 'var(--bg-tertiary)',
            border: editingMock === m ? '1px solid var(--accent-color)' : '1px solid transparent',
            marginBottom: '0.5rem',
            borderRadius: '4px',
            cursor: 'pointer'
          }}>
            <div>
              <span className="status-badge" style={{ marginRight: '0.5rem' }}>{m.method}</span>
              <span>{m.path}</span>
              <span style={{ marginLeft: '0.5rem', color: 'var(--text-secondary)' }}>({m.status})</span>
            </div>
            <button className="secondary" onClick={(e) => deleteMock(m, e)} style={{ padding: '0.25rem 0.5rem' }}>Delete</button>
          </div>
        ))}
      </div>
    </div>
  );
};

export default MockServerConfig;