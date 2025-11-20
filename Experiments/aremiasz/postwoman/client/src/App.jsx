import React, { useState, useEffect } from 'react';
import { Send, Server, History, Folder, Play, Trash2, Plus, Import } from 'lucide-react';
import RequestPanel from './components/RequestPanel';
import ResponsePanel from './components/ResponsePanel';
import MockServerConfig from './components/MockServerConfig';
import { ToastProvider, useToast } from './components/Toast';
import { importPostmanCollection } from './utils/postmanImport';
import './styles.css';

function AppContent() {
    const [activeTab, setActiveTab] = useState('request'); // request, mock
    const [sidebarTab, setSidebarTab] = useState('history'); // history, collections
    const [response, setResponse] = useState(null);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);
    const [collection, setCollection] = useState([]);
    const [history, setHistory] = useState([]);
    const [selectedRequest, setSelectedRequest] = useState(null);
    const [importLink, setImportLink] = useState('');

    const { addToast } = useToast();

    useEffect(() => {
        const savedHistory = localStorage.getItem('postwoman_history');
        if (savedHistory) {
            setHistory(JSON.parse(savedHistory));
        }
    }, []);

    const addToHistory = (req) => {
        const newHistory = [req, ...history].slice(0, 50); // Keep last 50
        setHistory(newHistory);
        localStorage.setItem('postwoman_history', JSON.stringify(newHistory));
    };

    const clearHistory = () => {
        setHistory([]);
        localStorage.removeItem('postwoman_history');
        addToast('History cleared', 'info');
    };

    const handleRequest = async (reqData) => {
        setLoading(true);
        setError(null);
        setResponse(null);

        const startTime = Date.now();

        try {
            const res = await fetch(reqData.url, {
                method: reqData.method,
                headers: reqData.headers,
                body: ['GET', 'HEAD'].includes(reqData.method) ? undefined : reqData.body
            });

            const endTime = Date.now();
            const time = endTime - startTime;

            const size = res.headers.get('content-length') || 0;
            const contentType = res.headers.get('content-type');

            let data;
            if (contentType && contentType.includes('application/json')) {
                data = await res.json();
            } else {
                data = await res.text();
            }

            const responseObj = {
                status: res.status,
                statusText: res.statusText,
                headers: res.headers,
                data,
                time,
                size
            };

            setResponse(responseObj);
            addToHistory({ ...reqData, date: new Date().toISOString() });

            if (res.ok) {
                addToast(`Request successful (${res.status})`, 'success');
            } else {
                addToast(`Request failed (${res.status})`, 'error');
            }

        } catch (e) {
            setError(e.message);
            addToast(`Error: ${e.message}`, 'error');
        } finally {
            setLoading(false);
        }
    };

    const handleImport = async () => {
        if (!importLink) return;
        try {
            const { requests, mocks } = await importPostmanCollection(importLink);
            setCollection(requests);

            if (mocks && mocks.length > 0) {
                if (confirm(`Found ${mocks.length} examples. Import them as mocks?`)) {
                    let count = 0;
                    for (const m of mocks) {
                        try {
                            await fetch('http://localhost:3000/_api/mocks', {
                                method: 'POST',
                                headers: { 'Content-Type': 'application/json' },
                                body: JSON.stringify({
                                    path: m.path,
                                    method: m.method,
                                    response: JSON.parse(m.response),
                                    status: m.status
                                })
                            });
                            count++;
                        } catch (e) {
                            console.error('Failed to import mock', m, e);
                        }
                    }
                    addToast(`Imported ${requests.length} requests and ${count} mocks`, 'success');
                } else {
                    addToast(`Imported ${requests.length} requests`, 'success');
                }
            } else {
                addToast(`Imported ${requests.length} requests`, 'success');
            }

            setImportLink('');
        } catch (e) {
            console.error(e);
            addToast('Failed to import collection', 'error');
        }
    };

    const loadRequest = (req) => {
        setSelectedRequest(req);
        setActiveTab('request');
    };

    return (
        <div className="app-container">
            <div className="sidebar">
                <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', marginBottom: '1rem' }}>
                    <div style={{ width: '32px', height: '32px', background: 'var(--accent-color)', borderRadius: '8px', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
                        <Send size={20} color="white" />
                    </div>
                    <h2 style={{ fontSize: '1.2rem', margin: 0 }}>Postwoman</h2>
                </div>

                <div className="tabs" style={{ marginBottom: '0' }}>
                    <div className={`tab ${activeTab === 'request' ? 'active' : ''}`} onClick={() => setActiveTab('request')}>
                        <Send size={16} /> Request
                    </div>
                    <div className={`tab ${activeTab === 'mock' ? 'active' : ''}`} onClick={() => setActiveTab('mock')}>
                        <Server size={16} /> Mock Server
                    </div>
                </div>

                <div style={{ borderTop: '1px solid var(--border-color)', paddingTop: '1rem', flex: 1, display: 'flex', flexDirection: 'column', minHeight: 0 }}>
                    <div className="tabs">
                        <div className={`tab ${sidebarTab === 'history' ? 'active' : ''}`} onClick={() => setSidebarTab('history')}>
                            <History size={16} /> History
                        </div>
                        <div className={`tab ${sidebarTab === 'collections' ? 'active' : ''}`} onClick={() => setSidebarTab('collections')}>
                            <Folder size={16} /> Collections
                        </div>
                    </div>

                    <div style={{ flex: 1, overflowY: 'auto' }}>
                        {sidebarTab === 'history' && (
                            <>
                                {history.length === 0 && <p style={{ fontSize: '0.9rem', textAlign: 'center', marginTop: '2rem' }}>No history yet</p>}
                                {history.length > 0 && (
                                    <div style={{ display: 'flex', justifyContent: 'flex-end', marginBottom: '0.5rem' }}>
                                        <button className="icon-btn" onClick={clearHistory} title="Clear History">
                                            <Trash2 size={14} />
                                        </button>
                                    </div>
                                )}
                                {history.map((req, i) => (
                                    <div key={i} className="list-item" onClick={() => loadRequest(req)}>
                                        <span className={`method-badge ${req.method}`}>{req.method}</span>
                                        <span style={{ fontSize: '0.85rem', overflow: 'hidden', textOverflow: 'ellipsis', whiteSpace: 'nowrap' }}>{req.url}</span>
                                    </div>
                                ))}
                            </>
                        )}

                        {sidebarTab === 'collections' && (
                            <>
                                <div className="input-group" style={{ marginBottom: '1rem' }}>
                                    <input
                                        placeholder="Postman Share Link"
                                        value={importLink}
                                        onChange={(e) => setImportLink(e.target.value)}
                                        style={{ flex: 1, minWidth: 0 }}
                                    />
                                    <button className="icon-btn" onClick={handleImport} title="Import">
                                        <Import size={16} />
                                    </button>
                                </div>

                                {collection.length === 0 && <p style={{ fontSize: '0.9rem', textAlign: 'center', marginTop: '2rem' }}>No collection imported</p>}
                                {collection.map((req, i) => (
                                    <div key={i} className="list-item" onClick={() => loadRequest(req)}>
                                        <span className={`method-badge ${req.method}`}>{req.method}</span>
                                        <span style={{ fontSize: '0.85rem', overflow: 'hidden', textOverflow: 'ellipsis', whiteSpace: 'nowrap' }}>{req.name}</span>
                                    </div>
                                ))}
                            </>
                        )}
                    </div>
                </div>
            </div>

            <div className="main-content">
                {activeTab === 'request' && (
                    <>
                        <RequestPanel onRequest={handleRequest} initialRequest={selectedRequest} />
                        <ResponsePanel response={response} loading={loading} error={error} />
                    </>
                )}

                {activeTab === 'mock' && (
                    <MockServerConfig />
                )}
            </div>
        </div>
    );
}

function App() {
    return (
        <ToastProvider>
            <AppContent />
        </ToastProvider>
    );
}

export default App;
