import React, { useState, useEffect } from 'react';

const RequestPanel = ({ onRequest, initialRequest }) => {
    const [method, setMethod] = useState('GET');
    const [url, setUrl] = useState('');
    const [headers, setHeaders] = useState('{}');
    const [body, setBody] = useState('');
    const [activeTab, setActiveTab] = useState('params'); // params, headers, body

    useEffect(() => {
        if (initialRequest) {
            setMethod(initialRequest.method || 'GET');
            setUrl(initialRequest.url || '');
            setHeaders(JSON.stringify(initialRequest.headers || {}, null, 2));
            setBody(initialRequest.body || '');
        }
    }, [initialRequest]);

    const handleSend = () => {
        let parsedHeaders = {};
        try {
            parsedHeaders = JSON.parse(headers);
        } catch (e) {
            alert('Invalid JSON in Headers');
            return;
        }

        onRequest({
            method,
            url,
            headers: parsedHeaders,
            body
        });
    };

    return (
        <div className="panel request-panel">
            <div className="input-group">
                <select value={method} onChange={(e) => setMethod(e.target.value)} style={{ width: '100px' }}>
                    <option value="GET">GET</option>
                    <option value="POST">POST</option>
                    <option value="PUT">PUT</option>
                    <option value="DELETE">DELETE</option>
                    <option value="PATCH">PATCH</option>
                </select>
                <input
                    type="text"
                    placeholder="Enter URL"
                    value={url}
                    onChange={(e) => setUrl(e.target.value)}
                    style={{ flex: 1 }}
                />
                <button onClick={handleSend}>Send</button>
            </div>

            <div className="tabs">
                <div className={`tab ${activeTab === 'headers' ? 'active' : ''}`} onClick={() => setActiveTab('headers')}>Headers</div>
                <div className={`tab ${activeTab === 'body' ? 'active' : ''}`} onClick={() => setActiveTab('body')}>Body</div>
            </div>

            {activeTab === 'headers' && (
                <textarea
                    placeholder='{ "Content-Type": "application/json" }'
                    value={headers}
                    onChange={(e) => setHeaders(e.target.value)}
                    rows={5}
                    style={{ width: '100%' }}
                />
            )}

            {activeTab === 'body' && (
                <textarea
                    placeholder='Request Body (JSON)'
                    value={body}
                    onChange={(e) => setBody(e.target.value)}
                    rows={5}
                    style={{ width: '100%' }}
                />
            )}
        </div>
    );
};

export default RequestPanel;
